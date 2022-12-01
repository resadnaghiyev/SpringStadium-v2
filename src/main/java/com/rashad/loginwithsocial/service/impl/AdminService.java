package com.rashad.loginwithsocial.service.impl;

import com.rashad.loginwithsocial.entity.Company;
import com.rashad.loginwithsocial.entity.Stadium;
import com.rashad.loginwithsocial.model.CompanyRequest;
import com.rashad.loginwithsocial.model.RegisterRequest;
import com.rashad.loginwithsocial.model.StadiumRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AdminService {

    String createUser(RegisterRequest request);

    Long createCompany(CompanyRequest request);

    Company uploadCompanyLogo(Long id, MultipartFile file) throws IOException;

    void deleteCompanyLogo(Long id) throws IOException;

    void deleteCompany(Long companyId);

    Long createStadium(Long companyId, StadiumRequest request);

    Stadium uploadStadiumImage(Long id, MultipartFile file) throws IOException;

    void deleteStadiumImage(Long id);
}
