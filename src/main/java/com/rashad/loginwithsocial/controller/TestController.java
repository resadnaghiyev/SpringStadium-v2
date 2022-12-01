package com.rashad.loginwithsocial.controller;

import com.rashad.loginwithsocial.entity.*;
import com.rashad.loginwithsocial.model.StadiumRequest;
import com.rashad.loginwithsocial.repository.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
@Tag(name = "7. Testing CRUD")
@Hidden
public class TestController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final StadiumRepository stadiumRepository;
    private final ComPhoneRepository comPhoneRepository;
    private final StdImageRepository stdImageRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @GetMapping("/all")
    public String allAccess() {
        return "public API, All users can see this.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public String userAccess() {
        return "user API";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String moderatorAccess() {
        return "moderator API";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "admin API";
    }

    @PostMapping("/info/{id}")
    public Role userInfo(@PathVariable Long id) {
        return roleRepository.findById(id).orElseThrow();
    }

    @DeleteMapping("/token/{id}")
    public String deleteToken(@PathVariable Long id) {
        confirmationTokenRepository.deleteById(id);
        return "deleted " + id;
    }

//    @PostMapping("/company")
//    public String createCompany(@RequestBody CompanyRequest request) {
//        List<ComPhone> phones = new ArrayList<>();
//        for (PhoneRequest i : request.getComPhones()) {
//            ComPhone phone = new ComPhone(i.getPhone());
//            phones.add(phone);
//            phoneRepository.save(phone);
//        }
//        Company company = new Company(request.getName(), request.getAbout(), phones);
//        companyRepository.save(company);
//        return "created";
//    }

    @PostMapping("/{id}/stadium")
    public String createStadium(@RequestBody StadiumRequest request, @PathVariable Long id) {
//        List<StdPhone> stdPhones = new ArrayList<>();
//        for (PhoneRequest i : request.getComPhones()) {
//            StdPhone stdPhone = new StdPhone(i.getPhone());
//            stdPhones.add(stdPhone);
//            stdPhoneRepository.save(stdPhone);
//        }
//        Company company = companyRepository.findById(id).orElseThrow();
//        Stadium stadium = new Stadium(
//                request.getName(),
//                request.getAddress(),
//                request.getLatitude(),
//                request.getLongitude(),
//                request.getPrice(),
//                stdPhones,
//                company
//        );
//        stadiumRepository.save(stadium);
        return "created";
    }

}
