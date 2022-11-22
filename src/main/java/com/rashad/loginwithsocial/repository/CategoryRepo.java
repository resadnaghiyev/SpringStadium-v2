package com.rashad.loginwithsocial.repository;

import com.rashad.loginwithsocial.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    Category findByName(String name);
}
