package kz.springboot.springbootdemo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shopitem {
     Long id;
 String name;
 String description;
 int price;
 int amount;
 int stars; // Just rating, from 0 to 5
 String pictureUrl;


}
