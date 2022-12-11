package com.rashad.loginwithsocial.service;

import com.rashad.loginwithsocial.entity.Rating;
import com.rashad.loginwithsocial.entity.Stadium;
import com.rashad.loginwithsocial.entity.User;
import com.rashad.loginwithsocial.model.RatingRequest;
import com.rashad.loginwithsocial.repository.RatingRepository;
import com.rashad.loginwithsocial.repository.StadiumRepository;
import com.rashad.loginwithsocial.repository.UserRepository;
import com.rashad.loginwithsocial.service.impl.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StadiumServiceImpl implements StadiumService {

    private final StadiumRepository stadiumRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    @Override
    public Stadium getStadiumFromId(Long stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(() ->
                new IllegalStateException("stadium: Stadium with id: " + stadiumId + " not found"));
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (auth != "anonymousUser") {
            UserDetails principal = (UserDetails) auth;
            User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() ->
                    new IllegalStateException("user: Not found"));
            Rating rating = ratingRepository.findByStadiumIdAndUserId(stadiumId, user.getId());
            if (rating != null) {
                stadium.setUserRating(rating.getPoint());
            }
        }
        return stadium;
    }

    @Override
    public List<Stadium> getAllStadiums() {
        return stadiumRepository.findAll();
    }

    @Override
    public String addRatingToStadium(Long stadiumId, RatingRequest request) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(() ->
                new IllegalStateException("stadium: Stadium with id: " + stadiumId + " not found"));
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() ->
                new IllegalStateException("user: Not found"));
        Rating rating = ratingRepository.findByStadiumIdAndUserId(stadiumId, user.getId());
        if (rating == null) {
            rating = new Rating(request.getRating());
        }
        rating.setPoint(request.getRating());
        rating.setStadium(stadium);
        rating.setUser(user);
        ratingRepository.save(rating);
        int countRatings = stadium.getRatings().size();
        int sumRatings = 0;
        for (Rating r : stadium.getRatings()) {
            sumRatings += r.getPoint();
        }
        double overall = (double) sumRatings / countRatings;
        overall = Math.round(overall*10.0) / 10.0;
        stadium.setRating(overall);
        stadiumRepository.save(stadium);
        return "Rating: " + request.getRating() + " added to stadium: " + stadium.getName();
    }

    @Override
    public String getStadiumRating(Long stadiumId) {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (auth != "anonymousUser") {
            UserDetails principal = (UserDetails) auth;
            User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() ->
                    new IllegalStateException("user: Not found"));
            Rating rating = ratingRepository.findByStadiumIdAndUserId(stadiumId, user.getId());
            if (rating != null) {
                return rating.getPoint().toString();
            }
        }
        return null;
    }
}
