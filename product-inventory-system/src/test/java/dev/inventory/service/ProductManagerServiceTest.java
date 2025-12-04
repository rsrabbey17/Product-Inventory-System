package dev.inventory.service;

import dev.inventory.dto.ProductRequest;
import dev.inventory.dto.ProductResponse;
import dev.inventory.dto.RestockRequest;
import dev.inventory.entity.Product;
import dev.inventory.exception.ProductNotFoundException;
import dev.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductManagerServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductManagerService productService;

    private Product testProduct;
    private final String TEST_SKU = "SKU12345";

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .sku(TEST_SKU)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .quantity(50)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findProductBySku_ShouldReturnProduct_WhenProductExists() {
        when(productRepository.findBySku(TEST_SKU)).thenReturn(Optional.of(testProduct));

        ProductResponse result = productService.findProductBySku(TEST_SKU);

        assertNotNull(result);
        assertEquals(TEST_SKU, result.getSku());
        assertEquals("Test Product", result.getName());
        assertEquals(50, result.getQuantity());
        verify(productRepository, times(1)).findBySku(TEST_SKU);
    }

    @Test
    void findProductBySku_ShouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.findProductBySku("NONEXISTENT"));

        verify(productRepository, times(1)).findBySku("NONEXISTENT");
    }

    @Test
    void restockProduct_ShouldUpdateQuantity_WhenProductExists() {
        when(productRepository.findBySku(TEST_SKU)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        int quantityToAdd = 25;
        RestockRequest request = RestockRequest.builder()
                .quantityToAdd(quantityToAdd)
                .build();

        ProductResponse result = productService.restockProduct(TEST_SKU, request);

        assertNotNull(result);
        verify(productRepository, times(1)).findBySku(TEST_SKU);
        verify(productRepository, times(1)).save(testProduct);
        assertEquals(75, testProduct.getQuantity()); // 50 + 25
    }

    @Test
    void restockProduct_ShouldCallSaveExactlyOnce_WhenRestocking() {
        when(productRepository.findBySku(TEST_SKU)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        RestockRequest request = RestockRequest.builder()
                .quantityToAdd(10)
                .build();

        productService.restockProduct(TEST_SKU, request);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void restockProduct_ShouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        RestockRequest request = RestockRequest.builder()
                .quantityToAdd(10)
                .build();

        assertThrows(ProductNotFoundException.class,
                () -> productService.restockProduct("NONEXISTENT", request));

        verify(productRepository, times(1)).findBySku("NONEXISTENT");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldCreateNewProduct() {
        ProductRequest request = ProductRequest.builder()
                .sku("NEW-SKU-001")
                .name("New Product")
                .price(new BigDecimal("29.99"))
                .quantity(100)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductResponse result = productService.createProduct(request);

        assertNotNull(result);
        verify(productRepository, times(1)).save(any(Product.class));
    }
}