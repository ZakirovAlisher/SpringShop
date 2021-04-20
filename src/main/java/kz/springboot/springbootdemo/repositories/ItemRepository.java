package kz.springboot.springbootdemo.repositories;

import kz.springboot.springbootdemo.entities.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ItemRepository extends JpaRepository<Items, Long> {


    List<Items> findAllByNameLike(String name);

    List<Items> findAllByNameLikeOrderByPriceAsc(String name);
    List<Items> findAllByNameLikeOrderByPriceDesc(String name);

    List<Items> findAllByNameLikeAndPriceBetweenOrderByPriceAsc(String name, double price1, double price2);
    List<Items> findAllByNameLikeAndPriceBetweenOrderByPriceDesc(String name, double price1, double price2);

    List<Items> findAllByBrandNameLike(String br);



    List<Items> findAllByNameAndBrandIdLikeOrderByPriceAsc(String s, Long br_id);

    List<Items> findAllByNameAndBrandIdLikeOrderByPriceDesc(String s, Long br_id);

    List<Items> findAllByNameAndBrandIdLikeAndPriceBetweenOrderByPriceAsc(String s, double price1, double price2, Long br_id);

    List<Items> findAllByNameAndBrandIdLikeAndPriceBetweenOrderByPriceDesc(String s, double price1, double price2, Long br_id);
}
