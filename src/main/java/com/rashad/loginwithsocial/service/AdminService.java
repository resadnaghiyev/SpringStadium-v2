package com.rashad.loginwithsocial.service;

import com.rashad.loginwithsocial.entity.User;
import com.rashad.loginwithsocial.model.RegisterRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserServiceImpl userService;

    public String createUser(RegisterRequest request) {
        User user = new User(
                request.getName(),
                request.getSurname(),
                request.getEmail(),
                request.getPhone(),
                request.getUsername(),
                request.getPassword());
        userService.saveUser(user);
        userService.enableUser(request.getUsername());
        return "User created successfully";
    }
}
