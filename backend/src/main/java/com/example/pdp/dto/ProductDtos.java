package com.example.pdp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProductDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCoreDto {
        private String id;
        private String title;
        private String brand;
        private double price;
        private double originalPrice;
        private String description;
        private String heroImageUrl;
        private List<String> galleryImages;
        private List<String> specs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryDto {
        private String productId;
        private int stockQuantity;
        private boolean inStock;
        private String estimatedDelivery;
        private String warehouseLocation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewDto {
        private String id;
        private String author;
        private String avatarUrl;
        private int rating;
        private String date;
        private String commentHtml; // Dễ bị khai thác XSS nếu không qua sanitization
        private boolean verifiedPurchase;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonolithProductResponse {
        private ProductCoreDto core;
        private InventoryDto inventory;
        private List<ReviewDto> reviews;
        private long serverProcessingTimeMs;
    }
}
