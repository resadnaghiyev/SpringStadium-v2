package com.rashad.loginwithsocial.service.impl;

import com.rashad.loginwithsocial.model.GoogleLogin;
import com.rashad.loginwithsocial.model.JwtResponse;
import com.rashad.loginwithsocial.model.LoginRequest;
import com.rashad.loginwithsocial.model.RegisterRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface AuthService {

    Boolean checkEmailExist(String email);

    Boolean checkUsernameExist(String username);

    Map<String, Object> register(RegisterRequest request);

    String resendToken(String token);

    String confirmToken(String token);

    JwtResponse loginUser(LoginRequest request);

    JwtResponse loginWithGoogle(GoogleLogin request);

    JwtResponse refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException;

}
