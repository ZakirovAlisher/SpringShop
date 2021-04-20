package kz.springboot.springbootdemo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name="comments")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="comment", length = 65535,columnDefinition="Text")
    private String comment;

    @Column(name="addedDate")
    private Date addedDate;


    @ManyToOne(fetch = FetchType.LAZY)

    Items item;

    @ManyToOne(fetch = FetchType.LAZY)

    Users author;



}
