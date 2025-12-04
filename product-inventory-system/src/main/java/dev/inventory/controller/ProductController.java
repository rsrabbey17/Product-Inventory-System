package dev.inventory.controller;

import dev.inventory.dto.ProductRequest;
import dev.inventory.dto.ProductResponse;
import dev.inventory.dto.RestockRequest;
import dev.inventory.service.ProductManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductManagerService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        ProductResponse response = productService.findProductBySku(sku);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sku}/restock")
    public ResponseEntity<ProductResponse> restockProduct(
            @PathVariable String sku,
            @Valid @RequestBody RestockRequest request) {
        ProductResponse response = productService.restockProduct(sku, request);
        return ResponseEntity.ok(response);
    }
}