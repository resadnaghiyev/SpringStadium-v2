package com.rashad.loginwithsocial.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "companies")
public class Company extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String logoUrl;

    private String about;

    @JsonProperty("phones")
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private List<ComPhone> comPhones;

    @JsonIgnore
    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    private List<Stadium> stadiums;

    public Company(String name, String about, List<ComPhone> comPhones) {
        this.name = name;
        this.about = about;
        this.comPhones = comPhones;
    }
}
