package com.shopEZ.ShopEazzy.service;

import com.shopEZ.ShopEazzy.exceptions.APIException;
import com.shopEZ.ShopEazzy.exceptions.ResourceNotFoundException;
import com.shopEZ.ShopEazzy.model.Category;
import com.shopEZ.ShopEazzy.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new APIException("No categories found");
        }
        return categories;
    }

    @Override
    public String createNewCategory(Category category) {
        Category existingCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if(existingCategory != null){
            throw new APIException("Category name with " + existingCategory.getCategoryName() + " already exists.");
        }
        Category newCategory = new Category();
        newCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(newCategory);
        return newCategory.getCategoryName() + " " + "category added successfully";
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));
        categoryRepository.delete(category);
        return category.getCategoryName() + " category deleted successfully";
    }

    @Override
    public String updateCategory(Long categoryId, Category category) {
        Category updateCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));
        Category existingCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if(existingCategory != null){
            throw new APIException("Category name with " + existingCategory.getCategoryName() + " already exists.");
        }
        updateCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(updateCategory);
        return "category with id " + categoryId + " updated successfully";
    }
}
