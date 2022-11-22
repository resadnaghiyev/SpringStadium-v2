package com.rashad.loginwithsocial.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleLogin {

    private String email;
    private String firstName;
    private String lastName;
}
