package com.cytechpro.microservice.productservice.service;

import com.cytechpro.microservice.productservice.dto.ProductRequest;
import com.cytechpro.microservice.productservice.dto.ProductResponse;
import com.cytechpro.microservice.productservice.model.Product;
import com.cytechpro.microservice.productservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(ProductRequest productRequest){

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product  {} is saved", product.getName());
    }


    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(product -> mapToProductResponse(product)).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
