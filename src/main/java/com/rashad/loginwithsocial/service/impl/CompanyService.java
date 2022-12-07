package com.rashad.loginwithsocial.service.impl;

import com.rashad.loginwithsocial.entity.Company;
import com.rashad.loginwithsocial.entity.Stadium;

import java.util.List;

public interface CompanyService {

    Company getCompanyFromId(Long companyId);

    List<Company> getAllCompanies();

    List<Stadium> getStadiumsFromCompanyId(Long companyId);
}
