package com.example.pdp.controller;

import com.example.pdp.dto.ProductDtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductMonolithController {

    // ==========================================
    // 1. Anti-Pattern Monolithic Endpoint (Phase 0)
    // Latency = 50ms + 200ms + 1500ms = ~1.75s Total Delay
    // ==========================================
    @GetMapping("/products/{id}/monolith")
    public ResponseEntity<MonolithProductResponse> getProductMonolith(@PathVariable String id) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        // 1. Fetch Core Info (~50ms)
        Thread.sleep(50);
        ProductCoreDto core = buildProductCore(id);

        // 2. Fetch Inventory (~200ms)
        Thread.sleep(200);
        InventoryDto inventory = buildInventory(id);

        // 3. Fetch Reviews (~1500ms blocking operation)
        Thread.sleep(1500);
        List<ReviewDto> reviews = buildReviews(id);

        long totalTime = System.currentTimeMillis() - startTime;

        MonolithProductResponse response = MonolithProductResponse.builder()
                .core(core)
                .inventory(inventory)
                .reviews(reviews)
                .serverProcessingTimeMs(totalTime)
                .build();

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 2. Individual Endpoints (Sẵn sàng cho Phase 2)
    // ==========================================
    @GetMapping("/products/{id}/core")
    public ResponseEntity<ProductCoreDto> getProductCore(@PathVariable String id) throws InterruptedException {
        Thread.sleep(50);
        return ResponseEntity.ok(buildProductCore(id));
    }

    @GetMapping("/inventory/{id}")
    public ResponseEntity<InventoryDto> getInventory(@PathVariable String id) throws InterruptedException {
        Thread.sleep(200);
        return ResponseEntity.ok(buildInventory(id));
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable String id) throws InterruptedException {
        Thread.sleep(1500);
        return ResponseEntity.ok(buildReviews(id));
    }

    // Mock Data Builders
    private ProductCoreDto buildProductCore(String id) {
        return ProductCoreDto.builder()
                .id(id)
                .title("Pro SoundX Wireless Noise-Canceling Headphones Ultra Max")
                .brand("SoundX Premium Tech")
                .price(299.99)
                .originalPrice(399.99)
                .description("Trải nghiệm âm thanh đỉnh cao với công nghệ chống ồn chủ động Hybrid ANC tiên tiến, thời lượng pin tới 60 giờ và chất liệu da protein siêu mềm mại.")
                // Anti-pattern LCP: Unoptimized 4K high-res uncompressed image
                .heroImageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=3500&auto=format&fit=crop")
                .galleryImages(Arrays.asList(
                        "https://images.unsplash.com/photo-1484704849700-f032a568e944?q=80&w=2000&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1546435770-a3e426bf472b?q=80&w=2000&auto=format&fit=crop"
                ))
                .specs(Arrays.asList(
                        "Driver: 45mm Custom Dynamic Drivers",
                        "Bluetooth: v5.4 với hỗ trợ LDAC & aptX Lossless",
                        "Pin: 60 giờ (Tắt ANC) / 45 giờ (Bật ANC)",
                        "Trọng lượng: 250g",
                        "Kháng nước: IPX4"
                ))
                .build();
    }

    private InventoryDto buildInventory(String id) {
        return InventoryDto.builder()
                .productId(id)
                .stockQuantity(14)
                .inStock(true)
                .estimatedDelivery("Giao hàng hỏa tốc trong 24 giờ")
                .warehouseLocation("Kho Trung Tâm TP. Hồ Chí Minh")
                .build();
    }

    private List<ReviewDto> buildReviews(String id) {
        return Arrays.asList(
                ReviewDto.builder()
                        .id("rev-101")
                        .author("Nguyễn Văn Hùng")
                        .avatarUrl("https://i.pravatar.cc/150?img=11")
                        .rating(5)
                        .date("10/08/2026")
                        .commentHtml("Chất lượng âm thanh cực kỳ tuyệt vời! Bass sâu và âm treble trong trẻo. Đáng đồng tiền bát gạo.")
                        .verifiedPurchase(true)
                        .build(),
                ReviewDto.builder()
                        .id("rev-102")
                        .author("Trần Thị Mai")
                        .avatarUrl("https://i.pravatar.cc/150?img=32")
                        .rating(4)
                        .date("15/08/2026")
                        .commentHtml("Tai nghe êm, chống ồn tốt nhưng hộp đựng hơi to một chút.")
                        .verifiedPurchase(true)
                        .build(),
                // Anti-pattern Security: Stored XSS payload intentionally included
                ReviewDto.builder()
                        .id("rev-103")
                        .author("Hacker Anonymous")
                        .avatarUrl("https://i.pravatar.cc/150?img=60")
                        .rating(1)
                        .date("20/08/2026")
                        .commentHtml("<b style='color:red'>Sản phẩm quá tệ!</b> <img src='invalid-image' onerror=\"alert('⚠️ LỖ HỔNG XSS ĐÃ BỊ KHAI THÁC QUA v-html!')\">")
                        .verifiedPurchase(false)
                        .build()
        );
    }
}
