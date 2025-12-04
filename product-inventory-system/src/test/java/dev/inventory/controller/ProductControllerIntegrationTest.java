package dev.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.inventory.dto.ProductRequest;
import dev.inventory.dto.RestockRequest;
import dev.inventory.entity.Product;
import dev.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        testProduct = Product.builder()
                .sku("INTEGRATION-SKU-001")
                .name("Integration Test Product")
                .price(new BigDecimal("49.99"))
                .quantity(100)
                .build();

        testProduct = productRepository.save(testProduct);
    }

    @Test
    void getProductBySku_ShouldReturnProduct_WhenExists() throws Exception {
        mockMvc.perform(get("/api/products/{sku}", testProduct.getSku()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku", is(testProduct.getSku())))
                .andExpect(jsonPath("$.name", is(testProduct.getName())))
                .andExpect(jsonPath("$.quantity", is(testProduct.getQuantity())));
    }

    @Test
    void getProductBySku_ShouldReturn404_WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/products/{sku}", "NONEXISTENT-SKU"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_ShouldCreateProduct_WhenValidRequest() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .sku("NEW-SKU-001")
                .name("New Product")
                .price(new BigDecimal("29.99"))
                .quantity(50)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku", is(request.getSku())))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.quantity", is(request.getQuantity())));
    }

    @Test
    void restockProduct_ShouldUpdateQuantity_WhenProductExists() throws Exception {
        RestockRequest restockRequest = RestockRequest.builder()
                .quantityToAdd(25)
                .build();

        int expectedNewQuantity = testProduct.getQuantity() + restockRequest.getQuantityToAdd();

        mockMvc.perform(put("/api/products/{sku}/restock", testProduct.getSku())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(expectedNewQuantity)));
    }

    @Test
    void restockProduct_ShouldReturn404_WhenProductNotExists() throws Exception {
        RestockRequest restockRequest = RestockRequest.builder()
                .quantityToAdd(10)
                .build();

        mockMvc.perform(put("/api/products/{sku}/restock", "NONEXISTENT-SKU")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restockRequest)))
                .andExpect(status().isNotFound());
    }
}