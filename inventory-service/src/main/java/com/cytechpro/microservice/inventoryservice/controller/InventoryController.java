package com.cytechpro.microservice.inventoryservice.controller;

import com.cytechpro.microservice.inventoryservice.dto.InventoryResponse;
import com.cytechpro.microservice.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    //http://localhost:8082/api/inventory/iphone-16, Headphones
    //http://localhost:8082/api/inventory/skuCode = iphone-16&skuCode = Headphones

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode){

        return inventoryService.isInStock(skuCode);


    }
}
