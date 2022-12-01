package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.entity.ERole;
import com.rashad.loginwithsocial.entity.Role;
import com.rashad.loginwithsocial.model.RoleRequest;
import com.rashad.loginwithsocial.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@AllArgsConstructor
@Tag(name = "6. Role CRUD")
@Hidden
public class RoleController {

    private final UserServiceImpl userService;

    @PostMapping("/create")
    public String createRole(@RequestBody RoleRequest request) {
        String roleName = request.getRoleName();
        Role role = new Role(ERole.valueOf(roleName));
        userService.saveRole(role);
        return roleName + " created";
    }

    @PostMapping("/add-to-user")
    public String addRoleToUser(@RequestBody RoleRequest request) {
        String username = request.getUsername();
        String roleName = request.getRoleName();
        userService.addRoleToUser(username, ERole.valueOf(roleName));
        return roleName + " added to username with: " + username;
    }
}
