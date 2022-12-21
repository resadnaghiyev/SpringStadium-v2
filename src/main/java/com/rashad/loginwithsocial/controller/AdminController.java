package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.model.CustomResponse;
import com.rashad.loginwithsocial.model.RegisterRequest;
import com.rashad.loginwithsocial.model.RoleRequest;
import com.rashad.loginwithsocial.service.AdminServiceImpl;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@SecurityRequirement(name = "BearerJwt")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "6. Admin CRUD")
public class AdminController {

    private final AdminServiceImpl adminService;

    @Operation(
            summary = "Create role",
            description = "For the create new role you have to send " +
                    "body with example like shown below",
            responses = {@ApiResponse(responseCode = "201", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping("/create/role")
    public ResponseEntity<?> createRole(@RequestBody RoleRequest request) {
        String message = adminService.createRole(request);
        return new ResponseEntity<>(new CustomResponse(message), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Add role to user",
            description = "For the adding role to user you have to send " +
                    "body with example like shown below",
            responses = {@ApiResponse(responseCode = "201", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping("role/add-to-user")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleRequest request) {
        String message = adminService.addRoleToUser(request);
        return new ResponseEntity<>(new CustomResponse(message), HttpStatus.OK);
    }

    @Operation(
            summary = "Create user",
            description = "For the create new user you have to send " +
                    "body with example like shown below",
            responses = {@ApiResponse(responseCode = "201", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping("/create/user")
    public ResponseEntity<?> create(@RequestBody @Valid RegisterRequest request) {
        String message = adminService.createUser(request);
        return new ResponseEntity<>(new CustomResponse(message), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Delete all users",
            description = "Delete all users",
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @DeleteMapping("/user/delete-all")
    public ResponseEntity<?> deleteAllUser() {
        adminService.deleteAllUser();
        String message = "All users successfully deleted";
        return new ResponseEntity<>(new CustomResponse(message), HttpStatus.OK);
    }
}
