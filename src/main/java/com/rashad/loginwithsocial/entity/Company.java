package com.rashad.loginwithsocial.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
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

    @Column(unique = true)
    private String name;

    private String logoUrl;

    private String address;

    private String about;

    @JsonProperty("phones")
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private List<ComPhone> comPhones;

    @JsonIgnore
    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    private List<Stadium> stadiums;

    @ManyToOne
    @JsonProperty("admin")
    @JsonIncludeProperties({"id", "name"})
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public Company(String name,
                   String about,
                   String address,
                   List<ComPhone> comPhones,
                   User user) {
        this.name = name;
        this.about = about;
        this.address = address;
        this.comPhones = comPhones;
        this.user = user;
    }
}
