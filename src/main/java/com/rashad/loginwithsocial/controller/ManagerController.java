package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.entity.Company;
import com.rashad.loginwithsocial.entity.Stadium;
import com.rashad.loginwithsocial.model.*;
import com.rashad.loginwithsocial.service.ManagerServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager")
@SecurityRequirement(name = "BearerJwt")
@PreAuthorize("hasRole('MANAGER')")
@Tag(name = "3. Manager CRUD")
public class ManagerController {

    private final ManagerServiceImpl managerServiceImpl;

    @Operation(
            summary = "Create company",
            description = "For the create new company you have to send " +
                    "body with example like shown below",
            responses = {@ApiResponse(responseCode = "201", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PostMapping("/create/company")
    public ResponseEntity<?> createCompany(@RequestBody @Valid CompanyRequest request) {
        Long id = managerServiceImpl.createCompany(request);
        Map<String, Object> data = new HashMap<>();
        data.put("company_id", id);
        return new ResponseEntity<>(new CustomResponse(data), HttpStatus.CREATED);
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
        Company company = managerServiceImpl.uploadCompanyLogo(companyId, file);
        String message = "Company: " + company.getName() + " successfully created";
        return new ResponseEntity<>(new CustomResponse(company, message), HttpStatus.OK);
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
        Company company = managerServiceImpl.uploadCompanyLogo(companyId, file);
        String message = "Company's logo with id: " + companyId + " successfully changed";
        return new ResponseEntity<>(new CustomResponse(company, message), HttpStatus.OK);
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
        managerServiceImpl.deleteCompanyLogo(companyId);
        String message = "Company's logo with id: " + companyId + " successfully deleted";
        return new ResponseEntity<>(new CustomResponse(message), HttpStatus.OK);
    }

    @Operation(
            summary = "Update full company",
            description = "For the update company you have to send " +
                    "body with example like shown below",
            responses = {@ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PutMapping("/company/{id}/update")
    public ResponseEntity<?> updateCompany(@PathVariable("id") Long companyId,
                                           @RequestBody @Valid CompanyRequest request) {
        Company company = managerServiceImpl.updateCompany(companyId, request);
        String message = "Company: " + company.getName() + " successfully updated";
        return new ResponseEntity<>(new CustomResponse(company, message), HttpStatus.OK);
    }

    @Operation(
            summary = "Update company's fields",
            description = "For the update company you have to send " +
                    "body with example like shown below",
            responses = {@ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PatchMapping("/company/{id}/update-fields")
    public ResponseEntity<?> updateCompanyFields(@PathVariable("id") Long companyId,
                                                 @RequestBody Map<String, Object> fields) {
        Company company = managerServiceImpl.updateCompanyFields(companyId, fields);
        String message = "Company: " + company.getName() + " successfully updated";
        return new ResponseEntity<>(new CustomResponse(company, message), HttpStatus.OK);
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
        managerServiceImpl.deleteCompany(companyId);
        String message = "Company with id: " + companyId + " successfully deleted";
        return new ResponseEntity<>(new CustomResponse(message), HttpStatus.OK);
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
        Long id = managerServiceImpl.createStadium(companyId, request);
        Map<String, Object> data = new HashMap<>();
        data.put("stadium_id", id);
        return new ResponseEntity<>(new CustomResponse(data), HttpStatus.CREATED);
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
        Stadium stadium = managerServiceImpl.uploadStadiumImage(stadiumId, files);
        String message = "Stadium: " + stadium.getName() + " successfully created";
        return new ResponseEntity<>(new CustomResponse(stadium, message), HttpStatus.OK);
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
                                                @RequestBody @Valid IdListRequest request)
                                                throws IOException {
        Map<String, List<Long>> data = managerServiceImpl.deleteStadiumImage(stadiumId, request);
        return new ResponseEntity<>(new CustomResponse(data), HttpStatus.OK);
    }

    @Operation(
            summary = "Update full stadium",
            description = "For the update stadium you have to send " +
                    "body with example like shown below",
            responses = {@ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PutMapping("/stadium/{id}/update")
    public ResponseEntity<?> updateStadium(@PathVariable("id") Long stadiumId,
                                           @RequestBody @Valid StadiumRequest request) {
        Stadium stadium = managerServiceImpl.updateStadium(stadiumId, request);
        String message = "Stadium: " + stadium.getName() + " successfully updated";
        return new ResponseEntity<>(new CustomResponse(stadium, message), HttpStatus.OK);
    }

    @Operation(
            summary = "Update stadium's fields",
            description = "For the update stadium you have to send " +
                    "body with example like shown below",
            responses = {@ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @PatchMapping("/stadium/{id}/update-fields")
    public ResponseEntity<?> updateStadiumFields(@PathVariable("id") Long stadiumId,
                                                 @RequestBody Map<String, Object> fields) {
        Stadium stadium = managerServiceImpl.updateStadiumFields(stadiumId, fields);
        String message = "Stadium: " + stadium.getName() + " successfully updated";
        return new ResponseEntity<>(new CustomResponse(stadium, message), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete stadium",
            description = "Delete stadium by id",
            parameters = {@Parameter(name = "id", description = "stadiumId", example = "5")},
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @DeleteMapping("/stadium/{id}/delete")
    public ResponseEntity<?> deleteStadium(@PathVariable("id") Long stadiumId) {
        managerServiceImpl.deleteStadium(stadiumId);
        String message = "Stadium with id: " + stadiumId + " successfully deleted";
        return new ResponseEntity<>(new CustomResponse(message), HttpStatus.OK);
    }

}
