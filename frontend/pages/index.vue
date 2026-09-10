<template>
  <div class="min-h-screen bg-slate-950 text-slate-100 pb-20 selection:bg-emerald-500 selection:text-black">
    <!-- Glassmorphic Navigation Header -->
    <header class="border-b border-slate-800 bg-slate-950/80 backdrop-blur-xl sticky top-0 z-50 shadow-2xl">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 h-20 flex items-center justify-between gap-4">
        <!-- Logo & Brand -->
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-2xl bg-gradient-to-tr from-indigo-500 via-purple-500 to-emerald-400 flex items-center justify-center text-white font-black text-xl shadow-lg shadow-indigo-500/20">
            A
          </div>
          <div>
            <span class="text-2xl font-black tracking-tight text-white heading-font bg-clip-text text-transparent bg-gradient-to-r from-white via-slate-200 to-slate-400">
              AudioLuxe
            </span>
            <div class="text-[10px] font-mono text-emerald-400 tracking-widest uppercase">Performance Lab</div>
          </div>
        </div>

        <!-- Interactive Phase Switcher Tabs -->
        <div class="flex items-center bg-slate-900/90 p-1.5 rounded-2xl border border-slate-800 text-xs font-semibold shadow-inner overflow-x-auto">
          <button
            @click="switchPhase(0)"
            :class="[
              'px-3.5 py-2 rounded-xl transition-all duration-300 flex items-center gap-2 whitespace-nowrap',
              activePhase === 0
                ? 'bg-rose-600 text-white shadow-lg shadow-rose-600/30 font-bold'
                : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
            ]"
          >
            <span class="w-2 h-2 rounded-full bg-rose-400"></span>
            🔴 Phase 0 (Baseline)
          </button>

          <button
            @click="switchPhase(2)"
            :class="[
              'px-3.5 py-2 rounded-xl transition-all duration-300 flex items-center gap-2 whitespace-nowrap',
              activePhase === 2
                ? 'bg-amber-500 text-black shadow-lg shadow-amber-500/30 font-bold'
                : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
            ]"
          >
            <span class="w-2 h-2 rounded-full bg-amber-400"></span>
            🖼️ Phase 2 (LCP Only)
          </button>

          <button
            @click="switchPhase(3)"
            :class="[
              'px-3.5 py-2 rounded-xl transition-all duration-300 flex items-center gap-2 whitespace-nowrap',
              activePhase === 3
                ? 'bg-emerald-500 text-black shadow-lg shadow-emerald-500/30 font-bold'
                : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
            ]"
          >
            <span class="w-2 h-2 rounded-full bg-emerald-400"></span>
            ⚡ Phase 3 (Streaming SSR)
          </button>

          <button
            @click="switchPhase(4)"
            :class="[
              'px-3.5 py-2 rounded-xl transition-all duration-300 flex items-center gap-2 whitespace-nowrap',
              activePhase === 4
                ? 'bg-indigo-500 text-white shadow-lg shadow-indigo-500/30 font-bold'
                : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
            ]"
          >
            <span class="w-2 h-2 rounded-full bg-indigo-300"></span>
            🧠 Phase 4 (INP & Yielding)
          </button>

          <button
            @click="switchPhase(5)"
            :class="[
              'px-3.5 py-2 rounded-xl transition-all duration-300 flex items-center gap-2 whitespace-nowrap',
              activePhase === 5
                ? 'bg-emerald-400 text-black shadow-lg shadow-emerald-400/30 font-bold'
                : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
            ]"
          >
            <span class="w-2 h-2 rounded-full bg-emerald-300 animate-ping"></span>
            🎯 Phase 5 (CLS=0.00 & 0KB JS)
          </button>
        </div>

        <!-- Telemetry HUD Badge -->
        <div class="hidden lg:flex items-center gap-3 bg-slate-900/60 border border-slate-800 px-4 py-2 rounded-2xl text-xs">
          <div class="text-right">
            <div class="text-[10px] text-slate-400 uppercase font-mono">Strategy</div>
            <div :class="['font-bold', activePhase >= 3 ? 'text-emerald-400' : 'text-rose-400']">
              {{ activePhase >= 3 ? 'Fast Core SSR (~50ms)' : 'Monolithic SSR (~1.8s)' }}
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto px-4 sm:px-6 pt-8 space-y-8">
      <!-- Error Alert -->
      <div v-if="monolithError && activePhase < 3" class="p-5 bg-rose-950/80 text-rose-300 rounded-2xl border border-rose-800 flex items-center gap-3">
        <span class="text-2xl">⚠️</span>
        <div>
          <h4 class="font-bold text-white">Không thể kết nối tới Backend Spring Boot API</h4>
          <p class="text-xs text-rose-400">Kiểm tra kết quả chạy Docker hoặc backend service ({{ config.public.apiBase }}).</p>
        </div>
      </div>
      <div v-else-if="coreError && activePhase >= 3" class="p-5 bg-rose-950/80 text-rose-300 rounded-2xl border border-rose-800 flex items-center gap-3">
        <span class="text-2xl">⚠️</span>
        <div>
          <h4 class="font-bold text-white">Không thể kết nối tới Spring Boot Core API</h4>
          <p class="text-xs text-rose-400">Đảm bảo backend Spring Boot đã khởi chạy thành công.</p>
        </div>
      </div>

      <!-- Main Container -->
      <div v-else class="space-y-8">
        
        <!-- PHASE BANNERS -->
        <!-- Phase 0 Banner -->
        <div
          v-if="activePhase === 0"
          class="bg-gradient-to-r from-rose-950 via-slate-900 to-slate-950 border border-rose-500/40 rounded-3xl p-6 shadow-2xl space-y-3"
        >
          <div class="flex items-center justify-between flex-wrap gap-2">
            <h2 class="font-bold text-lg text-rose-400 flex items-center gap-2">
              🔴 Phase 0: Baseline chưa tối ưu (Anti-Patterns Version)
            </h2>
            <span class="text-xs bg-rose-500/20 text-rose-300 border border-rose-500/40 px-3 py-1 rounded-full font-mono">
              TTFB ~1.8s | LCP ~4.8s | INP Blocking | Raw 4K Image
            </span>
          </div>
          <p class="text-xs text-slate-300 leading-relaxed">
            Phiên bản này ngâm Server trong ~1.75s, tải ảnh 4K thô >4MB không nén, vòng lặp blocking 100.000 phần tử trên Main Thread và dính lỗ hổng Stored XSS qua <code class="bg-black/40 px-1 rounded text-rose-300">v-html</code>.
          </p>
        </div>

        <!-- Phase 2 Banner -->
        <div
          v-if="activePhase === 2"
          class="bg-gradient-to-r from-amber-950 via-slate-900 to-slate-950 border border-amber-500/40 rounded-3xl p-6 shadow-2xl space-y-3"
        >
          <div class="flex items-center justify-between flex-wrap gap-2">
            <h2 class="font-bold text-lg text-amber-400 flex items-center gap-2">
              🖼️ Phase 2: Tối Ưu LCP & Critical Path (Ảnh Hero WebP/AVIF & Preconnect)
            </h2>
            <span class="text-xs bg-amber-500/20 text-amber-300 border border-amber-500/40 px-3 py-1 rounded-full font-mono">
              Dung lượng ảnh giảm 96% (4.2MB ➔ 150KB)
            </span>
          </div>
          <p class="text-xs text-slate-300 leading-relaxed">
            Áp dụng module <code class="text-amber-300">&lt;NuxtImg&gt;</code> tự động convert WebP/AVIF, gắn <code class="text-amber-300">fetchpriority="high"</code> và Preconnect tới CDN. Tuy nhiên TTFB vẫn chậm do dùng Monolith API.
          </p>
        </div>

        <!-- Phase 3 Banner -->
        <div
          v-if="activePhase === 3"
          class="bg-gradient-to-r from-emerald-950 via-slate-900 to-teal-950 border border-emerald-500/50 rounded-3xl p-6 shadow-2xl space-y-4"
        >
          <div class="flex items-center justify-between flex-wrap gap-3">
            <h2 class="font-bold text-lg text-emerald-400 flex items-center gap-2">
              ⚡ Phase 3: Tối Ưu TTFB & Streaming SSR (&lt; 100ms Response)
            </h2>
            <span class="text-xs bg-emerald-500/30 text-emerald-300 border border-emerald-500/50 px-3.5 py-1 rounded-full font-mono font-bold animate-pulse">
              🚀 TTFB Giảm ~95% (&lt; 100ms)
            </span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-3 text-xs">
            <div class="bg-white/5 p-3.5 rounded-2xl border border-white/10 space-y-1">
              <div class="text-slate-400">1. Fast Core API (~50ms)</div>
              <div class="font-bold text-emerald-300">SSR HTML Shell &lt; 100ms</div>
              <div class="text-[11px] text-slate-400">Nuxt Server chỉ chờ Core Info rồi lập tức trả HTML cho Browser.</div>
            </div>

            <div class="bg-white/5 p-3.5 rounded-2xl border border-white/10 space-y-1">
              <div class="text-slate-400">2. Inventory Status (~200ms)</div>
              <div class="font-bold text-emerald-300">Client Deferred Fetch</div>
              <div class="text-[11px] text-slate-400">Browser tự gửi request <code class="text-emerald-300">/api/inventory/1</code> độc lập.</div>
            </div>

            <div class="bg-white/5 p-3.5 rounded-2xl border border-white/10 space-y-1">
              <div class="text-slate-400">3. Slow Reviews (~1.5s)</div>
              <div class="font-bold text-emerald-300">Client Deferred Fetch</div>
              <div class="text-[11px] text-slate-400">Browser tự gửi request <code class="text-emerald-300">/api/reviews/1</code> hiển thị trong Network Tab.</div>
            </div>
          </div>
        </div>

        <!-- Phase 4 Banner: INP & Main Thread Optimization -->
        <div
          v-if="activePhase === 4"
          class="bg-gradient-to-r from-indigo-950 via-slate-900 to-purple-950 border border-indigo-500/50 rounded-3xl p-6 shadow-2xl space-y-4"
        >
          <div class="flex items-center justify-between flex-wrap gap-3">
            <h2 class="font-bold text-lg text-indigo-300 flex items-center gap-2">
              🧠 Phase 4: Tối Ưu INP & Giải Phóng Main Thread (scheduler.yield() & Debounce)
            </h2>
            <span class="text-xs bg-indigo-500/30 text-indigo-200 border border-indigo-500/50 px-3.5 py-1 rounded-full font-mono font-bold animate-pulse">
              🎯 INP Target &lt; 50ms (Smooth 60 FPS)
            </span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-3 text-xs">
            <div class="bg-white/5 p-3.5 rounded-2xl border border-white/10 space-y-1">
              <div class="text-slate-400">1. Debounce Input Handling</div>
              <div class="font-bold text-indigo-300">Trì hoãn 300ms</div>
              <div class="text-[11px] text-slate-400">Tránh kích hoạt vòng lặp tính toán nặng liên tục ở mỗi phím gõ.</div>
            </div>

            <div class="bg-white/5 p-3.5 rounded-2xl border border-white/10 space-y-1">
              <div class="text-slate-400">2. Long Task Chunking</div>
              <div class="font-bold text-indigo-300">Chia nhỏ 10.000 items/chunk</div>
              <div class="text-[11px] text-slate-400">Chia 100.000 items thành 10 khối nhỏ để nhường quyền điều khiển.</div>
            </div>

            <div class="bg-white/5 p-3.5 rounded-2xl border border-white/10 space-y-1">
              <div class="text-slate-400">3. Main Thread Yielding</div>
              <div class="font-bold text-indigo-300">scheduler.yield() API</div>
              <div class="text-[11px] text-slate-400">Nhường Main Thread cho Browser render frame tiếp theo, giữ giao diện mượt 60fps.</div>
            </div>
          </div>
        </div>

        <!-- Phase 5 Banner: CLS & Hydration Zeroed -->
        <div
          v-if="activePhase === 5"
          class="bg-gradient-to-r from-teal-950 via-slate-900 to-emerald-950 border border-emerald-500/60 rounded-3xl p-6 shadow-2xl space-y-4"
        >
          <div class="flex items-center justify-between flex-wrap gap-3">
            <h2 class="font-bold text-lg text-emerald-300 flex items-center gap-2">
              🎯 Phase 5: Triệt Hạ CLS (CLS = 0.00) & Hydration Overhead (0KB Client JS Nuxt Islands)
            </h2>
            <span class="text-xs bg-emerald-500/30 text-emerald-200 border border-emerald-500/50 px-3.5 py-1 rounded-full font-mono font-bold animate-pulse">
              🎯 CLS = 0.00 | 0KB Client JS
            </span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-3 text-xs">
            <div class="bg-white/5 p-3.5 rounded-2xl border border-white/10 space-y-1">
              <div class="text-slate-400">1. Vue Skeleton Aspect-Ratio</div>
              <div class="font-bold text-emerald-300">CLS = 0.00 Tuyệt Đối</div>
              <div class="text-[11px] text-slate-400">Bảo lưu không gian cố định min-h-[540px], trang không bị đẩy giật khi stream reviews.</div>
            </div>

            <div class="bg-white/5 p-3.5 rounded-2xl border border-white/10 space-y-1">
              <div class="text-slate-400">2. Nuxt Island / Server Component</div>
              <div class="font-bold text-emerald-300">0KB Client JS Payload</div>
              <div class="text-[11px] text-slate-400">&lt;ProductSpecs.server.vue&gt; chuyển giao HTML thuần, 0 cost hydration trên Client.</div>
            </div>

            <div class="bg-white/5 p-3.5 rounded-2xl border border-white/10 space-y-1">
              <div class="text-slate-400">3. Font Loading Strategy</div>
              <div class="font-bold text-emerald-300">font-display: swap</div>
              <div class="text-[11px] text-slate-400">Hiển thị chữ ngay với fallback font, triệt tiêu hoàn toàn FOUT/FOIT.</div>
            </div>
          </div>
        </div>

        <!-- PRODUCT SECTION GRID -->
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-10 items-start">
          
          <!-- LEFT COLUMN: Product Image Gallery -->
          <div class="lg:col-span-7 space-y-4">
            <!-- Phase 0: Raw Unoptimized Image Showcase -->
            <div v-if="activePhase === 0 && monolithData" class="space-y-4">
              <div class="bg-slate-900 rounded-3xl overflow-hidden border border-rose-500/40 shadow-2xl relative">
                <img
                  :src="monolithData.core.heroImageUrl"
                  alt="Product Hero Image"
                  class="w-full object-cover h-[500px]"
                />
                <div class="absolute top-4 left-4 bg-rose-600/90 text-white text-xs px-3.5 py-1.5 rounded-full backdrop-blur-md font-bold shadow">
                  🔴 Unoptimized Raw 4K Image (~4.2MB)
                </div>
              </div>

              <!-- Thumbnails Phase 0 -->
              <div class="grid grid-cols-3 gap-3">
                <div
                  v-for="(img, idx) in monolithData.core.galleryImages"
                  :key="idx"
                  class="bg-slate-900 rounded-2xl overflow-hidden border border-slate-800 aspect-[4/3]"
                >
                  <img :src="img" class="w-full h-full object-cover" />
                </div>
              </div>
            </div>

            <!-- Phase 2 / 3 / 4 / 5: Optimized Nuxt Image Hero -->
            <ProductHeroOptimized
              v-else
              :hero-image-url="currentCoreData?.heroImageUrl || ''"
              :gallery-images="currentCoreData?.galleryImages || []"
            />
          </div>

          <!-- RIGHT COLUMN: Product Info & Actions -->
          <div v-if="currentCoreData" class="lg:col-span-5 space-y-6 bg-slate-900/60 p-6 sm:p-8 rounded-3xl border border-slate-800 backdrop-blur-xl shadow-2xl">
            <!-- Brand & Title -->
            <div class="space-y-2">
              <div class="flex items-center justify-between">
                <span class="text-xs font-bold uppercase tracking-widest text-emerald-400 bg-emerald-500/10 px-3 py-1 rounded-full border border-emerald-500/30">
                  {{ currentCoreData.brand }}
                </span>
                <span class="text-xs text-slate-400 font-mono">SKU: PRO-SOUNDX-01</span>
              </div>

              <h1 class="text-3xl sm:text-4xl font-extrabold tracking-tight text-white heading-font">
                {{ currentCoreData.title }}
              </h1>
            </div>

            <!-- Price Card -->
            <div class="bg-gradient-to-br from-slate-900 to-slate-950 p-5 rounded-2xl border border-slate-800 flex items-center justify-between">
              <div>
                <div class="text-xs text-slate-400">Giá bán ưu đãi</div>
                <div class="flex items-baseline gap-3 mt-1">
                  <span class="text-4xl font-black text-white">${{ currentCoreData.price }}</span>
                  <span class="text-base text-slate-500 line-through">${{ currentCoreData.originalPrice }}</span>
                </div>
              </div>
              <span class="bg-gradient-to-r from-emerald-500 to-teal-500 text-black font-black text-xs px-3.5 py-2 rounded-xl shadow-lg shadow-emerald-500/20">
                TIẾT KIỆM 25%
              </span>
            </div>

            <!-- Inventory Section Component -->
            <!-- Monolith Inventory (Phase 0 / Phase 2) -->
            <div v-if="activePhase < 3 && monolithData">
              <div class="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 space-y-2">
                <div class="flex items-center justify-between text-sm font-semibold">
                  <span class="text-slate-300">Tình trạng kho hàng:</span>
                  <span class="text-emerald-400 flex items-center gap-1.5">
                    <span class="w-2 h-2 rounded-full bg-emerald-400"></span> Còn {{ monolithData.inventory.stockQuantity }} sản phẩm
                  </span>
                </div>
                <p class="text-xs text-slate-400">{{ monolithData.inventory.estimatedDelivery }}</p>
                <p class="text-xs text-slate-500">Địa điểm: {{ monolithData.inventory.warehouseLocation }}</p>
              </div>
            </div>

            <!-- Streaming Inventory Component (Phase 3, 4, 5) -->
            <StreamingInventorySection v-else />

            <!-- Specs & Description: Phase 5 uses Nuxt Island / Server Component (0KB JS) -->
            <ProductSpecs
              v-if="activePhase === 5"
              :description="currentCoreData.description"
              :specs="currentCoreData.specs"
            />

            <!-- Specs & Description: Phase 0 - 4 fallback -->
            <template v-else>
              <!-- Description -->
              <div class="text-sm text-slate-300 leading-relaxed pt-2">
                {{ currentCoreData.description }}
              </div>

              <!-- Specifications List -->
              <div class="border-t border-slate-800 pt-5 space-y-3">
                <h3 class="font-bold text-sm text-white flex items-center gap-2">
                  <span>⚙️ Thông Số Kỹ Thuật Nổi Bật</span>
                </h3>
                <ul class="text-xs text-slate-300 space-y-2 font-sans">
                  <li v-for="(spec, i) in currentCoreData.specs" :key="i" class="flex items-start gap-2">
                    <span class="text-emerald-400 mt-0.5">✓</span>
                    <span>{{ spec }}</span>
                  </li>
                </ul>
              </div>
            </template>

            <!-- INP Real-Time Shipping Calculator Widget -->
            <!-- Unoptimized INP Shipping Widget (Phase 0, 2, 3) -->
            <div v-if="activePhase < 4" class="border-t border-slate-800 pt-5 space-y-3">
              <div class="flex items-center justify-between">
                <h3 class="font-bold text-sm text-white">Tính Phí Vận Chuyển Real-Time</h3>
                <span class="text-[11px] bg-rose-500/20 text-rose-300 border border-rose-500/30 px-2.5 py-0.5 rounded-full font-mono">
                  🔴 INP Anti-Pattern (Long Task)
                </span>
              </div>

              <div class="flex gap-2">
                <input
                  v-model="postalCode"
                  @input="calculateShippingCostSynchronously"
                  type="text"
                  placeholder="Nhập mã bưu chính (vd: 700000)..."
                  class="flex-1 px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
                />
                <button
                  @click="calculateShippingCostSynchronously"
                  class="bg-indigo-600 hover:bg-indigo-500 text-white px-5 py-2.5 rounded-xl text-xs font-bold transition-all shadow-lg shadow-indigo-600/20"
                >
                  Tính phí
                </button>
              </div>

              <div
                v-if="computedFee !== null"
                class="p-3.5 bg-slate-950 border border-slate-800 rounded-xl text-xs flex justify-between items-center"
              >
                <span class="text-slate-400">Phí vận chuyển ước tính:</span>
                <span class="text-emerald-400 font-bold font-mono">
                  ${{ computedFee }} <span class="text-[10px] text-slate-500">({{ lastCalcDuration.toFixed(1) }}ms execution)</span>
                </span>
              </div>
            </div>

            <!-- Phase 4 & 5 INP Optimized Shipping Component -->
            <ShippingCalcOptimized v-else />

          </div>
        </div>

        <!-- REVIEWS SECTION -->
        <!-- Monolithic Reviews (Phase 0 / Phase 2) -->
        <div v-if="activePhase < 3 && monolithData" class="mt-16 border-t border-slate-800 pt-10 space-y-6">
          <div class="flex items-center justify-between">
            <h2 class="text-2xl font-bold text-white heading-font">
              💬 Đánh Giá Khách Hàng (Monolithic Payload)
            </h2>
            <span class="text-xs bg-rose-500/20 text-rose-300 border border-rose-500/40 px-3 py-1 rounded-full font-medium">
              🔴 Security Anti-Pattern (Unsanitized v-html)
            </span>
          </div>

          <div class="space-y-4">
            <div
              v-for="rev in monolithData.reviews"
              :key="rev.id"
              class="bg-slate-900 p-6 rounded-2xl border border-slate-800 space-y-3"
            >
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <img :src="rev.avatarUrl" class="w-10 h-10 rounded-full border border-slate-700 object-cover" />
                  <div>
                    <h4 class="font-bold text-sm text-white">{{ rev.author }}</h4>
                    <p class="text-xs text-slate-500">{{ rev.date }}</p>
                  </div>
                </div>
                <div class="flex text-amber-400 text-sm">
                  <span v-for="s in rev.rating" :key="s">★</span>
                </div>
              </div>

              <div
                class="text-sm text-slate-300 leading-relaxed bg-slate-950 p-4 rounded-xl border border-slate-800"
                v-html="rev.commentHtml"
              ></div>
            </div>
          </div>
        </div>

        <!-- Phase 5 Fixed Aspect-Ratio Skeleton Reviews Component -->
        <ReviewsSkeletonFixed v-else-if="activePhase === 5" />

        <!-- Phase 3 / 4 Streaming Reviews Component -->
        <StreamingReviewsSection v-else />

      </div>
    </main>
  </div>
