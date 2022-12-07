package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.entity.Company;
import com.rashad.loginwithsocial.entity.Stadium;
import com.rashad.loginwithsocial.model.*;
import com.rashad.loginwithsocial.repository.StadiumRepository;
import com.rashad.loginwithsocial.service.AdminServiceImpl;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "3. Admin CRUD")
public class AdminController {

    private final AdminServiceImpl adminServiceImpl;
    private final StadiumRepository stadiumRepository;

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
        String message = adminServiceImpl.createUser(request);
        return new ResponseEntity<>(new CustomResponse(
                true, null, message, null), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Create company",
            description = "For the create new company you have to send " +
                    "body with example like shown below",
            responses = {@ApiResponse(responseCode = "201", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping("/create/company")
    public ResponseEntity<?> createCompany(@RequestBody CompanyRequest request) {
        Long id = adminServiceImpl.createCompany(request);
        Map<String, Object> data = new HashMap<>();
        data.put("company_id", id);
        return new ResponseEntity<>(new CustomResponse(true, data, "", null), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Upload company logo",
            description = "For upload logo you have to send form-data image file with company id",
            parameters = {@Parameter(name = "id", description = "companyId", example = "5")},
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping(value = "/company/{id}/upload/logo", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCompanyLogo(@PathVariable("id") Long companyId,
                                               @RequestParam("file") MultipartFile file)
                                               throws IOException {
        Company company = adminServiceImpl.uploadCompanyLogo(companyId, file);
        String message = "Company: " + company.getName() + " created successfully";
        return new ResponseEntity<>(new CustomResponse(true, company, message, null), HttpStatus.OK);
    }

    @Operation(
            summary = "Change company logo",
            description = "For change logo you have to send form-data image file with company id",
            parameters = {@Parameter(name = "id", description = "companyId", example = "5")},
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping(value = "/company/{id}/change/logo", consumes = "multipart/form-data")
    public ResponseEntity<?> changeCompanyLogo(@PathVariable("id") Long companyId,
                                               @RequestParam("file") MultipartFile file)
                                               throws IOException {
        Company company = adminServiceImpl.uploadCompanyLogo(companyId, file);
        Map<String, Object> data = new HashMap<>();
        data.put("company_id", companyId);
        data.put("company_logo_url", company.getLogoUrl());
        return new ResponseEntity<>(new CustomResponse(true, data, "", null), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete company logo",
            description = "For delete logo you have to send company id",
            parameters = {@Parameter(name = "id", description = "companyId", example = "5")},
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @DeleteMapping("/company/{id}/delete/logo")
    public ResponseEntity<?> deleteCompanyLogo(@PathVariable("id") Long companyId) throws IOException {
        adminServiceImpl.deleteCompanyLogo(companyId);
        Map<String, Object> data = new HashMap<>();
        data.put("company_id", companyId);
        data.put("logo", "deleted successfully");
        return new ResponseEntity<>(new CustomResponse(true, data, "", null), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete company",
            description = "Delete company by id",
            parameters = {@Parameter(name = "id", description = "companyId", example = "5")},
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @DeleteMapping("/company/{id}/delete")
    public ResponseEntity<?> deleteCompany(@PathVariable("id") Long companyId) {
        adminServiceImpl.deleteCompany(companyId);
        Map<String, Object> data = new HashMap<>();
        data.put("company_id", companyId);
        data.put("message", "Company deleted successfully.");
        return new ResponseEntity<>(new CustomResponse(true, data, "", null), HttpStatus.OK);
    }

    @Operation(
            summary = "Create stadium",
            description = "For the create new stadium you have to send " +
                    "body with example like shown below",
            parameters = {@Parameter(name = "id", description = "companyId", example = "5")},
            responses = {@ApiResponse(responseCode = "201", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping("/create/{id}/stadium")
    public ResponseEntity<?> createStadium(@PathVariable("id") Long companyId,
                                           @RequestBody @Valid StadiumRequest request) {
        Long id = adminServiceImpl.createStadium(companyId, request);
        Map<String, Object> data = new HashMap<>();
        data.put("stadium_id", id);
        return new ResponseEntity<>(new CustomResponse(true, data, "", null), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Upload stadium images",
            description = "For upload images you have to send form-data image file with stadium id",
            parameters = {@Parameter(name = "id", description = "stadiumId", example = "5")},
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping(value = "/stadium/{id}/upload/image", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadStadiumPhotos(@PathVariable("id") Long stadiumId,
                                                 @RequestParam("file") MultipartFile[] files)
                                                 throws IOException {
        Stadium stadium = adminServiceImpl.uploadStadiumImage(stadiumId, files);
        String message = "Stadium: " + stadium.getName() + " created successfully";
        return new ResponseEntity<>(new CustomResponse(true, stadium, message, null), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete stadium images",
            description = "For delete image you have to send stadium id with body example like shown below",
            parameters = {@Parameter(name = "id", description = "stadiumId", example = "5")},
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @DeleteMapping("/stadium/{id}/delete/image")
    public ResponseEntity<?> deleteStadiumImage(@PathVariable("id") Long stadiumId,
                                                @RequestBody @Valid ImageRequest request)
                                                throws IOException {
        Map<String, List<Long>> data = adminServiceImpl.deleteStadiumImage(stadiumId, request);
        return new ResponseEntity<>(new CustomResponse(true, data, "", null), HttpStatus.OK);
    }
}
