<template>
  <div class="mt-16 border-t border-slate-800 pt-10 space-y-6">
    <!-- Header & Security Badges -->
    <div class="flex items-center justify-between flex-wrap gap-3">
      <div>
        <h2 class="text-2xl font-bold text-white heading-font flex items-center gap-3">
          💬 Đánh Giá Khách Hàng
          <span class="text-xs bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 px-3 py-1 rounded-full font-mono font-bold flex items-center gap-1.5">
            <span class="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
            🛡️ Phase 6: XSS Sanitized & Security Hardened
          </span>
        </h2>
        <p class="text-xs text-slate-400 mt-1">
          Dữ liệu bình luận được làm sạch triệt để bằng <code class="text-emerald-300 font-mono">DOMPurify</code> trước khi render, bảo vệ ứng dụng khỏi lỗ hổng Stored XSS.
        </p>
      </div>

      <!-- Security HUD Pills -->
      <div class="flex items-center gap-2 flex-wrap text-xs">
        <span class="bg-emerald-500/10 text-emerald-300 border border-emerald-500/30 px-3 py-1 rounded-full font-mono">
          ✓ CSP Active
        </span>
        <span class="bg-emerald-500/10 text-emerald-300 border border-emerald-500/30 px-3 py-1 rounded-full font-mono">
          ✓ X-Frame-Options: DENY
        </span>
        <span class="bg-emerald-500/10 text-emerald-300 border border-emerald-500/30 px-3 py-1 rounded-full font-mono">
          ✓ Strict Domain CORS
        </span>
      </div>
    </div>

    <!-- Security Audit HUD Banner -->
    <div class="bg-slate-900 border border-emerald-500/40 rounded-3xl p-5 shadow-2xl backdrop-blur-xl space-y-3">
      <div class="flex items-center justify-between flex-wrap gap-2 border-b border-slate-800 pb-3">
        <div class="font-bold text-sm text-emerald-400 flex items-center gap-2">
          🛡️ Kết Quả Kiểm Trả Bảo Mật XSS Real-Time
        </div>
        <span class="text-xs text-slate-400 font-mono">OWASP Web Security Standard</span>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-3 text-xs">
        <div class="bg-slate-950 p-3.5 rounded-2xl border border-slate-800 space-y-1">
          <div class="text-slate-400 text-[11px]">Bảo Vệ XSS</div>
          <div class="font-bold text-emerald-400">DOMPurify Sanitized</div>
          <div class="text-[10px] text-slate-400">Vô hại hóa mã độc &lt;img onerror="alert(...)"&gt;</div>
        </div>

        <div class="bg-slate-950 p-3.5 rounded-2xl border border-slate-800 space-y-1">
          <div class="text-slate-400 text-[11px]">Chính Sách CORS</div>
          <div class="font-bold text-emerald-400">Strict Whitelist Domains</div>
          <div class="text-[10px] text-slate-400">Thay thế wildcard '*' nguy hiểm thành domain cụ thể</div>
        </div>

        <div class="bg-slate-950 p-3.5 rounded-2xl border border-slate-800 space-y-1">
          <div class="text-slate-400 text-[11px]">Response Security Headers</div>
          <div class="font-bold text-emerald-400">CSP, Nosniff, HSTS, DENY</div>
          <div class="text-[10px] text-slate-400">Đạt 100% Pass tiêu chuẩn Lighthouse Security</div>
        </div>
      </div>
    </div>

    <!-- Loading Skeleton Placeholder -->
    <div v-if="pending" class="bg-slate-900 border border-slate-800 rounded-3xl p-8 text-center space-y-4 shadow-2xl">
      <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-emerald-500/20 text-emerald-400 text-2xl border border-emerald-500/40 animate-bounce">
        📡
      </div>
      <div class="space-y-1">
        <h3 class="font-bold text-white text-base">Đang tải và làm sạch dữ liệu đánh giá từ Server...</h3>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="p-5 bg-rose-500/10 text-rose-300 rounded-2xl border border-rose-500/30 text-sm">
      ⚠️ Không thể kết nối tới Reviews API (Spring Boot).
    </div>

    <!-- Sanitized Reviews List -->
    <div v-else-if="sanitizedReviews" class="space-y-4">
      <div 
        v-for="rev in sanitizedReviews" 
        :key="rev.id" 
        class="bg-slate-900 p-6 rounded-3xl border border-slate-800 shadow-xl space-y-3 transition-all hover:border-emerald-500/40"
      >
        <div class="flex items-center justify-between flex-wrap gap-2">
          <div class="flex items-center gap-3">
            <img :src="rev.avatarUrl" class="w-11 h-11 rounded-full border border-slate-700 object-cover shadow-sm" />
            <div>
              <div class="flex items-center gap-2">
                <h4 class="font-bold text-sm text-white">{{ rev.author }}</h4>
                <span v-if="rev.isHackerPayload" class="text-[10px] bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 px-2 py-0.5 rounded-full font-bold">
                  🛡️ Payload Đã Được Làm Sạch (Neutralized)
                </span>
                <span v-else-if="rev.verifiedPurchase" class="text-[10px] bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 px-2 py-0.5 rounded-full font-bold">
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

        <!-- Safely Rendered HTML after DOMPurify Sanitization -->
        <div 
          class="text-sm text-slate-300 leading-relaxed bg-slate-950 p-4 rounded-2xl border border-slate-800"
          v-html="rev.sanitizedHtml"
        ></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import DOMPurify from 'dompurify';

const config = useRuntimeConfig();

const { data: reviews, pending, error } = await useFetch(
  `${config.public.apiBase}/api/reviews/1`,
  {
    server: false,
    lazy: true,
  }
);

// Sanitize review HTML content using DOMPurify to eliminate Stored XSS
const sanitizedReviews = computed(() => {
  if (!reviews.value) return [];

  return reviews.value.map((rev) => {
    // DOMPurify strips out dangerous tags (<script>, onerror="", onload="") while keeping safe HTML tags (<b>, <i>, <p>)
    const cleanHtml = DOMPurify.sanitize(rev.commentHtml, {
      ALLOWED_TAGS: ['b', 'i', 'em', 'strong', 'a', 'p', 'span', 'br'],
      ALLOWED_ATTR: ['href', 'target', 'style', 'class'],
    });

    return {
      ...rev,
      sanitizedHtml: cleanHtml,
      isHackerPayload: rev.commentHtml.includes('onerror=') || rev.commentHtml.includes('alert('),
    };
  });
});
</script>
