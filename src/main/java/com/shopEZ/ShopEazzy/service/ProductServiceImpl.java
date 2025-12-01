package com.shopEZ.ShopEazzy.service;

import com.shopEZ.ShopEazzy.exceptions.APIException;
import com.shopEZ.ShopEazzy.exceptions.ResourceNotFoundException;
import com.shopEZ.ShopEazzy.model.Category;
import com.shopEZ.ShopEazzy.model.Product;
import com.shopEZ.ShopEazzy.payload.ProductDTO;
import com.shopEZ.ShopEazzy.payload.ProductResponse;
import com.shopEZ.ShopEazzy.repository.CategoryRepository;
import com.shopEZ.ShopEazzy.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final String path;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ModelMapper modelMapper,
                              FileService fileService,
                              @Value("${project.image}") String path) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
        this.path = path;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        boolean isProductNotFound = true;
        List<Product> products = category.getProducts();
        for(Product prod: products){
            if(prod.getProductName().equals(productDTO.getProductName())){
                isProductNotFound = false;
                break;
            }
        }

        if(isProductNotFound){
            Product product = modelMapper.map(productDTO, Product.class);
            product.setProductName(productDTO.getProductName());
            product.setDescription(productDTO.getDescription());
            product.setImage("default.png");
            product.setQuantity(productDTO.getQuantity());
            product.setPrice(productDTO.getPrice());
            product.setDiscount(productDTO.getDiscount());
            double specialPrice = productDTO.getPrice() -
                    ((productDTO.getDiscount() * 0.01) * productDTO.getPrice());
            product.setSpecialPrice(specialPrice);
            product.setCategory(category);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }
        else{
            throw new APIException("Product already exists.");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findAll(pageDetails);

        List<Product> products = productsPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No products found.");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findByCategory(category, pageDetails);
        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new APIException("No products found for the category.");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new APIException("No products found for your search result: " + keyword);
        }
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setImage("default.png");
        product.setQuantity(productDTO.getQuantity());
        product.setPrice(productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount());
        double specialPrice = productDTO.getPrice() -
                ((productDTO.getDiscount() * 0.01) * productDTO.getPrice());
        product.setSpecialPrice(specialPrice);
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productID, MultipartFile image) throws IOException {

        Product existingProduct = productRepository.findById(productID)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productID));

        String fileName = fileService.uploadImage(path, image);
        existingProduct.setImage(fileName);
        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}
