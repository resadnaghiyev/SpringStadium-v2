package com.rashad.loginwithsocial.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rashad.loginwithsocial.entity.*;
import com.rashad.loginwithsocial.model.*;
import com.rashad.loginwithsocial.repository.*;
import com.rashad.loginwithsocial.service.impl.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final ComPhoneRepository comPhoneRepository;
    private final CompanyRepository companyRepository;
    private final StdPhoneRepository stdPhoneRepository;
    private final DistrictRepository districtRepository;
    private final StadiumRepository stadiumRepository;
    private final StdImageRepository stdImageRepository;
    private final CityRepository cityRepository;

    Cloudinary cloudinary = new Cloudinary();

    @Override
    public String createUser(RegisterRequest request) {
        User user = new User(
                request.getName(),
                request.getSurname(),
                request.getEmail(),
                request.getPhone(),
                request.getUsername(),
                request.getPassword());
        userRepository.save(user);
        userService.setActiveUser(request.getUsername());
        return "User created successfully";
    }

    @Override
    public Long createCompany(CompanyRequest request) {
        List<ComPhone> phones = new ArrayList<>();
        for (PhoneRequest i : request.getPhones()) {
            ComPhone phone = new ComPhone(i.getPhone());
            comPhoneRepository.save(phone);
            phones.add(phone);
        }
        Company company = new Company(request.getName(), request.getAbout(), phones);
        companyRepository.save(company);
        return company.getId();
    }

    @Override
    public Company uploadCompanyLogo(Long companyId, MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            Company company = companyRepository.findById(companyId).orElseThrow(() ->
                    new IllegalStateException("company: Company with id: " + companyId + " not found"));
            if (company.getLogoUrl() != null) {
                deleteCompanyLogo(companyId);
            }
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "/media/logos"));
            String url = uploadResult.get("secure_url").toString();
            company.setLogoUrl(url);
            companyRepository.save(company);
            return company;
        } else {
            throw new IllegalStateException("file: Required shouldn't be empty");
        }
    }

    @Override
    public void deleteCompanyLogo(Long companyId) throws IOException {
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new IllegalStateException("company: Company with id: " + companyId + " not found"));
        String url = company.getLogoUrl();
        if (url != null) {
            String public_id = url.substring(url.lastIndexOf("media"), url.lastIndexOf("."));
            Map<?, ?> deleteResult = cloudinary.uploader().destroy(public_id,
                    ObjectUtils.asMap("resource_type", "image"));
            if (Objects.equals(deleteResult.get("result").toString(), "ok")) {
                company.setLogoUrl(null);
                companyRepository.save(company);
            } else {
                throw new IllegalStateException("cloudinary: Deleting logo failed, public_id is not correct");
            }
        } else {
            throw new IllegalStateException("logo: You dont have logo for deleting");
        }
    }

    @Override
    public void deleteCompany(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new IllegalStateException("company: Company with id: " + companyId + " not found"));
        companyRepository.delete(company);
    }

    @Override
    public Long createStadium(Long companyId, StadiumRequest request) {
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new IllegalStateException("company: Company with id: " + companyId + " not found"));
        List<StdPhone> phones = new ArrayList<>();
        for (PhoneRequest i : request.getPhones()) {
            StdPhone phone = new StdPhone(i.getPhone());
            stdPhoneRepository.save(phone);
            phones.add(phone);
        }
        City city = cityRepository.findByName(request.getCity());
        if (city == null) {
            city = new City(request.getCity());
            cityRepository.save(city);
        }
        District district = districtRepository.findByName(request.getDistrict());
        if (district == null) {
            district = new District(request.getDistrict());
            districtRepository.save(district);
        }
        Stadium stadium = new Stadium(
                request.getName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude(),
                request.getPrice(),
                city,
                district,
                phones,
                company
        );
        stadiumRepository.save(stadium);
        return stadium.getId();
    }

    @Override
    public Stadium uploadStadiumImage(Long stadiumId, MultipartFile[] files) throws IOException {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(() ->
                new IllegalStateException("stadium: Stadium with id: " + stadiumId + " not found"));
        if (!files[0].isEmpty()) {
            for (MultipartFile file : files) {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap("folder", "/media/stadium_images"));
                StdImage image = new StdImage(uploadResult.get("secure_url").toString());
                stdImageRepository.save(image);
                stadium.getImages().add(image);
                stadiumRepository.save(stadium);
            }
        } else {
            throw new IllegalStateException("file: Required shouldn't be empty");
        }
        return stadium;
    }

    @Override
    public Map<String, List<Long>> deleteStadiumImage(Long id, IdListRequest request) throws IOException {
        Map<String, List<Long>> response = new HashMap<>();
        List<Long> success = new ArrayList<>();
        List<Long> failed = new ArrayList<>();
        List<Long> notFound = new ArrayList<>();
        if (request.getIdList().size() == 0) {
            throw new IllegalStateException("imageIdList: required shouldn't be empty");
        }
        for (Long imageId : request.getIdList()) {
            Optional<StdImage> image = stdImageRepository.findById(imageId);
            if (image.isPresent()) {
                String url = image.get().getImageUrl();
                String public_id = url.substring(url.lastIndexOf("media"), url.lastIndexOf("."));
                Map<?, ?> deleteResult = cloudinary.uploader().destroy(public_id,
                        ObjectUtils.asMap("resource_type", "image"));
                if (Objects.equals(deleteResult.get("result").toString(), "ok")) {
                    stdImageRepository.deleteById(imageId);
                    success.add(imageId);
                    response.put("success", success);
                } else {
                    failed.add(imageId);
                    response.put("failed", failed);
                }
            } else {
                notFound.add(imageId);
                response.put("notFound", notFound);
            }
        }
        return response;
    }

    @Override
    public void deleteAllUser(List<Long> ids) {
        for (Long id : ids) {
            userRepository.deleteById(id);
        }
    }
}
