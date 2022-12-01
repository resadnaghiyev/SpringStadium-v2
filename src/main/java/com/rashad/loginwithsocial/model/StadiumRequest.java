package com.rashad.loginwithsocial.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StadiumRequest {

    @Length(max = 20)
    @NotBlank(message = "required should not be empty")
    @Schema(example = "Camp Nou")
    private String name;

    @Length(max = 20)
    @NotBlank(message = "required should not be empty")
    @Schema(example = "Bakı")
    private String city;

    @Length(max = 20)
    @NotBlank(message = "required should not be empty")
    @Schema(example = "Xətai")
    private String district;

    @Length(max = 100)
    @NotBlank(message = "required should not be empty")
    @Schema(example = "Məhəmməd Hadi 23")
    private String address;

    @Length(max = 20)
    @NotBlank(message = "required should not be empty")
    @Schema(example = "40.123256")
    private String latitude;

    @Length(max = 20)
    @NotBlank(message = "required should not be empty")
    @Schema(example = "56.463256")
    private String longitude;

    @Schema(example = "24.90")
    private Double price;

    @NotNull(message = "required should not be empty")
    private List<PhoneRequest> phones;
}
