package com.rashad.loginwithsocial.repository;

import com.rashad.loginwithsocial.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByPoint(Integer point);

    Rating findByStadiumIdAndUserId(Long stadiumId, Long userId);

    Rating findByStadiumIdAndUser_Username(Long stadiumId, String username);
}
