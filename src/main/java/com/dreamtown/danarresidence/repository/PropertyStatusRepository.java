package com.dreamtown.danarresidence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dreamtown.danarresidence.entity.PropertyStatus;

public interface PropertyStatusRepository extends JpaRepository<PropertyStatus, Integer>{
    
}
