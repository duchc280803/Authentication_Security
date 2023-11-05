# Hello xin chào mọi người

# Đây là nơi mình hướng dẫn các bạn sử dụng spring security 6 để cấu hình Authentication và Authorization

- Spring Security là một framework mạnh mẽ cho việc quản lý và bảo vệ các tài nguyên trong ứng dụng Spring
- Spring Security hỗ trợ nhiều phương thức xác thực khác nhau như xác thực dựa trên form, xác thực dựa trên token, xác
  thực dựa trên mã JWT, và nhiều cơ chế xác thực khác.
- Ngoài ra, Spring Security cũng cung cấp các tính năng bảo mật khác như bảo vệ khỏi các cuộc tấn công CSRF (Cross-Site
  Request Forgery), SQL injection, và nhiều lỗ hổng bảo mật khác.
- Spring Security là một phần quan trọng của các ứng dụng Spring nói chung và đóng vai trò quan trọng trong việc bảo vệ
  thông tin và tài nguyên của ứng dụng khỏi các mối đe dọa bảo mật.

1. Đầu tiên hãy tạo cho mình môt dự án spring boot nhé
2. Tiếp theo hãy vào file POM thêm những thư viện cần thiết để dùng

Để hiển thị nội dung của một tệp pom.xml trong tệp README.md, bạn có thể sử dụng ba dấu backticks (```) trước và sau đoạn mã XML.

Ví dụ:
```xml
    <dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
		<version>8.0.28</version>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-jpa</artifactId>
	</dependency>
    <dependency>
	    <groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-security</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.security</groupId>
		<artifactId>spring-security-test</artifactId>
		<scope>test</scope>
	</dependency>
	<dependency>
		<groupId>io.jsonwebtoken</groupId>
		<artifactId>jjwt-api</artifactId>
		<version>0.11.5</version>
	</dependency>
	<dependency>
		<groupId>io.jsonwebtoken</groupId>
		<artifactId>jjwt-impl</artifactId>
		<version>0.11.5</version>
		<scope>runtime</scope>
	</dependency>
	<dependency>
		<groupId>io.jsonwebtoken</groupId>
		<artifactId>jjwt-jackson</artifactId>
		<version>0.11.5</version>
		<scope>runtime</scope>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-validation</artifactId>
	</dependency>
```
3. Sau đó hãy tạo ra những package cần thiết để sử dụng(Entity, Controller, Service, Model, Config, Repository, Util)
4. Đầu tiên hãy tạo cho mình một UserCustomDetail trong model nhé
```java

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCustomDetail implements UserDetails {

    // Đây là class entity
    private Account account;

    // đây là nơi để xác thực vai trò người dùng nó sẽ trả về cho mình vai trò
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(account.getRole().getRoleEnums().name()));
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```
5. Sau đó hãy tạo một UserDetailServiceImpl trong service
```java
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> findByUser = accountRepository.findByUsername(username);
        return UserCustomDetail.builder().account(findByUser.get()).build();
    }

}
```
UserDetailsServiceđược sử dụng DaoAuthenticationProviderđể truy xuất tên người dùng, mật khẩu và các thuộc tính khác để xác thực bằng tên người dùng và mật khẩu
6. Tiếp theo tạo ApplicationConfig trong package config nhé
```java
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailServiceImpl();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
```
Mình sẽ giải thích ngắn gọn nhất để mọi người dễ hiểu
Đầu tiên là passwordEncoder() nó dùng để mã hóa pasword
userDetailsService() dùng để Truy Xuất Người Dùng
authenticationProvider() xác thực người dùng
authenticationManager(AuthenticationConfiguration configuration) Trả về một đối tượng người dùng

7. Ở đây mình sẽ cần phải cấu hình Jwt Token và Bộ lọc trước
``` java

@Service
public class JwtService {

    private static final String SECRET_KEY = "e82c73692e6fa99b1770cfd6605bfc5b9ec3a12b362d9de5459a2612191497c4";

    // Trích xuất tên người dùng từ một chuỗi JWT.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Giải quyết thông tin được trích xuất
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Tạo một JWT dựa trên thông tin người dùng
    public String generateToken(UserCustomDetail userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Tạo một chuỗi JWT dựa trên các thông tin người dùng
    public String generateToken(
            Map<String, Object> extraClaims,
            UserCustomDetail userCustomDetails
    ) {
        return buildToken(extraClaims, userCustomDetails);
    }

    // Xây dựng chuỗi JWT để bổ xung cho các method khác
    private String buildToken(
            Map<String, Object> extraClaims,
            UserCustomDetail userCustomDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userCustomDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Kiểm tra xem một JWT có hợp lệ không
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Kiểm tra token hết hạn hay chưa
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    // Trích xuất thời điểm hết hạn của JWT.
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    // Trích xuất tất cả các thông tin từ JWT
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //  Trả về một khóa dùng để ký JWT dựa trên một chuỗi secret key.
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```
``` java

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailServiceImpl userDetailService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserCustomDetail userCustomDetail = (UserCustomDetail) this.userDetailService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userCustomDetail)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userCustomDetail,
                        null,
                        userCustomDetail.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```
Nói chung phía trên là JwtAuthenticationFilter là một bộ lọc trong Spring Security được sử dụng để xác thực người dùng dựa trên chuỗi JWT được gửi trong header của request.
8. Tạo một class SecurityConfig trong file Config nhé
``` java
@Configuration // định nghĩa 1 hoặc nhiều bean
@EnableWebSecurity // được sử dụng khi cấu hình security
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth // ủy quyền
                        .requestMatchers("/authentication/**").permitAll() // ai cũng có thể truy cập
                        .requestMatchers("/api/v1/product/banana").permitAll() // ai cũng có thể truy cập
                        .requestMatchers("/api/v1/product/apple").hasAnyAuthority(RoleEnums.USER.name()) // Chỉ user
                        .requestMatchers("/api/v1/product/fish").hasAnyAuthority(RoleEnums.ADMIN.name())// Chỉ ADMIN
                        .requestMatchers("/api/v1/product/sion").hasAnyAuthority(RoleEnums.MANAGER.name())// Chỉ MANAGER
                        .anyRequest()
                        .authenticated())
                .sessionManagement(
                        sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // quản lý các phiên
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
```
# OK như vậy là cấu hình xong security mọi người có thể xem thêm đăng ký , đăng nhập ở code trên nhé
