package com.rashad.loginwithsocial.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequest {

    private String name;

    private String about;

    private List<PhoneRequest> phones;
}
