package com.example.demoauthentication.repository;

import com.example.demoauthentication.entity.Role;
import com.example.demoauthentication.enums.RoleEnums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByRoleEnums(RoleEnums roleEnums);
}
