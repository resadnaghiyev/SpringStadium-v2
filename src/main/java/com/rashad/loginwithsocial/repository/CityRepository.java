package com.rashad.loginwithsocial.repository;

import com.rashad.loginwithsocial.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    City findByName(String name);
}
