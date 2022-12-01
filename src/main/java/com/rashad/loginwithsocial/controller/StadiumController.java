package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.entity.Stadium;
import com.rashad.loginwithsocial.model.CustomResponse;
import com.rashad.loginwithsocial.repository.StadiumRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stadium")
@RequiredArgsConstructor
@Tag(name = "5. Stadium CRUD")
public class StadiumController {

    private final StadiumRepository stadiumRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getStadium(@PathVariable("id") Long stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow();
        return new ResponseEntity<>(new CustomResponse(true, stadium, "", null), HttpStatus.OK);
    }

}
