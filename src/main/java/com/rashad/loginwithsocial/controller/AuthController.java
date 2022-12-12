package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.model.*;
import com.rashad.loginwithsocial.service.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "1. Login and Register")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @Operation(
            summary = "Check email exist",
            description = "For checking if email exist you have to send request with email",
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @GetMapping("/email")
    public ResponseEntity<?> checkEmailExist(@RequestParam("check") String email) {
        Boolean exist = authServiceImpl.checkEmailExist(email);
        String message = "Email exist: " + exist;
        return new ResponseEntity<>(new CustomResponse(true, null, message, null), HttpStatus.OK);
    }

    @Operation(
            summary = "Check username exist",
            description = "For checking if username exist you have to send request with username",
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @GetMapping("/username")
    public ResponseEntity<?> checkUsernameExist(@RequestParam("check") String username) {
        Boolean exist = authServiceImpl.checkUsernameExist(username);
        String message = "Username exist: " + exist;
        return new ResponseEntity<>(new CustomResponse(true, null, message, null), HttpStatus.OK);
    }

    @Operation(
            summary = "Register",
            description = "For the register you have to send body with example like shown below",
            responses = {@ApiResponse(responseCode = "201", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        Map<String, Object> body = authServiceImpl.register(request);
        return new ResponseEntity<>(new CustomResponse(
                true, body.get("user"), body.get("message").toString(), null), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Resend confirmation link",
            description = "For the resend confirmation token you have to send token parameter " +
                    "with the value of old expired token which is sent to your email",
            responses = {@ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @GetMapping("/register/resend")
    public ResponseEntity<?> resendToken(@RequestParam("token") String token) {
        String message = authServiceImpl.resendToken(token);
        return new ResponseEntity<>(new CustomResponse(true, null, message, null), HttpStatus.OK);
    }

    @Operation(
            summary = "Confirm email",
            description = "For the confirm your email you have to send token parameter " +
                    "with the value which is sent to your email or just click link in your email",
            responses = {@ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @GetMapping("/register/confirm")
    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
        String message = authServiceImpl.confirmToken(token);
        return new ResponseEntity<>(new CustomResponse(true, null, message, null), HttpStatus.OK);
    }

    @Operation(
            summary = "Login",
            description = "For the login you have to send body with example like shown below",
            responses = {@ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        JwtResponse tokens = authServiceImpl.loginUser(request);
        return new ResponseEntity<>(new CustomResponse(true, tokens, "", null), HttpStatus.OK);
    }

    @Hidden
    @PostMapping("/google/login")
    public ResponseEntity<?> google(@RequestBody GoogleLogin request) {
        JwtResponse tokens = authServiceImpl.loginWithGoogle(request);
        return new ResponseEntity<>(new CustomResponse(true, tokens, "", null), HttpStatus.OK);
    }

    @Operation(
            summary = "Get new access token",
            description = "For the getting new access token you have to" +
                    " send Authorization Header with refresh token",
            responses = {@ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))},
            security = {@SecurityRequirement(name = "BearerJwt")}
    )
    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        JwtResponse jwt = authServiceImpl.refreshToken(request, response);
        if (jwt.getAccessToken() != null) {
            return new ResponseEntity<>(new CustomResponse(true, jwt, "", null), HttpStatus.OK);
        }
        String error = "refresh token is invalid or missing";
        return new ResponseEntity<>(new CustomResponse(false, null, "", error), UNAUTHORIZED);
    }
}

