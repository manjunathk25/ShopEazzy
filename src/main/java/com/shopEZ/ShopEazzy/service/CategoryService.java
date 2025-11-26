package com.shopEZ.ShopEazzy.service;

import com.shopEZ.ShopEazzy.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    String createNewCategory(Category category);

    String deleteCategory(Long categoryId);

    String updateCategory(Long categoryId, Category category);

}
