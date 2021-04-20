package kz.springboot.springbootdemo.services;

import kz.springboot.springbootdemo.entities.*;
import kz.springboot.springbootdemo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private SoldRepository soldRepository;

    @Autowired
    private CommentRepository commentRepository;
    @Override
    public Items addItem(Items item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Items> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Items getItem(Long id) {
        return itemRepository.getOne(id);
    }

    @Override
    public void deleteItem(Items item) {
itemRepository.delete(item);
    }

    @Override
    public Items saveItem(Items item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Items> byname(String name) {

        return itemRepository.findAllByNameLike("%"+name+"%");
    }

    @Override
    public List<Items> bynameAsc(String name) {

        return itemRepository.findAllByNameLikeOrderByPriceAsc("%"+ name+"%");
    }

    @Override
    public List<Items> bynameDesc(String name) {
        return itemRepository.findAllByNameLikeOrderByPriceDesc("%"+ name+"%");
    }

    @Override
    public List<Items> bynamepriceAsc(String name, double price1, double price2) {
        return  itemRepository.findAllByNameLikeAndPriceBetweenOrderByPriceAsc("%"+name+"%",price1,price2);
    }

    @Override
    public List<Items> bynamepriceDesc(String name, double price1, double price2) {
        return itemRepository.findAllByNameLikeAndPriceBetweenOrderByPriceDesc("%"+name+"%",price1,price2);
    }

    @Override
    public List<Countries> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Countries addCountry(Countries c) {
        return countryRepository.save(c);
    }

    @Override
    public Countries getCountry(Long id) {
        return countryRepository.getOne(id);
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Brands addBrand(Brands b) {
        return brandRepository.save(b);
    }

    @Override
    public Brands getBrand(Long id) {
        return brandRepository.getOne(id);
    }

    @Override
    public List<Items> bybrand(String br) {
        return itemRepository.findAllByBrandNameLike(br);
    }

    @Override
    public Countries saveCtn(Countries item) {
        return countryRepository.save(item);
    }

    @Override
    public Brands saveBr(Brands item) {
        return brandRepository.save(item);
    }

    @Override
    public void deleteCtn(Countries item) {
        countryRepository.delete(item);
    }

    @Override
    public void deleteBr(Brands item) {
brandRepository.delete(item);
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Categories getCategory(Long id) {
        return  categoryRepository.getOne(id);
    }

    @Override
    public Categories saveCategory(Categories category) {
           return categoryRepository.save(category);
    }

    @Override
    public Categories addCategory(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Categories category) {
        categoryRepository.delete(category);
    }

    @Override
    public Pictures addPicture(Pictures picture) {
        return pictureRepository.save(picture);
    }

    @Override
    public void deletePicture(Pictures picture) {
pictureRepository.delete(picture);
    }

    @Override
    public Pictures getPicture(Long id) {
        return  pictureRepository.getOne(id);
    }

    @Override
    public Sold addSold(Sold sold) {
        return soldRepository.save(sold);
    }

    @Override
    public List<Sold> getAllSolds() {
        return soldRepository.findAll();
    }

    @Override
    public Comments saveComment(Comments comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comments getComment(Long id) {
        return commentRepository.getOne(id);
    }

    @Override
    public Comments addComment(Comments comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Comments comment) {
        commentRepository.delete(comment);
    }

}
