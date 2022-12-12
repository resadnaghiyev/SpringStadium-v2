package com.rashad.loginwithsocial.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rashad.loginwithsocial.entity.User;
import com.rashad.loginwithsocial.model.BioRequest;
import com.rashad.loginwithsocial.model.PrivateProfile;
import com.rashad.loginwithsocial.repository.RoleRepository;
import com.rashad.loginwithsocial.repository.UserRepository;
import com.rashad.loginwithsocial.entity.ConfirmationToken;
import com.rashad.loginwithsocial.entity.ERole;
import com.rashad.loginwithsocial.entity.Role;
import com.rashad.loginwithsocial.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final ConfirmTokenServiceImpl confirmTokenServiceImpl;

    Cloudinary cloudinary = new Cloudinary();

    public String getAvatarUrl() {
        return "https://res.cloudinary.com/resadnv/image/upload" +
                "/v1670859626/media/avatars/vu72i6dy2frwgwzrc12b.jpg";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalStateException("username: User with username: " + username + " is not found"));
        return UserDetailsImpl.build(user);
    }

    @Override
    public Map<String, Object> signUpUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("username: This username already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("email: This email already taken");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalStateException("phone: This phone already taken");
        }
        String token = createToken(user);
        saveUser(user);
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("user", user);
        return body;
    }

    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        user.getConfirmationTokens().add(confirmationToken);
        confirmTokenServiceImpl.saveConfirmationToken(confirmationToken);
        return token;
    }

    @Override
    public User registerGoogle(User user) {
        saveUser(user);
        setActiveUser(user.getUsername());
        return user;
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setAvatarUrl(getAvatarUrl());
        userRepository.save(user);
    }

    @Override
    public void setActiveUser(String username) {
        userRepository.activeUser(username);
        addRoleToUser(username, ERole.ROLE_USER);
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, ERole roleName) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalStateException("username: " + username + " is not found"));
        Role role = roleRepository.findByName(roleName).orElseThrow(() ->
                new IllegalStateException("role: " + roleName + " is not found"));
        user.getRoles().add(role);
    }

    public User checkIfUserOwnsData(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalStateException("user: user with id: " + id + " is not found"));
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (Objects.equals(user.getUsername(), principal.getUsername())) {
            return user;
        } else {
            throw new IllegalStateException("user: This id: " + id + " is not belong to you");
        }
    }

    @Override
    public void addBioToUser(Long id, BioRequest request) {
        User user = checkIfUserOwnsData(id);
        user.setBiography(request.getBio());
        userRepository.save(user);
    }

    @Override
    public String uploadAvatar(Long id, MultipartFile file) throws IOException {
        User user = checkIfUserOwnsData(id);
        if (!file.isEmpty()) {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "/media/avatars"));
            String url = uploadResult.get("secure_url").toString();
            user.setAvatarUrl(url);
            userRepository.save(user);
            return url;
        } else {
            throw new IllegalStateException("file: Required shouldn't be empty");
        }

    }

    @Override
    public void deleteAvatar(Long id) throws IOException {
        User user = checkIfUserOwnsData(id);
        String url = user.getAvatarUrl();
        if (!Objects.equals(url, getAvatarUrl())) {
            String public_id = url.substring(url.lastIndexOf("media"), url.lastIndexOf("."));
            Map<?, ?> deleteResult = cloudinary.uploader().destroy(public_id,
                    ObjectUtils.asMap("resource_type", "image"));
            if (Objects.equals(deleteResult.get("result").toString(), "ok")) {
                user.setAvatarUrl(getAvatarUrl());
                userRepository.save(user);
            } else {
                throw new IllegalStateException("cloudinary: Deleting avatar failed, public_id is not correct");
            }
        } else {
            throw new IllegalStateException("avatar: You dont have avatar for deleting");
        }
    }

    @Override
    public User getUserMe() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return getUserFromUsername(principal.getUsername());
    }

    @Override
    public void setPrivate(Long userId) {
        userRepository.setPrivate(userId);
    }

    @Override
    public User getUserFromUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalStateException("username: User with username: " + username + " is not found"));
    }

    @Override
    public Object getUserFromId(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalStateException("user: User with id: " + id + " is not found"));
        if (user.getIsPrivate()) {
            return new PrivateProfile(
                    user.getId(),
                    user.getName(),
                    user.getSurname(),
                    user.getUsername(),
                    user.getAvatarUrl(),
                    user.getBiography());
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        User user = checkIfUserOwnsData(id);
        userRepository.delete(user);
    }
}



