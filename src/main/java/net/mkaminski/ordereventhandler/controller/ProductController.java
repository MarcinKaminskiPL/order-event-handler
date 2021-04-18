package net.mkaminski.ordereventhandler.controller;

import net.mkaminski.ordereventhandler.model.Product;
import net.mkaminski.ordereventhandler.repo.ProductRepository;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    ResponseEntity<Optional<Product>> getProductById(@Validated @PathVariable String id) {
        Optional<Product> product = productRepository.findById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    ResponseEntity addProduct(@RequestBody Product product) {
        productRepository.save(product);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    ResponseEntity deleteProduct(@PathVariable String id){
        if(productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    @PutMapping("/{id}")
    ResponseEntity updateProduct(@RequestBody Product product){
        if(productRepository.existsById(product.getId())){
            productRepository.save(product);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
