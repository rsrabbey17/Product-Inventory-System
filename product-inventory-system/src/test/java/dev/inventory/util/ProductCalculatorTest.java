package dev.inventory.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductCalculatorTest {

    @Test
    void testCalculateDiscountedPrice_ZeroDiscount() {
        double result = ProductCalculator.calculateDiscountedPrice(100.0, 0);
        assertEquals(100.0, result, 0.001);
    }

    @Test
    void testCalculateDiscountedPrice_FiftyPercentDiscount() {
        double result = ProductCalculator.calculateDiscountedPrice(100.0, 50);
        assertEquals(50.0, result, 0.001);
    }

    @Test
    void testCalculateDiscountedPrice_FullDiscount() {
        double result = ProductCalculator.calculateDiscountedPrice(100.0, 100);
        assertEquals(0.0, result, 0.001);
    }

    @ParameterizedTest
    @CsvSource({
            "100.0, 20, 80.0",
            "50.0, 10, 45.0",
            "75.0, 33.33, 50.0025",
            "0.0, 50, 0.0"
    })
    void testCalculateDiscountedPrice_VariousRates(double original, double rate, double expected) {
        double result = ProductCalculator.calculateDiscountedPrice(original, rate);
        assertEquals(expected, result, 0.001);
    }

    @Test
    void testCalculateDiscountedPrice_NegativeOriginalPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> ProductCalculator.calculateDiscountedPrice(-100.0, 20));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-10.0, 150.0})
    void testCalculateDiscountedPrice_InvalidDiscountRate(double invalidRate) {
        assertThrows(IllegalArgumentException.class,
                () -> ProductCalculator.calculateDiscountedPrice(100.0, invalidRate));
    }

    @Test
    void testIsQuantitySufficient_SufficientStock() {
        boolean result = ProductCalculator.isQuantitySufficient(100, 50);
        assertTrue(result);
    }

    @Test
    void testIsQuantitySufficient_InsufficientStock() {
        boolean result = ProductCalculator.isQuantitySufficient(30, 50);
        assertFalse(result);
    }

    @Test
    void testIsQuantitySufficient_ExactMatch() {
        boolean result = ProductCalculator.isQuantitySufficient(50, 50);
        assertTrue(result);
    }

    @Test
    void testIsQuantitySufficient_ZeroRequired() {
        boolean result = ProductCalculator.isQuantitySufficient(100, 0);
        assertTrue(result);
    }

    @Test
    void testIsQuantitySufficient_NegativeCurrentQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> ProductCalculator.isQuantitySufficient(-10, 5));
    }

    @Test
    void testIsQuantitySufficient_NegativeRequiredQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> ProductCalculator.isQuantitySufficient(100, -5));
    }
}