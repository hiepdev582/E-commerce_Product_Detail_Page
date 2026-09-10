<template>
  <div
    class="mt-16 border-t border-slate-800 pt-10 space-y-6 min-h-[540px] aspect-[16/9] sm:aspect-auto relative"
  >
    <!-- Header Title & Telemetry Badge -->
    <div class="flex items-center justify-between flex-wrap gap-3">
      <div>
        <h2
          class="text-2xl font-bold text-white heading-font flex items-center gap-3"
        >
          💬 Đánh Giá Khách Hàng
          <span
            class="text-xs bg-emerald-500/20 text-emerald-300 px-3 py-1 rounded-full font-sans font-bold border border-emerald-500/40 flex items-center gap-1.5"
          >
            <span class="w-2 h-2 rounded-full bg-emerald-400"></span>
            🎯 CLS = 0.00 (Fixed Reserved Space)
          </span>
        </h2>
        <p class="text-xs text-slate-400 mt-1">
          Khung chứa được thiết lập kích thước cố định sẵn (<code
            class="text-emerald-300 font-mono"
            >min-h-[540px]</code
          >). Khi dữ liệu stream về sau (~1.5s), trang không bị đẩy vị trí
          xuống.
        </p>
      </div>

      <span
        class="text-xs bg-slate-900 text-slate-400 border border-slate-800 px-3 py-1.5 rounded-full font-mono"
      >
        Zero Layout Shift (CLS: 0.00)
      </span>
    </div>

    <!-- Dynamic Stream Content Container with Fixed Aspect Ratio Reserve -->
    <div class="min-h-[440px] rounded-3xl overflow-hidden relative">
      <!-- Loading Skeleton Placeholder -->
      <div
        v-if="pending"
        class="bg-slate-900 border border-slate-800 rounded-3xl p-6 h-full flex flex-col justify-between space-y-4 shadow-2xl"
      >
        <div class="flex items-center gap-4 border-b border-slate-800 pb-4">
          <div
            class="w-12 h-12 rounded-2xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center text-xl font-bold border border-emerald-500/40 animate-pulse"
          >
            📡
          </div>
          <div class="space-y-1">
            <h4 class="font-bold text-white text-sm">
              Đang stream bình luận (Latency ~1.5s)...
            </h4>
            <p class="text-xs text-slate-400">
              Không gian bên dưới đã được bảo lưu sẵn. CLS = 0.00 hoàn hảo.
            </p>
          </div>
        </div>

        <div class="space-y-3 flex-1 pt-2">
          <div
            v-for="n in 3"
            :key="n"
            class="bg-slate-950 p-4 rounded-2xl border border-slate-800 space-y-3 animate-pulse"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full bg-slate-800"></div>
                <div class="space-y-1.5">
                  <div class="h-3 bg-slate-800 rounded w-28"></div>
                  <div class="h-2 bg-slate-800/60 rounded w-16"></div>
                </div>
              </div>
              <div class="h-3 bg-amber-500/20 rounded w-16"></div>
            </div>
            <div class="h-3 bg-slate-800/60 rounded w-full"></div>
          </div>
        </div>
      </div>

      <!-- Error State -->
      <div
        v-else-if="error"
        class="p-6 bg-rose-500/10 text-rose-300 rounded-3xl text-sm border border-rose-500/30"
      >
        ⚠️ Không thể kết nối tới Reviews API (Spring Boot).
      </div>

      <!-- Loaded Reviews Content (Fills reserved height smoothly with no layout jump) -->
      <div v-else-if="reviews" class="space-y-4">
        <div
          v-for="rev in reviews"
          :key="rev.id"
          class="bg-slate-900 p-6 rounded-3xl border border-slate-800 shadow-lg space-y-3 transition-all hover:border-slate-700"
        >
          <div class="flex items-center justify-between flex-wrap gap-2">
            <div class="flex items-center gap-3">
              <img
                :src="rev.avatarUrl"
                class="w-11 h-11 rounded-full border border-slate-700 object-cover shadow-sm"
              />
              <div>
                <div class="flex items-center gap-2">
                  <h4 class="font-bold text-sm text-white">{{ rev.author }}</h4>
                  <span
                    v-if="rev.verifiedPurchase"
                    class="text-[10px] bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 px-2 py-0.5 rounded-full font-bold"
                  >
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

          <div
            class="text-sm text-slate-300 leading-relaxed bg-slate-950 p-4 rounded-2xl border border-slate-800"
            v-html="rev.commentHtml"
          ></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const config = useRuntimeConfig();

// Phase 5 Deferred stream with reserved container height
const {
  data: reviews,
  pending,
  error,
} = await useFetch(`${config.public.apiBase}/api/reviews/1`, {
  server: false,
  lazy: true,
});
</script>