</template>

<script setup>
const config = useRuntimeConfig();
const route = useRoute();

// Active Phase State (0: Baseline Monolith, 2: LCP Optimized, 3: TTFB Streaming, 4: INP Yielding, 5: CLS=0 & 0KB JS)
const activePhase = ref(
  route.query.phase !== undefined ? Number(route.query.phase) : 5
);

const switchPhase = (phase) => {
  activePhase.value = phase;
  navigateTo({
    path: route.path,
    query: { phase }
  });
};

// Base URL detection helper (Server vs Client inside Docker)
const ssrApiBase = config.apiInternal || config.public.apiBase;

// 1. Monolithic Fetch (ONLY called when activePhase is 0 or 2): ~1.75s blocking delay
const { data: monolithData, error: monolithError } = activePhase.value < 3
  ? await useFetch(`${ssrApiBase}/api/products/1/monolith`, { server: true })
  : { data: ref(null), error: ref(null) };

// 2. Fast Core Fetch (ONLY called when activePhase is 3 or 4): ~50ms latency -> TTFB < 100ms
const { data: coreData, error: coreError } = activePhase.value >= 3
  ? await useFetch(`${ssrApiBase}/api/products/1/core`, { server: true })
  : { data: ref(null), error: ref(null) };

// Current Core Product Data computed helper
const currentCoreData = computed(() => {
  if (activePhase.value >= 3) {
    return coreData.value;
  }
  return monolithData.value?.core;
});

// Unoptimized INP Anti-Pattern State
const postalCode = ref("");
const computedFee = ref(null);
const lastCalcDuration = ref(0);

const calculateShippingCostSynchronously = () => {
  const start = performance.now();

  let sum = 0;
  for (let i = 0; i < 100000; i++) {
    sum += Math.sqrt(i) * Math.sin(i) * Math.cos(i);
  }

  const duration = performance.now() - start;
  lastCalcDuration.value = duration;
  computedFee.value = ((sum % 15) + 5).toFixed(2);
};
</script>
