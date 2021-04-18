package net.mkaminski.ordereventhandler.controller;

import net.mkaminski.ordereventhandler.model.Order;
import net.mkaminski.ordereventhandler.repo.OrderRepository;
import net.mkaminski.ordereventhandler.repo.ProductRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderRepository.findAll());
    }


    @PostMapping
    ResponseEntity<Order> addOrder(@RequestBody Order order) {
        order.setCreatedTime(LocalDateTime.now());
        orderRepository.save(order);
        return ResponseEntity.created(URI.create("/" + order.getId())).body(order);
    }

    @DeleteMapping("/{id}")
    ResponseEntity deleteOrder(@PathVariable String id) {
        if (orderRepository.existsById(id)) {
            System.out.println("w ifie delete");
            orderRepository.deleteById(id);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    ResponseEntity updateOrder(@PathVariable String id) {
        if (orderRepository.existsById(id)) {
            Order order = orderRepository.findById(id).get();
            order.getProducts()
                    .forEach(product -> {
                        if (productRepository.existsById(product.getId())) {
                            int newPrice = productRepository.findById(product.getId()).get().getPrice();
                            product.setPrice(newPrice);
                        }
                    });
            orderRepository.save(order);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/betweendates")
    ResponseEntity findOrdersBetweenDates(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<Order> response = new ArrayList<>();
        orderRepository.findAll().forEach(order -> {
            if (order.getCreatedTime().isAfter(from) && order.getCreatedTime().isBefore(to)) {
                response.add(order);
            }
        });
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
