package dev.inventory.util;

public class ProductCalculator {

    public static double calculateDiscountedPrice(double originalPrice, double discountRate) {
        if (originalPrice < 0) {
            throw new IllegalArgumentException("Original price cannot be negative");
        }
        if (discountRate < 0 || discountRate > 100) {
            throw new IllegalArgumentException("Discount rate must be between 0 and 100");
        }

        double discountAmount = originalPrice * (discountRate / 100);
        return originalPrice - discountAmount;
    }

    public static boolean isQuantitySufficient(int currentQuantity, int requiredQuantity) {
        if (currentQuantity < 0) {
            throw new IllegalArgumentException("Current quantity cannot be negative");
        }
        if (requiredQuantity < 0) {
            throw new IllegalArgumentException("Required quantity cannot be negative");
        }

        return currentQuantity >= requiredQuantity;
    }
}