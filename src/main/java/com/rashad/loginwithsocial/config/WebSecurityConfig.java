package com.rashad.loginwithsocial.config;

import com.rashad.loginwithsocial.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthEntryPoint unauthorizedHandler;
    private final JwtFilter jwtFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .accessDeniedHandler(accessDeniedHandler).and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/company/**").permitAll()
                .antMatchers("/stadium/**").permitAll()
                .antMatchers("/admin/**").permitAll()
                .antMatchers("/manager/**").permitAll()
                .antMatchers("/roles/**").permitAll()
                .antMatchers("/test/**").permitAll()
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated();

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsMappingConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://localhost:8081",
                                "https://stadium-booking.vercel.app",
                                "https://stadium.up.railway.app")
                        .allowedMethods("*");
            }
        };
    }
}


//                .antMatchers("/user/login/**").permitAll()
//                .antMatchers("/user/token/refresh/**").permitAll()
//                .antMatchers("/user/register/**").permitAll()
//                .antMatchers("/user/google/login/**").permitAll()