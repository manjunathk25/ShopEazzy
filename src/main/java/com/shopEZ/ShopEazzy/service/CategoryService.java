package com.shopEZ.ShopEazzy.service;

import com.shopEZ.ShopEazzy.payload.CategoryDTO;
import com.shopEZ.ShopEazzy.payload.CategoryResponse;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO createNewCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);

}
