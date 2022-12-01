package com.rashad.loginwithsocial.service;

import com.rashad.loginwithsocial.repository.StadiumRepository;
import com.rashad.loginwithsocial.service.impl.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StadiumServiceImpl implements StadiumService {

    private final StadiumRepository stadiumRepository;


}
