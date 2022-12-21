package com.rashad.loginwithsocial.service;

import com.rashad.loginwithsocial.entity.ERole;
import com.rashad.loginwithsocial.entity.Role;
import com.rashad.loginwithsocial.entity.User;
import com.rashad.loginwithsocial.model.RegisterRequest;
import com.rashad.loginwithsocial.model.RoleRequest;
import com.rashad.loginwithsocial.repository.UserRepository;
import com.rashad.loginwithsocial.service.impl.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    @Override
    public String createRole(RoleRequest request) {
        String roleName = request.getRoleName();
        Role role = new Role(ERole.valueOf(roleName));
        userService.saveRole(role);
        return roleName + " created";
    }

    @Override
    public String addRoleToUser(RoleRequest request) {
        String username = request.getUsername();
        String roleName = request.getRoleName();
        userService.addRoleToUser(username, ERole.valueOf(roleName));
        return roleName + " added to username with: " + username;
    }

    @Override
    public String createUser(RegisterRequest request) {
        User user = new User(
                request.getName(),
                request.getSurname(),
                request.getEmail(),
                request.getPhone(),
                request.getUsername(),
                request.getPassword());
        userService.saveUser(user);
        userService.setActiveUser(request.getUsername());
        return "User created successfully";
    }

    @Override
    public void deleteAllUser() {
        userRepository.deleteAll();
    }
}

//    @Override
//    public void deleteAllUser(List<Long> ids) {
//        for (Long id : ids) {
//            userRepository.deleteById(id);
//        }
//    }