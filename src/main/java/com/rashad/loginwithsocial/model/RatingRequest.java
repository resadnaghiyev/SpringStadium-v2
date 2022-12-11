package com.rashad.loginwithsocial.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequest {

    @Min(1)
    @Max(10)
    @NotNull(message = "required should not be empty")
    @Schema(example = "6")
    private Integer rating;
}
