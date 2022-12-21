package com.rashad.loginwithsocial.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rashad.loginwithsocial.entity.*;
import com.rashad.loginwithsocial.model.*;
import com.rashad.loginwithsocial.repository.*;
import com.rashad.loginwithsocial.service.impl.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final UserRepository userRepository;
    private final ComPhoneRepository comPhoneRepository;
    private final CompanyRepository companyRepository;
    private final StdPhoneRepository stdPhoneRepository;
    private final DistrictRepository districtRepository;
    private final StadiumRepository stadiumRepository;
    private final StdImageRepository stdImageRepository;
    private final CityRepository cityRepository;

    Cloudinary cloudinary = new Cloudinary();

    public Company checkCompanyManager(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new IllegalStateException("company: Company with id: " + companyId + " not found"));
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() ->
                new IllegalStateException("admin: Not Found"));
        if (company.getUser() == user) {
            return company;
        } else {
            throw new IllegalStateException("company: This company don't below to you");
        }
    }

    public Stadium checkStadiumManager(Long stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(() ->
                new IllegalStateException("stadium: Stadium with id: " + stadiumId + " not found"));
        checkCompanyManager(stadium.getCompany().getId());
        return stadium;
    }

    @Override
    public Long createCompany(CompanyRequest request) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() ->
                new IllegalStateException("admin: Not Found"));
        List<ComPhone> phones = comPhoneRepository.saveAll(request.getPhones());
        Company company = new Company(
                request.getName(), request.getAbout(), request.getAddress(), phones, user
        );
        companyRepository.save(company);
        return company.getId();
    }

    @Override
    public Company uploadCompanyLogo(Long companyId, MultipartFile file) throws IOException {
        Company company = checkCompanyManager(companyId);
        if (!file.isEmpty()) {
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
        Company company = checkCompanyManager(companyId);
        String url = company.getLogoUrl();
        if (url != null) {
            String public_id = url.substring(url.lastIndexOf("media"), url.lastIndexOf("."));
            Map<?, ?> deleteResult = cloudinary.uploader().destroy(public_id,
                    ObjectUtils.asMap("resource_type", "image"));
            if (Objects.equals(deleteResult.get("result").toString(), "ok")) {
                company.setLogoUrl(null);
                companyRepository.save(company);
            } else {
                throw new IllegalStateException(
                        "cloudinary: Deleting logo failed, public_id is not correct");
            }
        } else {
            throw new IllegalStateException("logo: You dont have logo for deleting");
        }
    }

    @Override
    public Company updateCompany(Long companyId, CompanyRequest request) {
        Company company = checkCompanyManager(companyId);
        company.setName(request.getName());
        company.setAbout(request.getAbout());
        company.setAddress(request.getAddress());
        comPhoneRepository.deleteAll(company.getComPhones());
        List<ComPhone> phones = comPhoneRepository.saveAll(request.getPhones());
        company.setComPhones(phones);
        return companyRepository.save(company);
    }

    @Override
    public Company updateCompanyFields(Long companyId, Map<String, Object> fields) {
        Company company = checkCompanyManager(companyId);
        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Company.class, key);
            if (field == null) {
                throw new IllegalStateException("field: Field: " + key + " not Found");
            }
            field.setAccessible(true);
            if (field.getType() == String.class){
                ReflectionUtils.setField(field, company, value);
            } else {
                throw new IllegalStateException(
                        "field: For update field: " + key + " you have to full update");
            }
        });
        return companyRepository.save(company);
    }

    @Override
    public void deleteCompany(Long companyId) {
        Company company = checkCompanyManager(companyId);
        companyRepository.delete(company);
    }

    @Override
    public Long createStadium(Long companyId, StadiumRequest request) {
        Company company = checkCompanyManager(companyId);
        List<StdPhone> phones = stdPhoneRepository.saveAll(request.getPhones());
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
        Stadium stadium = checkStadiumManager(stadiumId);
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
    public Map<String, List<Long>> deleteStadiumImage(Long stadiumId,
                                                      IdListRequest request) throws IOException {
        checkStadiumManager(stadiumId);
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
    public Stadium updateStadium(Long stadiumId, StadiumRequest request) {
        Stadium stadium = checkStadiumManager(stadiumId);
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
        stadium.setName(request.getName());
        stadium.setCity(city);
        stadium.setDistrict(district);
        stadium.setAddress(request.getAddress());
        stadium.setLatitude(request.getLatitude());
        stadium.setLongitude(request.getLongitude());
        stadium.setPrice(request.getPrice());
        stdPhoneRepository.deleteAll(stadium.getPhones());
        List<StdPhone> phones = stdPhoneRepository.saveAll(request.getPhones());
        stadium.setPhones(phones);
        return stadiumRepository.save(stadium);
    }

    @Override
    public Stadium updateStadiumFields(Long stadiumId, Map<String, Object> fields) {
        Stadium stadium = checkStadiumManager(stadiumId);
        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Stadium.class, key);
            if (field == null) {
                throw new IllegalStateException("field: Field: " + key + " not Found");
            }
            field.setAccessible(true);
            if (field.getType() == String.class){
                ReflectionUtils.setField(field, stadium, value);
            } else {
                throw new IllegalStateException(
                        "field: For update field: " + key + " you have to full update");
            }
        });
        return stadiumRepository.save(stadium);
    }

    @Override
    public void deleteStadium(Long stadiumId) {
        Stadium stadium = checkStadiumManager(stadiumId);
        stadiumRepository.delete(stadium);
    }
}
