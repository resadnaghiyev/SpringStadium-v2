package com.rashad.loginwithsocial.repository;

import com.rashad.loginwithsocial.entity.ComPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComPhoneRepository extends JpaRepository<ComPhone, Long> {
}
