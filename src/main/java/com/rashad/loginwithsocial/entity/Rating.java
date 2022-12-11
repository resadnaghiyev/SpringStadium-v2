package com.rashad.loginwithsocial.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer point;

    @ManyToOne
    @JoinColumn(name = "stadium_id", referencedColumnName = "id")
    private Stadium stadium;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Rating(Integer point) {
        this.point = point;
    }
}


//    @JsonIgnore
//    @ManyToMany
//    @JoinTable(
//            name = "user_ratings",
//            joinColumns = @JoinColumn(
//                    name = "rating_id",
//                    referencedColumnName = "id"
//            ),
//            inverseJoinColumns = @JoinColumn(
//                    name = "user_id",
//                    referencedColumnName = "id"
//            )
//    )
//    private List<User> users;
//
//    @JsonIgnore
//    @ManyToMany
//    @JoinTable(
//            name = "stadium_ratings",
//            joinColumns = @JoinColumn(
//                    name = "rating_id",
//                    referencedColumnName = "id"
//            ),
//            inverseJoinColumns = @JoinColumn(
//                    name = "stadium_id",
//                    referencedColumnName = "id"
//            )
//    )
//    private List<Stadium> stadiums;