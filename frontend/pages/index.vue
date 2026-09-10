<template>
  <div class="min-h-screen pb-16">
    <!-- Header Navbar -->
    <header class="border-b bg-white/90 backdrop-blur sticky top-0 z-50 shadow-sm">
      <div
        class="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between gap-4"
      >
        <div class="flex items-center gap-3">
          <span class="text-2xl font-extrabold text-indigo-600 heading-font"
            >AudioLuxe</span
          >
          <!-- Dynamic Phase Badge -->
          <span
            :class="[
              'text-xs px-2.5 py-1 rounded-full font-semibold transition-all flex items-center gap-1.5',
              activePhase === 0
                ? 'bg-red-100 text-red-700 border border-red-200'
                : 'bg-emerald-100 text-emerald-800 border border-emerald-300 font-bold shadow-sm'
            ]"
          >
            <span :class="['w-2 h-2 rounded-full', activePhase === 0 ? 'bg-red-500' : 'bg-emerald-500 animate-ping']"></span>
            {{ activePhase === 0 ? 'Phase 0: Baseline Anti-Patterns' : '⚡ Phase 2: LCP & Critical Path Optimized' }}
          </span>
        </div>

        <!-- Interactive Phase Switcher -->
        <div class="flex items-center bg-slate-100 p-1 rounded-xl border border-slate-200 text-xs font-semibold">
          <button
            @click="activePhase = 0"
            :class="[
              'px-3 py-1.5 rounded-lg transition-all',
              activePhase === 0
                ? 'bg-red-600 text-white shadow'
                : 'text-slate-600 hover:text-slate-900'
            ]"
          >
            🔴 Phase 0 (Baseline)
          </button>
          <button
            @click="activePhase = 2"
            :class="[
              'px-3 py-1.5 rounded-lg transition-all',
              activePhase === 2
                ? 'bg-emerald-600 text-white shadow font-bold'
                : 'text-slate-600 hover:text-slate-900'
            ]"
          >
            ⚡ Phase 2 (Optimized LCP)
          </button>
        </div>

        <div class="text-xs font-medium text-slate-500 hidden sm:block">
          Server Processing Time:
          <span class="text-amber-600 font-bold"
            >{{ data?.serverProcessingTimeMs || 0 }}ms</span
          >
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto px-4 pt-6">
      <!-- Error Alert -->
      <div v-if="error" class="p-4 bg-red-50 text-red-700 rounded-lg mb-6 border border-red-200">
        ⚠️ Không thể kết nối tới Backend Spring Boot API ({{
          config.public.apiBase
        }}). Đảm bảo Backend đang chạy!
      </div>

      <!-- Monolithic Product View -->
      <div v-else-if="data" class="space-y-6">
        <!-- Phase 2 Optimization Summary Banner (Visible when Phase 2 active) -->
        <div
          v-if="activePhase === 2"
          class="bg-gradient-to-r from-emerald-900 via-slate-900 to-indigo-950 text-white rounded-2xl p-5 border border-emerald-500/30 shadow-xl space-y-3"
        >
          <div class="flex items-center justify-between flex-wrap gap-2">
            <h2 class="font-bold text-lg text-emerald-400 flex items-center gap-2">
              ✨ Kỹ Thuật Tối Ưu Đã Thi Công Trong Phase 2:
            </h2>
            <span class="text-xs bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 px-3 py-1 rounded-full font-mono">
              Target LCP &lt; 1.2s
            </span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-4 gap-3 text-xs">
            <div class="bg-white/5 p-3 rounded-xl border border-white/10 space-y-1">
              <div class="text-slate-400">1. Tự Động Định Dạng</div>
              <div class="font-semibold text-emerald-300">&lt;NuxtImg&gt; WebP/AVIF</div>
              <div class="text-[11px] text-slate-400">Giảm ~96% dung lượng (4.2MB → 150KB)</div>
            </div>

            <div class="bg-white/5 p-3 rounded-xl border border-white/10 space-y-1">
              <div class="text-slate-400">2. Kết Nối Sớm Socket</div>
              <div class="font-semibold text-emerald-300">&lt;link rel="preconnect"&gt;</div>
              <div class="text-[11px] text-slate-400">Tiết kiệm 50-150ms DNS & TLS</div>
            </div>

            <div class="bg-white/5 p-3 rounded-xl border border-white/10 space-y-1">
              <div class="text-slate-400">3. Uu Tiên Tải Browser</div>
              <div class="font-semibold text-emerald-300">fetchpriority="high"</div>
              <div class="text-[11px] text-slate-400">Được Scanner quét và tải đầu tiên</div>
            </div>

            <div class="bg-white/5 p-3 rounded-xl border border-white/10 space-y-1">
              <div class="text-slate-400">4. Phù Hợp Màn Hình</div>
              <div class="font-semibold text-emerald-300">sizes & srcset Breakpoints</div>
              <div class="text-[11px] text-slate-400">Tải kích thước chính xác cho device</div>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
          <!-- SECTION 1: Product Gallery (LCP Section) -->
          <div class="lg:col-span-7 space-y-4">
            <!-- Phase 0: Baseline Unoptimized -->
            <div v-if="activePhase === 0" class="space-y-4">
              <div
                class="bg-slate-100 rounded-2xl overflow-hidden border border-slate-200 shadow-sm relative"
              >
                <!-- 
                  ANTI-PATTERN LCP:
                  - Dùng thẻ <img> nguyên bản với URL ảnh 4K không nén (3500px width, >4MB)
                  - Không dùng <NuxtImg> / WebP / AVIF
                  - Không set width/height attribute
                  - Không có fetchpriority="high" hoặc preload
                -->
                <img
                  :src="data.core.heroImageUrl"
                  alt="Product Hero Image"
                  class="w-full object-cover h-[480px]"
                />
                <div
                  class="absolute bottom-3 right-3 bg-red-600/90 text-white text-xs px-3 py-1.5 rounded-full backdrop-blur font-bold flex items-center gap-1.5"
                >
                  🔴 Unoptimized Raw 4K Image (~4.2MB)
                </div>
              </div>

              <!-- Thumbnail Gallery (Phase 0) -->
              <div class="grid grid-cols-3 gap-3">
                <div
                  v-for="(img, idx) in data.core.galleryImages"
                  :key="idx"
                  class="bg-slate-100 rounded-lg overflow-hidden border"
                >
                  <img :src="img" class="w-full h-24 object-cover" />
                </div>
              </div>
            </div>

            <!-- Phase 2: Optimized Nuxt Image Product Hero -->
            <ProductHeroOptimized
              v-else-if="activePhase === 2"
              :hero-image-url="data.core.heroImageUrl"
              :gallery-images="data.core.galleryImages"
            />
          </div>

          <!-- SECTION 2: Product Info & Inventory -->
          <div class="lg:col-span-5 space-y-6">
            <div>
              <span
                class="text-sm font-semibold text-indigo-600 tracking-wide uppercase"
                >{{ data.core.brand }}</span
              >
              <h1 class="text-3xl font-bold mt-1 text-slate-900 heading-font">
                {{ data.core.title }}
              </h1>
              <div class="flex items-center gap-3 mt-3">
                <span class="text-3xl font-black text-slate-900"
                  >${{ data.core.price }}</span
                >
                <span class="text-lg text-slate-400 line-through"
                  >${{ data.core.originalPrice }}</span
                >
                <span
                  class="bg-green-100 text-green-700 text-xs font-bold px-2.5 py-1 rounded"
                  >Giảm 25%</span
                >
              </div>
            </div>

            <!-- Inventory Card -->
            <div
              class="bg-indigo-50/60 border border-indigo-100 rounded-xl p-4 space-y-2"
            >
              <div
                class="flex items-center justify-between text-sm font-semibold"
              >
                <span class="text-indigo-900">Tình trạng kho hàng:</span>
                <span class="text-emerald-600 flex items-center gap-1">
                  <span>●</span> Còn {{ data.inventory.stockQuantity }} sản phẩm
                </span>
              </div>
              <p class="text-xs text-slate-600">
                {{ data.inventory.estimatedDelivery }}
              </p>
              <p class="text-xs text-slate-500">
                Địa điểm: {{ data.inventory.warehouseLocation }}
              </p>
            </div>

            <!-- Description -->
            <div class="prose prose-slate text-sm">
              <p>{{ data.core.description }}</p>
            </div>

            <!-- Specifications -->
            <div class="border-t pt-4 space-y-2">
              <h3 class="font-bold text-sm text-slate-900">Thông số kỹ thuật:</h3>
              <ul class="text-xs text-slate-600 space-y-1 list-disc pl-4">
                <li v-for="(spec, i) in data.core.specs" :key="i">{{ spec }}</li>
              </ul>
            </div>

            <!-- SECTION 3: Real-Time Shipping Calculator (INP Anti-Pattern) -->
            <div class="border-t pt-4 space-y-3">
              <h3
                class="font-bold text-sm text-slate-900 flex items-center justify-between"
              >
                <span>Tính phí vận chuyển Real-Time</span>
                <span class="text-xs text-red-600 font-normal"
                  >🔴 INP Anti-Pattern (Long Task)</span
                >
              </h3>

              <div class="flex gap-2">
                <!-- 
                  ANTI-PATTERN INP:
                  - Gõ chữ kích hoạt hàm calculateShippingCostSynchronously ngay lập tức
                  - Không dùng Debounce
                  - Chạy vòng lặp 100.000 phần tử đồng bộ trên Main Thread
                -->
                <input
                  v-model="postalCode"
                  @input="calculateShippingCostSynchronously"
                  type="text"
                  placeholder="Nhập mã bưu chính (vd: 700000)..."
                  class="flex-1 px-3 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
                <button
                  @click="calculateShippingCostSynchronously"
                  class="bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-indigo-700"
                >
                  Tính phí
                </button>
              </div>

              <div
                v-if="computedFee !== null"
                class="p-3 bg-slate-100 rounded-lg text-xs font-semibold flex justify-between"
              >
                <span>Phí vận chuyển ước tính:</span>
                <span class="text-indigo-600 font-bold"
                  >${{ computedFee }} (Đã tính qua
                  {{ lastCalcDuration.toFixed(1) }}ms blocking execution)</span
                >
              </div>
            </div>
          </div>
        </div>

        <!-- SECTION 4: Reviews Section (CLS & Security Anti-Pattern) -->
        <div class="mt-16 border-t pt-10 space-y-6">
          <div class="flex items-center justify-between">
            <h2 class="text-2xl font-bold text-slate-900 heading-font">
              Đánh giá từ khách hàng
            </h2>
            <span
              class="text-xs bg-amber-100 text-amber-800 px-3 py-1 rounded-full font-medium"
            >
              🔴 Security Anti-Pattern (Unsanitized v-html)
            </span>
          </div>

          <div class="space-y-4">
            <div
              v-for="rev in data.reviews"
              :key="rev.id"
              class="bg-white p-5 rounded-xl border border-slate-200 shadow-sm space-y-3"
            >
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <img
                    :src="rev.avatarUrl"
                    class="w-10 h-10 rounded-full border"
                  />
                  <div>
                    <h4 class="font-bold text-sm text-slate-900">
                      {{ rev.author }}
                    </h4>
                    <p class="text-xs text-slate-400">{{ rev.date }}</p>
                  </div>
                </div>
                <div class="flex text-amber-400 text-sm">
                  <span v-for="s in rev.rating" :key="s">★</span>
                </div>
              </div>

              <div
                class="text-sm text-slate-700 leading-relaxed bg-slate-50 p-3 rounded-lg border border-slate-100"
                v-html="rev.commentHtml"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
const config = useRuntimeConfig();

// Active Phase State (0: Baseline, 2: LCP Optimized)
const activePhase = ref(2);

// Monolithic Fetch (Phase 0/2 share same backend baseline until Phase 3)
const { data, error } = await useFetch(
  `${config.public.apiBase}/api/products/1/monolith`,
  {
    server: true,
  },
);

// INP Anti-Pattern State
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
