package kz.springboot.springbootdemo.entities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Items {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

        @Column(name="name", length =200)
    private String name;

    @Column(name="description", length =200)
    private String description;

    @Column(name="price")
    private double price;

    @Column(name="stars")
    private int stars;

    @Column(name="small_url", length =500)
    private String smallPicURL;

    @Column(name="large_url", length =500)
    private String largePicURL;


    @Column(name="date")
    private Date addedDate;

    @Column(name="top")
    private boolean inTopPage;

    @ManyToOne(fetch = FetchType.EAGER)
    Brands brand;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Categories> categories;


    @OneToMany(mappedBy = "item",fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Pictures> pictures;

    @OneToMany(mappedBy = "item",fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Comments> comments;

    @OneToMany(mappedBy = "item",fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Sold> solds;

}
