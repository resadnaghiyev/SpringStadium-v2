package com.rashad.loginwithsocial.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrivateProfile {

    private Long id;

    private String name;

    private String surname;

    private String username;

    private String avatarUrl;

    private String bio;
}
