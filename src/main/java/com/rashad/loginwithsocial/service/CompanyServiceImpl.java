package com.rashad.loginwithsocial.service;

import com.rashad.loginwithsocial.entity.Company;
import com.rashad.loginwithsocial.repository.CompanyRepository;
import com.rashad.loginwithsocial.service.impl.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public Company getCompanyFromId(Long companyId) {
        return companyRepository.findById(companyId).orElseThrow(() ->
                new IllegalStateException("Company with id: " + companyId + " is not found"));
    }
}