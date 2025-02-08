package com.cytechpro.microservice.productservice.repository;

import com.cytechpro.microservice.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {


}
