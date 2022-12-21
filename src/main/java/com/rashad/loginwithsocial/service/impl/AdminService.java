package com.rashad.loginwithsocial.service.impl;

import com.rashad.loginwithsocial.model.RegisterRequest;
import com.rashad.loginwithsocial.model.RoleRequest;

public interface AdminService {

    String createRole(RoleRequest request);

    String addRoleToUser(RoleRequest request);

    String createUser(RegisterRequest request);

    void deleteAllUser();
}
