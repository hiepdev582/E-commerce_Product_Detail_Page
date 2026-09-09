# 🛒 E-Commerce Product Detail Page - Performance & Security Lab

Dự án thực hành về **Web Performance** và **Web Security** thông qua việc xây dựng trang **Chi tiết Sản phẩm (Product Detail Page)** sử dụng **Nuxt 3** (Frontend) và **Spring Boot** (Backend API).

---

## 🎯 Mục Tiêu Dự Án

Trang Chi tiết Sản phẩm nghe qua đơn giản nhưng lại là nơi tập trung các bài toán hiệu năng phức tạp nhất trong thực tế:

- **Hero Image khổng lồ** gây tụt LCP.
- **Dữ liệu phân tán nhiều nguồn** từ Spring Boot Microservices/APIs (Thông tin sản phẩm, Tồn kho real-time, Bình luận & Đánh giá) gây nghẽn TTFB.
- **Dynamic UI & tính toán phức tạp** (Bộ lọc đánh giá, tính phí vận chuyển real-time) làm nghẽn Main Thread của Browser, tăng INP.
- **Streaming UI muộn & Font chữ load chậm** làm giật khung hình (CLS & FOUT).

---

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

### **Frontend:**

- **Framework:** Nuxt 3 (Vue 3, Composition API, TypeScript)
- **Engine & Modules:** Nitro Engine, `@nuxt/image`, `@nuxt/fonts`
- **Architecture:** Nuxt Streaming SSR, Vue `<Suspense>`, Nuxt Islands (`<NuxtIsland>`)
- **Styling:** Tailwind CSS / Vanilla CSS

### **Backend:**

- **Framework:** Spring Boot 3+ (Java 17 / 21)
- **Modules:** Spring Web / Spring WebFlux (Reactive Streams), Spring Security
- **Database & Cache:** PostgreSQL / MySQL, Redis Cache (cho Inventory & Core Info)

### **Testing & Measurement:**

- Chrome DevTools (Performance & Network Tab), PageSpeed Insights, WebVitals extension, OWASP ZAP.

---

## 🚀 Các Thử Thách Kỹ Thuật (Detailed Challenges)

### 1. Tối ưu LCP & Critical Rendering Path

- **Thử thách:** Ảnh chính của sản phẩm (Hero Image) đang là nhân tố chính làm giảm điểm LCP.
- **Nhiệm vụ:**
  - [ ] Cấu hình CDN và mô phỏng module `@nuxt/image` (`<NuxtImg>`) để tự động convert format tối ưu nhất (**AVIF / WebP**), sinh `srcset` và responsive breakpoints.
  - [ ] Gắn `fetchpriority="high"` và tag `<link rel="preload">` đúng cách cho ảnh LCP chính trong Nuxt `useHead()`.
  - [ ] Cấu hình Preconnect (`<link rel="preconnect">`) hoặc `dns-prefetch` tới domain CDN chứa ảnh ngoại vi; tận dụng tối đa HTTP/2 hoặc HTTP/3.

### 2. Áp dụng Streaming SSR & Tối ưu TTFB

- **Thử thách:** Spring Boot Backend cung cấp 3 API endpoints có độ trễ (latency) khác nhau:
  1. **Core Info API (`/api/products/{id}`):** Nhanh (~50ms)
  2. **Inventory API (`/api/inventory/{id}`):** Trung bình (~200ms)
  3. **Reviews API (`/api/reviews/{id}`):** Chậm (~1.5s)
- **Nhiệm vụ:**
  - [ ] Loại bỏ pattern SSR truyền thống (khiến Nuxt phải chờ cả 3 Spring Boot APIs xong mới trả HTML, kéo TTFB lên 1.5s).
  - [ ] Áp dụng **Nuxt Streaming SSR + Vue `<Suspense>`** (`useAsyncData` với `lazy: true` hoặc Async Components): Bắn ngay Shell HTML + Core Info về Client trong **< 100ms**, sau đó stream dần dữ liệu Tồn kho và Bình luận khi Spring Boot hoàn tất xử lý.

### 3. Tối ưu INP (Interaction to Next Paint) & Main Thread

