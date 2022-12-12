package com.rashad.loginwithsocial.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rashad.loginwithsocial.email.EmailSender;
import com.rashad.loginwithsocial.service.impl.AuthService;
import com.rashad.loginwithsocial.validator.EmailValidator;
import com.rashad.loginwithsocial.entity.ConfirmationToken;
import com.rashad.loginwithsocial.entity.User;
import com.rashad.loginwithsocial.jwt.JwtUtils;
import com.rashad.loginwithsocial.model.*;
import com.rashad.loginwithsocial.repository.UserRepository;
import com.rashad.loginwithsocial.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${base.url}")
    private String baseUrl;

    private final JwtUtils jwtUtils;
    private final EmailSender emailSender;
    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final AuthenticationManager authenticationManager;
    private final ConfirmTokenServiceImpl confirmTokenServiceImpl;

    @Override
    public Boolean checkEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Boolean checkUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Map<String, Object> register(RegisterRequest request) {
        if (!emailValidator.test(request.getEmail())) {
            throw new IllegalStateException("email: Email not valid");
        }
        if (!passwordValidator.test(request.getPassword())) {
            throw new IllegalStateException("password: Password not valid");
        }
        Map<String, Object> body = userService.signUpUser(
                new User(
                        request.getName(),
                        request.getSurname(),
                        request.getEmail(),
                        request.getPhone(),
                        request.getUsername(),
                        request.getPassword()
                )
        );
        String link = baseUrl + "/api/v1/user/register/confirm?token=" + body.get("token");
        emailSender.send(request.getEmail(), buildEmail(request.getName(), link));
        body.put("message", "Confirmation token send to email: " + request.getEmail());
        return body;
    }

    @Override
    public String resendToken(String token) {
        ConfirmationToken oldToken = confirmTokenServiceImpl.getToken(token).orElseThrow(() ->
                new IllegalStateException("token: Token is not valid"));
        String newToken = userService.createToken(oldToken.getUser());
        String link = baseUrl + "/api/v1/user/register/confirm?token=" + newToken;
        emailSender.send(oldToken.getUser().getEmail(), buildEmail(oldToken.getUser().getName(), link));
        return "Confirmation token send to email: " + oldToken.getUser().getEmail();
    }

    @Override
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmTokenServiceImpl.getToken(token)
                .orElseThrow(() -> new IllegalStateException("token: Token is not valid"));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email: Email already confirmed");
        }
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token: Token expired");
        }
        confirmTokenServiceImpl.setConfirmedAt(token);
        userService.setActiveUser(confirmationToken.getUser().getUsername());
        return "User confirmed";
    }

    @Override
    public JwtResponse loginUser(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));
        return getJwtTokens(request.getUsername());
    }

    @Override
    public JwtResponse loginWithGoogle(GoogleLogin request) {
        if (!emailValidator.test(request.getEmail())) {
            throw new IllegalStateException("email: Email not valid");
        }
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            user = userService.registerGoogle(convertTo(request));
        }
        return getJwtTokens(user.getUsername());
    }

    public JwtResponse getJwtTokens(String username) {
        UserDetails userDetails = userService.loadUserByUsername(username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalStateException("user: Not found"));
        String access_token = jwtUtils.generateAccessToken(userDetails);
        String refresh_token = jwtUtils.generateRefreshToken(userDetails);
        return new JwtResponse(access_token, refresh_token, user);
    }

    @Override
    public JwtResponse refreshToken(HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                String refresh_token = authorization.substring("Bearer ".length());
                String username = jwtUtils.getUsernameFromToken(refresh_token);
                UserDetails userDetails = userService.loadUserByUsername(username);
                if (jwtUtils.validateToken(refresh_token, userDetails) &&
                        !jwtUtils.isAccessToken(refresh_token)) {
                    String access_token = jwtUtils.generateAccessToken(userDetails);
                    refresh_token = jwtUtils.generateRefreshToken(userDetails);
                    User user = userRepository.findByUsername(username).orElseThrow(() ->
                            new IllegalStateException("user: Not found"));
                    return new JwtResponse(access_token, refresh_token, user);
                }
            } catch (Exception exception) {
                response.setStatus(UNAUTHORIZED.value());
                Map<String, Object> body = new HashMap<>();
                Map<String, List<String>> errorMap = new HashMap<>();
                errorMap.put("jwt", List.of(exception.getMessage()));
                body.put("success", false);
                body.put("data", null);
                body.put("message", "");
                body.put("error", errorMap);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), body);
            }
        }
        return new JwtResponse();
    }

    private User convertTo(GoogleLogin request) {
        String email = request.getEmail();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        return new User(
                firstName,
                lastName,
                email,
                null,
                generateUsername(email),
                generatePassword()
        );
    }

    private String generateUsername(String email) {
        String[] username = email.split("@");
        return username[0];
    }

    private String generatePassword() {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[8];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for(int i = 4; i< 8; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return new String(password);
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}



