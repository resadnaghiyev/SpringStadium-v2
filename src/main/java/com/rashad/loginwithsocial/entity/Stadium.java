package com.rashad.loginwithsocial.entity;

import com.fasterxml.jackson.annotation.*;
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
@Table(name = "stadiums")
public class Stadium extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String address;

    private String latitude;

    private String longitude;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "stadium_id", referencedColumnName = "id")
    private List<StdPhone> phones;

    @OneToMany
    @JoinColumn(name = "stadium_id", referencedColumnName = "id")
    private List<StdImage> images;

    @ManyToOne
    @JsonIncludeProperties({"id", "name"})
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
    private City city;

    @ManyToOne
    @JoinColumn(name = "district_id", referencedColumnName = "id", nullable = false)
    private District district;

    @JsonIgnore
    @OneToMany(mappedBy = "stadium", fetch = FetchType.LAZY)
    private List<Rating> ratings;

    private Double price;

    private Double rating;

    private Integer userRating;

    public Stadium(String name,
                   String address,
                   String latitude,
                   String longitude,
                   Double price,
                   City city,
                   District district,
                   List<StdPhone> phones,
                   Company company) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.phones = phones;
        this.company = company;
        this.city = city;
        this.district = district;
    }
}
