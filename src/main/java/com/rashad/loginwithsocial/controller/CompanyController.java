package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.entity.Company;
import com.rashad.loginwithsocial.model.CustomResponse;
import com.rashad.loginwithsocial.service.CompanyServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
@Tag(name = "4. Company CRUD")
public class CompanyController {

    private final CompanyServiceImpl companyServiceImpl;

    @Operation(
            summary = "Get one company",
            description = "Get one company by id",
            parameters = {@Parameter(name = "id", description = "companyId", example = "5")},
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompany(@PathVariable("id") Long companyId) {
        Company company = companyServiceImpl.getCompanyFromId(companyId);
        Map<String, Object> data = new HashMap<>();
        data.put("company_details", company);
        return new ResponseEntity<>(new CustomResponse(true, data, "", null), HttpStatus.OK);
    }
}
