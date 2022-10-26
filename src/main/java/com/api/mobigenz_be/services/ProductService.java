package com.api.mobigenz_be.services;

import com.api.mobigenz_be.DTOs.ProductDto;
import com.api.mobigenz_be.entities.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {

    List<ProductDto> getProducts();
    List<Product> getProducts2();
    ProductDto insertProduct(ProductDto productDto);
}
