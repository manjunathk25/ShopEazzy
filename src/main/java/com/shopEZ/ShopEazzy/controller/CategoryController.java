package com.shopEZ.ShopEazzy.controller;

import com.shopEZ.ShopEazzy.model.Category;
import com.shopEZ.ShopEazzy.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<List<Category>> getCategories(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getAllCategories());
    }

    @PostMapping("/admin/category")
    public ResponseEntity<String> createNewCategory(@Valid @RequestBody Category category){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createNewCategory(category));
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable long categoryId){
        String status = categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable long categoryId,
                                 @Valid @RequestBody Category category){
        String status = categoryService.updateCategory(categoryId, category);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }
}
