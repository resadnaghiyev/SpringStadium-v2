package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.entity.Category;
import com.rashad.loginwithsocial.entity.User;
import com.rashad.loginwithsocial.model.RoleRequest;
import com.rashad.loginwithsocial.repository.CategoryRepo;
import com.rashad.loginwithsocial.repository.UserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
@Tag(name = "5. Testing CRUD")
@Hidden
public class TestController {

    private final CategoryRepo repo;
    private final UserRepository userRepo;

    @GetMapping("/all")
    public String allAccess() {
        return "public API, All users can see this.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public String userAccess() {
        return "user API";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String moderatorAccess() {
        return "moderator API";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "admin API";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public String add(@RequestBody RoleRequest request) {
        String category = request.getRoleName();
        Category cate = new Category(category);
        repo.save(cate);
        return "success";
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public String update(@RequestBody RoleRequest request) {
        String category = request.getRoleName();
        Category cate = repo.findByName(category);
        cate.setName("new name");
        repo.save(cate);
        return "success";
    }

    @PostMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public String userInfo(@RequestBody RoleRequest request) {
        String username = request.getRoleName();
        User user = userRepo.findByUsername(username).orElseThrow();
        String created = user.getCreatedDate().toString();
        return created;
    }
}
