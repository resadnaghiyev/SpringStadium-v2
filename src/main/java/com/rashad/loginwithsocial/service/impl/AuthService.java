package com.rashad.loginwithsocial.service.impl;

import com.rashad.loginwithsocial.model.GoogleLogin;
import com.rashad.loginwithsocial.model.JwtResponse;
import com.rashad.loginwithsocial.model.LoginRequest;
import com.rashad.loginwithsocial.model.RegisterRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AuthService {

    String register(RegisterRequest request);

    String resendToken(String token);

    String confirmToken(String token);

    Map<String, List<String>> loginUser(LoginRequest request);

    Map<String, List<String>> loginWithGoogle(GoogleLogin request);

    JwtResponse refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException;
}
