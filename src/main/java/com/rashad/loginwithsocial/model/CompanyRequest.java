package com.rashad.loginwithsocial.model;

import com.rashad.loginwithsocial.entity.ComPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequest {

    @Length(min = 4, max = 20)
    @NotBlank(message = "required should not be empty")
    @Schema(example = "Company 1")
    private String name;

    @Schema(example = "This is our about us")
    private String about;

    @NotBlank(message = "required should not be empty")
    @Schema(example = "Məhəmməd Hadi 23")
    private String address;

    @NotEmpty(message = "required should not be empty")
    private List<ComPhone> phones;
}
