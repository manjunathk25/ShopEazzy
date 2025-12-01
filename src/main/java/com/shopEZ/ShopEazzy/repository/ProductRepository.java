package com.shopEZ.ShopEazzy.repository;

import com.shopEZ.ShopEazzy.model.Category;
import com.shopEZ.ShopEazzy.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(Category category, Pageable pageDetails);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);
}
