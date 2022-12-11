package com.rashad.loginwithsocial.service.impl;

import com.rashad.loginwithsocial.entity.Stadium;
import com.rashad.loginwithsocial.model.RatingRequest;

import java.util.List;

public interface StadiumService {

    Stadium getStadiumFromId(Long stadiumId);

    List<Stadium> getAllStadiums();

    String addRatingToStadium(Long stadiumId, RatingRequest request);

    String getStadiumRating(Long stadiumId);
}
