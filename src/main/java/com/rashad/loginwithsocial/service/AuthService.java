package com.rashad.loginwithsocial.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rashad.loginwithsocial.email.EmailSender;
import com.rashad.loginwithsocial.email.EmailValidator;
import com.rashad.loginwithsocial.entity.ConfirmationToken;
import com.rashad.loginwithsocial.entity.User;
import com.rashad.loginwithsocial.jwt.JwtUtils;
import com.rashad.loginwithsocial.model.*;
import com.rashad.loginwithsocial.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final EmailSender emailSender;
    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenService confirmationTokenService;

    public String register(RegisterRequest request) {
        if (!emailValidator.test(request.getEmail())) {
            throw new IllegalStateException("email not valid");
        }
        if (!passwordValidator.test(request.getPassword())) {
            throw new IllegalStateException("password not valid");
        }
        String token = userService.signUpUser(
                new User(
                        request.getName(),
                        request.getSurname(),
                        request.getEmail(),
                        request.getPhone(),
                        request.getUsername(),
                        request.getPassword()
                )
        );
        String link = "https://stadiumv1.herokuapp.com/api/v1/user/register/confirm?token=" + token;
        emailSender.send(request.getEmail(), buildEmail(request.getName(), link));
        return "Confirmation token send to email: " + request.getEmail();
    }

    public String resendToken(String token) {
        ConfirmationToken oldToken = confirmationTokenService.getToken(token).orElseThrow(() ->
                new IllegalStateException("Token is not valid"));
        String newToken = userService.createToken(oldToken.getUser());
        String link = "https://stadiumv1.herokuapp.com/api/v1/user/register/confirm?token=" + newToken;
        emailSender.send(oldToken.getUser().getEmail(), buildEmail(oldToken.getUser().getName(), link));
        return "Confirmation token send to email: " + oldToken.getUser().getEmail();
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not valid"));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }
        confirmationTokenService.setConfirmedAt(token);
        userService.enableUser(confirmationToken.getUser().getUsername());
        return "User confirmed";
    }

    public Map<String, List<String>> loginUser(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));
        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Map<String, List<String>> tokens = new HashMap<>();
        tokens.put("access", List.of(jwtUtils.generateAccessToken(userDetails)));
        tokens.put("refresh", List.of(jwtUtils.generateRefreshToken(userDetails)));
        tokens.put("username", List.of(request.getUsername()));
        tokens.put("roles", roles);
        return tokens;
    }

    public Map<String, List<String>> loginWithGoogle(GoogleLogin request) {
        if (!emailValidator.test(request.getEmail())) {
            throw new IllegalStateException("email not valid");
        }
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            user = userService.registerGoogle(convertTo(request));
        }
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Map<String, List<String>> tokens = new HashMap<>();
        tokens.put("access", List.of(jwtUtils.generateAccessToken(userDetails)));
        tokens.put("refresh", List.of(jwtUtils.generateRefreshToken(userDetails)));
        tokens.put("username", List.of(user.getUsername()));
        tokens.put("roles", roles);
        return tokens;
    }

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
                    List<String> roles = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                    return new JwtResponse(access_token, refresh_token, username, roles);
                }
            } catch (Exception exception) {
                response.setStatus(UNAUTHORIZED.value());
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("data", null);
                error.put("message", "");
                error.put("error", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
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



