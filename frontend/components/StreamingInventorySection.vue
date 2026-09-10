<template>
  <div class="bg-slate-900/90 border border-emerald-500/30 text-white rounded-2xl p-5 shadow-xl relative overflow-hidden backdrop-blur-md">
    <!-- Stream Status Ribbon -->
    <div class="flex items-center justify-between border-b border-white/10 pb-3 mb-3">
      <div class="flex items-center gap-2">
        <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-ping"></span>
        <span class="text-xs font-bold uppercase tracking-wider text-emerald-400">Inventory Service</span>
      </div>
      <span class="text-[11px] font-mono bg-emerald-500/10 text-emerald-300 border border-emerald-500/30 px-2.5 py-0.5 rounded-full">
        ⚡ Deferred Fetch (~200ms)
      </span>
    </div>

    <!-- Loading Skeleton during Client Fetch (~200ms) -->
    <div v-if="pending" class="animate-pulse space-y-3 py-1">
      <div class="flex justify-between items-center">
        <div class="h-5 bg-slate-800 rounded w-1/3"></div>
        <div class="h-5 bg-emerald-900/50 rounded w-1/4"></div>
      </div>
      <div class="h-3.5 bg-slate-800/80 rounded w-3/4"></div>
      <div class="h-3 bg-slate-800/50 rounded w-1/2"></div>
      <div class="flex items-center gap-2 pt-1 text-xs text-emerald-400 font-mono">
        <span class="animate-spin">🌀</span> Fetching real-time stock data from Spring Boot...
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-xs text-rose-400 font-medium py-2 flex items-center gap-2">
      <span>⚠️</span> Không thể kết nối tới Inventory API (Spring Boot).
    </div>

    <!-- Loaded Inventory Content -->
    <div v-else-if="inventory" class="space-y-3">
      <div class="flex items-center justify-between">
        <span class="text-sm font-semibold text-slate-300">Tình trạng kho hàng:</span>
        <span class="bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 text-xs font-bold px-3 py-1 rounded-full flex items-center gap-1.5 shadow-sm">
          <span class="w-2 h-2 rounded-full bg-emerald-400"></span>
          Còn {{ inventory.stockQuantity }} sản phẩm
        </span>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs pt-1">
        <div class="bg-white/5 p-2.5 rounded-xl border border-white/5 flex items-center gap-2">
          <span class="text-base">🚀</span>
          <div>
            <div class="text-slate-400 text-[10px]">Dự kiến giao hàng</div>
            <div class="font-medium text-slate-200">{{ inventory.estimatedDelivery }}</div>
          </div>
        </div>

        <div class="bg-white/5 p-2.5 rounded-xl border border-white/5 flex items-center gap-2">
          <span class="text-base">📍</span>
          <div>
            <div class="text-slate-400 text-[10px]">Vị trí kho xuất hàng</div>
            <div class="font-medium text-slate-200">{{ inventory.warehouseLocation }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const config = useRuntimeConfig();

// Phase 3: Client-side deferred fetching ({ server: false }) so SSR is not blocked
const { data: inventory, pending, error } = await useFetch(
  `${config.public.apiBase}/api/inventory/1`,
  {
    server: false,
    lazy: true,
  }
);
</script>