- **Thử thách:** Nhúng bộ lọc đánh giá phức tạp hoặc khung tính phí vận chuyển real-time. Khi người dùng nhập/click chọn option trên Vue UI, giao diện bị giật lag do xuất hiện **Long Tasks (> 50ms)**.
- **Nhiệm vụ:**
  - [ ] Sử dụng tab **Performance** trong Chrome DevTools để soi Main Thread và phát hiện điểm nghẽn execution của Vue Reactivity System / JS computation.
  - [ ] Áp dụng kỹ thuật chia nhỏ Long Task (**Long Task Chunking**): Tận dụng `scheduler.yield()` (hoặc fallback `setTimeout` / `requestAnimationFrame` / Web Worker) để nhường quyền xử lý cho Main Thread render frame tiếp theo.
  - [ ] Đảm bảo chỉ số **INP < 200ms** mượt mà cho mọi tương tác người dùng.

### 4. Triệt hạ CLS & Hydration Cost

- **Thử thách:** Khung bình luận stream về sau đẩy nội dung phía dưới xuống gây nhảy layout (**CLS**). Font chữ load muộn làm đổi kiểu chữ đột ngột (**FOUT**).
- **Nhiệm vụ:**
  - [ ] Reserve cố định không gian cho các dynamic UI component bằng **Vue Skeleton** sở hữu `aspect-ratio` chuẩn.
  - [ ] Cấu hình **Font Loading Strategy** (`font-display: swap` kết hợp `@nuxt/fonts` hoặc `size-adjust`) để triệt tiêu CLS do font gây ra.
  - [ ] _(Nâng cao)_ Áp dụng **Nuxt Islands** (`<NuxtIsland>`) / Server Components để đạt **0KB JavaScript Client** cho các phần nội dung tĩnh (như mô tả sản phẩm, thông số kỹ thuật).

---

## 🛡️ Thử Thách Mở Rộng: Web Security Best Practices

Để trang PDP đạt tiêu chuẩn an toàn bảo mật Production (Nuxt + Spring Boot):

- [ ] **Content Security Policy (CSP):** Cấu hình CSP headers chặt chẽ thông qua Nuxt Security Module hoặc Spring Security (`http.headers().contentSecurityPolicy(...)`).
- [ ] **XSS Prevention:** Sanitization triệt để dữ liệu đánh giá/bình luận ở cả Spring Boot (OWASP Java HTML Sanitizer) và Nuxt Frontend (`DOMPurify` / `v-html` protection).
- [ ] **Secure HTTP Headers:** Bổ sung `X-Frame-Options`, `X-Content-Type-Options`, `Referrer-Policy`, `Strict-Transport-Security` (HSTS) và cấu hình CORS chặt chẽ giữa Nuxt và Spring Boot APIs.

---

## 📊 Tiêu Chí Đánh Giá (Target Web Vitals Metrics)

| Chỉ số (Metric)                     | Mục tiêu (Target) | Trạng thái   |
| :---------------------------------- | :---------------- | :----------- |
| **TTFB** (Time to First Byte)       | `< 100ms`         | 🟢 Ideal     |
| **LCP** (Largest Contentful Paint)  | `< 1.2s`          | 🟢 Ideal     |
| **INP** (Interaction to Next Paint) | `< 200ms`         | 🟢 Ideal     |
| **CLS** (Cumulative Layout Shift)   | `= 0.00`          | 🟢 Perfect   |
| **Lighthouse Performance Score**    | `> 95+`           | 🟢 Excellent |
| **Lighthouse Security Score**       | `100% Pass`       | 🟢 Secure    |

---

## 📂 Thư Mục Đề Xuất (Suggested Project Structure)

```text
E-commerce_Product_Detail_Page/
├── frontend/             # Nuxt 3 App
│   ├── app.vue
│   ├── pages/
│   │   └── products/[id].vue  # PDP Page with Streaming SSR & Suspense
│   ├── components/
│   │   ├── ProductHero.vue    # Optimized LCP Image Component
│   │   ├── ProductSpecs.server.vue # Nuxt Island / Server Component (0kb JS)
│   │   ├── ShippingCalc.vue   # INP Optimized Component (Long Task chunking)
│   │   └── ProductReviews.vue # Lazy Streamed Component + Skeleton
│   ├── nuxt.config.ts         # Nuxt Modules (@nuxt/image, @nuxt/fonts, Security)
│   └── package.json
│
└── backend/        # Spring Boot 3 API Service
    ├── src/main/java/com/example/pdp/
    │   ├── controller/
    │   │   ├── ProductController.java   # Fast API (~50ms)
    │   │   ├── InventoryController.java # Medium API (~200ms)
    │   │   └── ReviewController.java    # Slow API (~1.5s simulation)
    │   ├── config/
    │   │   └── SecurityConfig.java      # Spring Security & CSP Headers
    │   └── PdpApplication.java
    └── pom.xml / build.gradle
```
