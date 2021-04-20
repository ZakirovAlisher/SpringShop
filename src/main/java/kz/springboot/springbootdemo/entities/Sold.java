package kz.springboot.springbootdemo.entities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "solds")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;



    @ManyToOne(fetch = FetchType.EAGER)

    private Items item;


    @Column(name="date")
    private Date date;

}
