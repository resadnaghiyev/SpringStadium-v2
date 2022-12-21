package com.rashad.loginwithsocial.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "std_phones")
public class StdPhone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @Column(length = 20)
    @Schema(example = "+994502467869")
    private String phone;

    public StdPhone(String phone) {
        this.phone = phone;
    }
}
