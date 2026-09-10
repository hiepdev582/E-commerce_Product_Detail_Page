<template>
  <!-- 
    PHASE 5: NUXT ISLAND / SERVER COMPONENT (.server.vue)
    - Component này chỉ được render thành HTML tĩnh trên Nuxt SSR Server
    - Gửi 0KB JavaScript tới Client Browser (Zero Hydration Overhead)
    - Phù hợp hoàn hảo cho nội dung tĩnh như Mô tả & Thông số kỹ thuật sản phẩm
  -->
  <div class="space-y-5 bg-slate-900/80 p-6 rounded-3xl border border-emerald-500/30 backdrop-blur-xl shadow-xl">
    <!-- Server Component Badge Header -->
    <div class="flex items-center justify-between border-b border-slate-800 pb-3">
      <h3 class="font-bold text-sm text-white flex items-center gap-2">
        <span>⚙️ Thông Số Kỹ Thuật & Mô Tả</span>
      </h3>
      <span class="text-xs bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 px-3 py-1 rounded-full font-mono font-bold flex items-center gap-1.5">
        <span class="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
        ⚡ Nuxt Island (0KB Client JS)
      </span>
    </div>

    <!-- Description -->
    <div class="text-xs text-slate-300 leading-relaxed font-sans">
      <p>{{ description }}</p>
    </div>

    <!-- Specifications List -->
    <div class="space-y-2 pt-2">
      <div class="text-xs font-bold text-slate-400 uppercase tracking-wider">Chi tiết cấu hình:</div>
      <ul class="text-xs text-slate-300 space-y-2 font-sans">
        <li v-for="(spec, i) in specs" :key="i" class="flex items-start gap-2 bg-slate-950/60 p-2.5 rounded-xl border border-slate-800/80">
          <span class="text-emerald-400 font-bold">✓</span>
          <span>{{ spec }}</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
defineProps({
  description: {
    type: String,
    default: "",
  },
  specs: {
    type: Array,
    default: () => [],
  },
});
</script>
