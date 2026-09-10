# 📋 CHECKLIST THỰC HÀNH PERFORMANCE & SECURITY LAB (NUXT 3 + SPRING BOOT)

---

## 📉 PHASE 0: DỰNG PHIÊN BẢN CHƯA TỐI ƯU (ANTI-PATTERNS BASELINE)

_Mục tiêu: Cố tình viết code "tệ nhất có thể" để đo chỉ số ban đầu._

- [ ] **LCP Tệ (Hero Image):**
  - [ ] Dùng ảnh Hero trực tiếp dạng PNG/JPEG gốc chưa nén (> 4MB - 8MB).
  - [ ] Dùng thẻ `<img>` thường, không set `width`/`height`.
  - [ ] Không dùng `<NuxtImg>`, không set `fetchpriority="high"`, không `<link rel="preload">`.
  - [ ] Load qua URL CDN/Domain ngoài không có `<link rel="preconnect">`.

- [ ] **TTFB Tệ (Spring Boot API & Nuxt SSR):**
  - [ ] Tạo 1 API Spring Boot gộp cả 3 luồng: `Core Info` (50ms) + `Inventory` (200ms) + `Reviews` (`Thread.sleep(1500)`). Tổng latency = 1.75s.
  - [ ] Nuxt dùng `await useFetch()` chặn toàn bộ SSR rendering trên Server, khiến Browser nhận HTML muộn > 1.8s.

- [ ] **INP Tệ (Main Thread Blocking):**
  - [ ] Viết tính năng lọc bình luận hoặc tính phí ship real-time chạy loop đồng bộ 100.000 items ngay khi `keyup`/`click` mà không `debounce` hay `yield`.
  - [ ] Tạo ra Long Task > 200ms - 500ms làm đóng băng UI.

- [ ] **CLS & FOUT Tệ (Layout Shifts & Font Loading):**
  - [ ] Khung Bình luận stream về sau đẩy nội dung phía dưới xuống (không reserve width/height hoặc skeleton).
  - [ ] Import Google Font trong CSS dạng `@import` không có `font-display: swap` hoặc `size-adjust` gây FOUT/FOIT.

- [ ] **Security Yếu:**
  - [ ] Không cấu hình Header CSP (`Content-Security-Policy`).
  - [ ] Render đánh giá người dùng trực tiếp bằng `v-html` không qua Sanitization (dễ dính XSS).
  - [ ] Cấu hình CORS `Access-Control-Allow-Origin: *`.

---

## 📌 PHASE 1: ĐO ĐẠC & GHI NHẬN CHỈ SỐ BAN ĐẦU

- [x] Chạy Chrome DevTools Lighthouse (Mobile & Desktop).
- [x] Chạy Performance Tab & Network Tab trong Chrome DevTools.
- [x] Dùng Web Vitals Extension đo chỉ số thực tế.
- [x] Ghi lại kết quả vào dòng **0. Unoptimized Baseline** ở Bảng Tiến Độ.

---

## 📌 PHASE 2: TỐI ƯU LCP & CRITICAL RENDERING PATH

- [x] Cài đặt module `@nuxt/image` và chuyển ảnh Hero sang format **AVIF / WebP**.
- [x] Thêm responsive breakpoints (`sizes` và `srcset`) cho ảnh Hero.
- [x] Thêm `fetchpriority="high"` vào thẻ `<NuxtImg>` hoặc cấu hình `useHead()` preload.
- [x] Thêm `<link rel="preconnect">` & `<link rel="dns-prefetch">` tới Domain chứa ảnh.
- [x] Đo lại điểm **LCP** và Resource Load Time.

---

## 📌 PHASE 3: TỐI ƯU TTFB & STREAMING SSR

- [x] Tách Spring Boot API thành 3 REST endpoints riêng biệt:
  - [x] `GET /api/products/{id}/core` (Nhanh - ~50ms)
  - [x] `GET /api/inventory/{id}` (Trung bình - ~200ms)
  - [x] `GET /api/reviews/{id}` (Chậm - ~1.5s simulation)
- [x] Chuyển Nuxt SSR sang dạng **Streaming SSR + Vue `<Suspense>`** (`lazy: true` hoặc Async Components).
- [x] Đảm bảo HTML Shell + Core Info được trả về Client dưới **< 100ms**.
- [x] Đo lại **TTFB** trên Network Tab (Network Waterfall).

---

## 📌 PHASE 4: TỐI ƯU INP & GIẢI PHÓNG MAIN THREAD

- [x] Mở Performance Tab, soi các dải đỏ **Long Task (> 50ms)** khi tương tác UI.
- [x] Thêm **Debounce / Throttle** cho ô nhập liệu tính phí ship real-time.
- [x] Áp dụng **Long Task Chunking**: Dùng `scheduler.yield()` (hoặc fallback `setTimeout(..., 0)` / `requestAnimationFrame` / Web Worker) để chia nhỏ bài toán tính toán 100.000 items.
- [x] Đo lại chỉ số **INP < 200ms**.

---

## 📌 PHASE 5: TRIỆT HẠ CLS & HYDRATION COST

- [x] Tạo **Vue Skeleton Components** với `aspect-ratio` và chiều cao cố định cho khung Bình luận & Sản phẩm liên quan.
- [x] Cấu hình `@nuxt/fonts` hoặc `font-display: swap` kết hợp `size-adjust` triệt tiêu FOUT/FOIT.
- [x] Chuyển phần Thông số kỹ thuật / Mô tả sản phẩm tĩnh sang **Nuxt Islands** (`<NuxtIsland>` hoặc `.server.vue`) để đạt **0KB Client JS**.
- [x] Đo lại chỉ số **CLS = 0.00**.

---

## 📌 PHASE 6: GIA CỦNG WEB SECURITY (SECURITY HARDENING)

- [ ] Cấu hình **Content Security Policy (CSP)** trong Nuxt Config hoặc Spring Security.
- [ ] Lọc dữ liệu đầu vào bình luận bằng `DOMPurify` (Frontend) hoặc `OWASP Java HTML Sanitizer` (Backend).
- [ ] Thêm đầy đủ Security Headers: `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `Referrer-Policy`, `Strict-Transport-Security`.
- [ ] Giới hạn CORS domain cụ thể thay vì `*`.
- [ ] Audit lại bằng Lighthouse Security & SecurityHeaders.com.

---

## 📊 BẢNG THEO DÕI TIẾN ĐỘ CHỈ SỐ WEB VITALS

| Giai Đoạn (Phase)             | TTFB        | LCP        | INP         | CLS      | Bundle Size (JS) | Lighthouse Perf | Lighthouse Security |
| :---------------------------- | :---------- | :--------- | :---------- | :------- | :--------------- | :-------------- | :------------------ |
| **0. Unoptimized Baseline**   |             |            |             |          |                  |                 |                     |
| **1. Sau Tối Ưu LCP**         |             |            |             |          |                  |                 |                     |
| **2. Sau Streaming SSR**      |             |            |             |          |                  |                 |                     |
| **3. Sau Tối Ưu INP**         |             |            |             |          |                  |                 |                     |
| **4. Sau Triệt CLS & RSC**    |             |            |             |          |                  |                 |                     |
| **5. Sau Security Hardening** |             |            |             |          |                  |                 |                     |
| **🎯 Mục Tiêu (Target)**      | **< 100ms** | **< 1.2s** | **< 200ms** | **0.00** | **Tối thiểu**    | **> 95+**       | **100% Pass**       |
