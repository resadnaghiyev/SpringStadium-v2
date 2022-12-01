package com.rashad.loginwithsocial.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhoneRequest {

    @Length(max = 20)
    @NotBlank(message = "required should not be empty")
    @Schema(example = "+994502467869")
    private String phone;

}
