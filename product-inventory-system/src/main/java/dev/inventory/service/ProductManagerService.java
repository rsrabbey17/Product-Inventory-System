package dev.inventory.service;

import dev.inventory.dto.ProductRequest;
import dev.inventory.dto.ProductResponse;
import dev.inventory.dto.RestockRequest;
import dev.inventory.entity.Product;
import dev.inventory.exception.ProductNotFoundException;
import dev.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductManagerService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException(sku));
        return mapToResponse(product);
    }

    public ProductResponse restockProduct(String sku, RestockRequest request) {
        return restockProduct(sku, request.getQuantityToAdd());
    }

    public ProductResponse restockProduct(String sku, int quantityToAdd) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException(sku));

        int newQuantity = product.getQuantity() + quantityToAdd;
        product.setQuantity(newQuantity);

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}