package com.rashad.loginwithsocial.service;

import com.rashad.loginwithsocial.entity.Stadium;
import com.rashad.loginwithsocial.repository.StadiumRepository;
import com.rashad.loginwithsocial.service.impl.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StadiumServiceImpl implements StadiumService {

    private final StadiumRepository stadiumRepository;


    @Override
    public Stadium getStadiumFromId(Long stadiumId) {
        return stadiumRepository.findById(stadiumId).orElseThrow(() ->
                new IllegalStateException("Stadium with id: " + stadiumId + " not found"));
    }

    @Override
    public List<Stadium> getAllStadiums() {
        return stadiumRepository.findAll();
    }
}
