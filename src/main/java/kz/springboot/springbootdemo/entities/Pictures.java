package kz.springboot.springbootdemo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name="pictures")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pictures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="url", length =255)
    private String url;
    @Column(name="addedDate")
    private Date addedDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    Items item;




}
