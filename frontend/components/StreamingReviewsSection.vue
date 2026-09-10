<template>
  <div class="mt-16 border-t border-slate-200 pt-10 space-y-6">
    <!-- Header Title & Phase Badge -->
    <div class="flex items-center justify-between flex-wrap gap-3">
      <div>
        <h2 class="text-2xl font-bold text-slate-900 heading-font flex items-center gap-3">
          💬 Đánh Giá Khách Hàng
          <span class="text-xs bg-emerald-100 text-emerald-800 px-3 py-1 rounded-full font-sans font-bold border border-emerald-300">
            ⚡ Client Deferred Stream (~1.5s)
          </span>
        </h2>
        <p class="text-xs text-slate-500 mt-1">
          Dữ liệu bình luận được tải bất đồng bộ từ Client mà không làm chặn tiến trình vẽ trang ban đầu (TTFB &lt; 100ms).
        </p>
      </div>

      <span class="text-xs bg-amber-100 text-amber-800 border border-amber-300 px-3 py-1.5 rounded-full font-semibold">
        🔴 Security Vulnerability (v-html Unsanitized)
      </span>
    </div>

    <!-- Rating Summary Bar -->
    <div class="bg-slate-900 text-white rounded-2xl p-6 border border-slate-800 shadow-xl grid grid-cols-1 md:grid-cols-12 gap-6 items-center">
      <div class="md:col-span-4 text-center md:text-left border-b md:border-b-0 md:border-r border-slate-800 pb-4 md:pb-0 md:pr-6 space-y-1">
        <div class="text-4xl font-black text-amber-400">4.8 <span class="text-lg font-medium text-slate-400">/ 5</span></div>
        <div class="flex justify-center md:justify-start text-amber-400 text-base">★★★★★</div>
        <div class="text-xs text-slate-400">Dựa trên 128 đánh giá đã xác minh</div>
      </div>

      <div class="md:col-span-8 space-y-2 text-xs">
        <div class="flex items-center gap-3">
          <span class="w-12 text-slate-400">5 sao</span>
          <div class="flex-1 bg-slate-800 h-2 rounded-full overflow-hidden">
            <div class="bg-amber-400 h-full w-[85%]"></div>
          </div>
          <span class="w-8 text-right font-mono text-slate-300">85%</span>
        </div>
        <div class="flex items-center gap-3">
          <span class="w-12 text-slate-400">4 sao</span>
          <div class="flex-1 bg-slate-800 h-2 rounded-full overflow-hidden">
            <div class="bg-amber-400 h-full w-[10%]"></div>
          </div>
          <span class="w-8 text-right font-mono text-slate-300">10%</span>
        </div>
        <div class="flex items-center gap-3">
          <span class="w-12 text-slate-400">1 sao</span>
          <div class="flex-1 bg-slate-800 h-2 rounded-full overflow-hidden">
            <div class="bg-rose-500 h-full w-[5%]"></div>
          </div>
          <span class="w-8 text-right font-mono text-slate-300">5%</span>
        </div>
      </div>
    </div>

    <!-- Loading Skeleton Placeholder (~1.5s Backend Latency Simulation) -->
    <div v-if="pending" class="bg-slate-900/90 border border-emerald-500/30 rounded-2xl p-8 text-center space-y-4 shadow-2xl backdrop-blur-md">
      <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-emerald-500/20 text-emerald-400 text-2xl border border-emerald-500/40 animate-bounce">
        📡
      </div>
      
      <div class="space-y-1">
        <h3 class="font-bold text-white text-base">Đang Stream Dữ Liệu Đánh Giá Từ Backend...</h3>
        <p class="text-xs text-slate-400 max-w-lg mx-auto">
          Trang web và ảnh Hero đã hoàn tất tải trong <b class="text-emerald-400">&lt; 100ms</b>. 
          Lệnh <code class="bg-slate-800 px-1.5 py-0.5 rounded text-emerald-300 font-mono">/api/reviews/1</code> đang được bắn trực tiếp từ Client trong Tab Network!
        </p>
      </div>

      <!-- Animated Skeleton Stream Cards -->
      <div class="space-y-3 pt-2 text-left">
        <div v-for="n in 2" :key="n" class="bg-slate-800/60 p-5 rounded-xl border border-slate-700/50 space-y-3 animate-pulse">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-slate-700"></div>
              <div class="space-y-1.5">
                <div class="h-3.5 bg-slate-700 rounded w-32"></div>
                <div class="h-2.5 bg-slate-700/60 rounded w-20"></div>
              </div>
            </div>
            <div class="h-3 bg-amber-500/30 rounded w-16"></div>
          </div>
          <div class="h-3 bg-slate-700/40 rounded w-full"></div>
          <div class="h-3 bg-slate-700/30 rounded w-4/5"></div>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="p-5 bg-rose-50 text-rose-700 rounded-xl text-sm border border-rose-200">
      ⚠️ Không thể kết nối tới Reviews API (Spring Boot).
    </div>

    <!-- Loaded Reviews List -->
    <div v-else-if="reviews" class="space-y-4">
      <div 
        v-for="rev in reviews" 
        :key="rev.id" 
        class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-3 transition-all hover:shadow-md hover:border-indigo-200"
      >
        <div class="flex items-center justify-between flex-wrap gap-2">
          <div class="flex items-center gap-3">
            <img :src="rev.avatarUrl" class="w-11 h-11 rounded-full border border-slate-200 object-cover shadow-sm" />
            <div>
              <div class="flex items-center gap-2">
                <h4 class="font-bold text-sm text-slate-900">{{ rev.author }}</h4>
                <span v-if="rev.verifiedPurchase" class="text-[10px] bg-emerald-100 text-emerald-800 px-2 py-0.5 rounded font-bold">
                  ✓ Đã mua hàng
                </span>
              </div>
              <p class="text-xs text-slate-400">{{ rev.date }}</p>
            </div>
          </div>

          <div class="flex text-amber-400 text-base">
            <span v-for="s in rev.rating" :key="s">★</span>
          </div>
        </div>

        <!-- Rendered Comment HTML (Anti-pattern v-html) -->
        <div 
          class="text-sm text-slate-700 leading-relaxed bg-slate-50/80 p-4 rounded-xl border border-slate-100"
          v-html="rev.commentHtml"
        ></div>
      </div>
    </div>
  </div>
</template>

<script setup>
const config = useRuntimeConfig();

// Phase 3: Client-side deferred fetching ({ server: false }) so SSR is not blocked
const { data: reviews, pending, error } = await useFetch(
  `${config.public.apiBase}/api/reviews/1`,
  {
    server: false,
    lazy: true,
  }
);
</script>
