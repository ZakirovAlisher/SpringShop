package kz.springboot.springbootdemo.repositories;

import kz.springboot.springbootdemo.entities.Brands;
import kz.springboot.springbootdemo.entities.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CommentRepository extends JpaRepository<Comments, Long> {
}
