package org.example.ordersevice.controller;

import lombok.RequiredArgsConstructor;
import org.example.ordersevice.model.Order;
import org.example.ordersevice.repository.OrderRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final  OrderRepo orderRepo;
    @PostMapping
    public Order createOrder(@RequestBody Order order){
        return orderRepo.save(order);
    }
    @GetMapping
    public List<Order> getOrder(){
        return orderRepo.findAll();
    }



}
