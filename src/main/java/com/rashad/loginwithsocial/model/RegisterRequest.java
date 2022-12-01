package com.rashad.loginwithsocial.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class RegisterRequest {

    @Length(max = 20)
    @NotBlank(message = "required should not be empty")
    @Schema(example = "Rashad")
    private String name;

    @Length(max = 20)
    @NotBlank(message = "required should not be empty")
    @Schema(example = "Naghiyev")
    private String surname ;

    @NotBlank(message = "required should not be empty")
    @Schema(example = "example@gmail.com")
    private String email;

    @NotBlank(message = "required should not be empty")
    @Schema(example = "+994504453278")
    private String phone;

    @Length(min = 4, max = 20)
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
