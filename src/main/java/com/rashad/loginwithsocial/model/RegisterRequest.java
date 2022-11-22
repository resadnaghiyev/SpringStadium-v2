package com.rashad.loginwithsocial.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class RegisterRequest {

    @NotBlank(message = "required should not be empty")
    @Schema(example = "Rashad")
    private String name;

    @NotBlank(message = "required should not be empty")
    @Schema(example = "Naghiyev")
    private String surname ;

    @NotBlank(message = "required should not be empty")
    @Schema(example = "example@gmail.com")
    private String email;

    @NotBlank(message = "required should not be empty")
    @Schema(example = "+994504453278")
    private String phone;

    @NotBlank(message = "required should not be empty")
    @Schema(example = "resad")
    private String username;

    @NotBlank(message = "required should not be empty")
    @Schema(example = "123@Resad",
            description = "Password must be at least 8 characters long and " +
            "should contain at least one upper, " +
            "one lower and one special character(@#$%^&+=).")
    private String password;
}
