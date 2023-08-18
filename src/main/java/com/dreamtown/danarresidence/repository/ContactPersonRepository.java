package com.dreamtown.danarresidence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dreamtown.danarresidence.entity.ContactPerson;

public interface ContactPersonRepository extends JpaRepository<ContactPerson, Integer>{
    
}
