package com.dreamtown.danarresidence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dreamtown.danarresidence.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
    
}
