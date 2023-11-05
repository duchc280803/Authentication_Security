package com.example.demoauthentication.service.impl;

import com.example.demoauthentication.entity.Account;
import com.example.demoauthentication.model.UserCustomDetail;
import com.example.demoauthentication.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
