package com.cytechpro.microservice.orderservice.service;

import com.cytechpro.microservice.orderservice.dto.InventoryResponse;
import com.cytechpro.microservice.orderservice.dto.OrderLineItemsDto;
import com.cytechpro.microservice.orderservice.dto.OrderRequest;
import com.cytechpro.microservice.orderservice.event.OrderPlacedEvent;
import com.cytechpro.microservice.orderservice.model.Order;
import com.cytechpro.microservice.orderservice.model.OrderLineItems;
import com.cytechpro.microservice.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {


    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

//    private  final KafkaTemplate<String, String> kafkaTemplate;

//    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder, KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
//        this.orderRepository = orderRepository;
//        this.webClientBuilder = webClientBuilder;
//        this.kafkaTemplate = kafkaTemplate;
//    }

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream()
                .map(this::mapToDto).toList();

        order.setOrderLineItemsList(orderLineItems);

        //Collect skuCodes from the incoming order item list
        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();


        //Check if the requested product is available in the inventory or not
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                        .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())

                                .retrieve()
                                        .bodyToMono(InventoryResponse[].class)
                                                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponses)
                .allMatch(inventoryResponse -> inventoryResponse.isInStock());

        //place the order only if the products are available
        if(allProductsInStock){
            orderRepository.save(order);

//            kafkaTemplate.send("notificationTopic", "Order Placed : " + order.getOrderNumber());
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }
}
