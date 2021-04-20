package kz.springboot.springbootdemo.repositories;

import groovy.lang.Category;
import kz.springboot.springbootdemo.entities.Brands;
import kz.springboot.springbootdemo.entities.Categories;
import kz.springboot.springbootdemo.entities.Countries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Locale;

@Repository
@Transactional
public interface CategoryRepository extends JpaRepository<Categories, Long> {
}
