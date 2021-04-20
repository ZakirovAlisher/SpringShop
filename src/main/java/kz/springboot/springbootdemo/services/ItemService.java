package kz.springboot.springbootdemo.services;

import kz.springboot.springbootdemo.entities.*;

import java.util.List;


public interface ItemService {
    Items addItem(Items items);
    List<Items> getAllItems();
    Items getItem(Long id);
    void deleteItem(Items item);
    Items saveItem(Items item);
    List<Items> byname(String name);


    List<Items> bynameAsc(String name);
    List<Items> bynameDesc(String name);

    List<Items> bynamepriceAsc(String name,  double price1, double price2);
    List<Items> bynamepriceDesc(String name,  double price1, double price2);

    List<Countries> getAllCountries( );
    Countries addCountry(Countries c);
Countries getCountry(Long id);

    List<Brands> getAllBrands( );
    Brands addBrand(Brands b);
    Brands getBrand(Long id);
    Brands saveBr(Brands item);

    List<Items> bybrand(String br);

    Countries saveCtn(Countries item);


    void deleteCtn(Countries item);
    void deleteBr(Brands item);


    List<Categories> getAllCategories();
    Categories getCategory(Long id);
    Categories saveCategory(Categories category);
    Categories addCategory(Categories category);
    void deleteCategory(Categories category);



    Pictures addPicture(Pictures picture);
    void deletePicture(Pictures picture);
    Pictures getPicture(Long id);

    Sold addSold(Sold sold);
    List<Sold> getAllSolds();

    Comments saveComment(Comments comment);
    Comments getComment(Long id);
    Comments addComment(Comments comment);
    void deleteComment(Comments comment);

}
