package com.rashad.loginwithsocial.service.impl;

import com.rashad.loginwithsocial.entity.Company;
import com.rashad.loginwithsocial.entity.Stadium;
import com.rashad.loginwithsocial.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ManagerService {

    Long createCompany(CompanyRequest request);

    Company uploadCompanyLogo(Long companyId, MultipartFile file) throws IOException;

    void deleteCompanyLogo(Long companyId) throws IOException;

    Company updateCompany(Long companyId, CompanyRequest request);

    Company updateCompanyFields(Long companyId, Map<String, Object> fields);

    void deleteCompany(Long companyId);

    Long createStadium(Long companyId, StadiumRequest request);

    Stadium uploadStadiumImage(Long stadiumId, MultipartFile[] files) throws IOException;

    Map<String, List<Long>> deleteStadiumImage(Long stadiumId, IdListRequest request) throws IOException;

    Stadium updateStadium(Long stadiumId, StadiumRequest request);

    Stadium updateStadiumFields(Long stadiumId, Map<String, Object> fields);

    void deleteStadium(Long stadiumId);
}
