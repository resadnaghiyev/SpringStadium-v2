package com.rashad.loginwithsocial.service.impl;

import com.rashad.loginwithsocial.entity.Company;
import com.rashad.loginwithsocial.entity.Stadium;

import java.util.List;

public interface StadiumService {

    Stadium getStadiumFromId(Long stadiumId);

    List<Stadium> getAllStadiums();
}
