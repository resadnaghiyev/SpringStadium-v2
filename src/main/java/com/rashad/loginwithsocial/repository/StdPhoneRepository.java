package com.rashad.loginwithsocial.repository;

import com.rashad.loginwithsocial.entity.StdPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StdPhoneRepository extends JpaRepository<StdPhone, Long> {
}
