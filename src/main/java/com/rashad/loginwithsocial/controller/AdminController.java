package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.model.CustomResponse;
import com.rashad.loginwithsocial.model.RegisterRequest;
import com.rashad.loginwithsocial.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "3. Admin CRUD")
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Create user",
            description = "For the create new user you have to send body with example like shown below",
            responses = {@ApiResponse(responseCode = "201", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping("/create/user")
    public ResponseEntity<?> create(@RequestBody @Valid RegisterRequest request) {
        String message = adminService.createUser(request);
        return new ResponseEntity<>(new CustomResponse(true, null, message, null), HttpStatus.CREATED);
    }
}
