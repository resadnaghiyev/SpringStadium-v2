package com.rashad.loginwithsocial.repository;

import com.rashad.loginwithsocial.entity.StdImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StdImageRepository extends JpaRepository<StdImage, Long> {

}
