package com.dreamtown.danarresidence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dreamtown.danarresidence.entity.MWilayah;
import com.dreamtown.danarresidence.entity.Property;

public interface PropertyRepository extends JpaRepository<Property, Integer> {

    Optional<Property> findOneByPropertyName(String propertyName);

    

    List<Property> findByWilayah(MWilayah wilayah);
}
