package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.entity.Company;
import com.rashad.loginwithsocial.entity.Stadium;
import com.rashad.loginwithsocial.model.CustomResponse;
import com.rashad.loginwithsocial.repository.StadiumRepository;
import com.rashad.loginwithsocial.service.StadiumServiceImpl;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stadium")
@RequiredArgsConstructor
@Tag(name = "5. Stadium CRUD")
public class StadiumController {

    private final StadiumServiceImpl stadiumService;

    @Operation(
            summary = "Get one stadium",
            description = "Get one stadium by id",
            parameters = {@Parameter(name = "id", description = "stadiumId", example = "5")},
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getStadium(@PathVariable("id") Long stadiumId) {
        Stadium stadium = stadiumService.getStadiumFromId(stadiumId);
        Map<String, Object> data = new HashMap<>();
        data.put("stadium_details", stadium);
        return new ResponseEntity<>(new CustomResponse(true, data, "", null), HttpStatus.OK);
    }

    @Operation(
            summary = "Get all stadiums",
            description = "Get all stadium list",
            responses = {@ApiResponse(responseCode = "200", description = "Success response",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    @GetMapping("/all")
    public ResponseEntity<?> getAllStadium() {
        List<Stadium> stadiums = stadiumService.getAllStadiums();
        Map<String, Object> data = new HashMap<>();
        data.put("all_stadium_list", stadiums);
        return new ResponseEntity<>(new CustomResponse(true, data, "", null), HttpStatus.OK);
    }

}
