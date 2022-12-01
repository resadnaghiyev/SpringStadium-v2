package com.rashad.loginwithsocial.service.impl;

import com.rashad.loginwithsocial.entity.ERole;
import com.rashad.loginwithsocial.entity.Role;
import com.rashad.loginwithsocial.entity.User;
import com.rashad.loginwithsocial.model.BioRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    String signUpUser(User user);

    User registerGoogle(User user);

    void enableUser(String email);

    void saveUser(User user);

    void saveRole(Role role);

    void addRoleToUser(String username, ERole roleName);

    User getUserFromUsername(String username);

    Object getUserFromId(Long id);

    List<User> getAllUsers();

    void deleteUser(Long id);

    void addBioToUser(Long id, BioRequest request);

    String uploadAvatar(Long id, MultipartFile file) throws IOException;

    void deleteAvatar(Long id) throws IOException;

    User getUserMe();

    void setPrivate(Long userId);
}
