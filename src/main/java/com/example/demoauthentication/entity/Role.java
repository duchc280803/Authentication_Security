package com.example.demoauthentication.entity;

import com.example.demoauthentication.enums.RoleEnums;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnums roleEnums;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<Account> accountList;
}
