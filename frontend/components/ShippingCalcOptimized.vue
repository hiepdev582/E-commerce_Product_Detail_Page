<template>
  <div class="bg-slate-900 border border-slate-800 rounded-3xl p-6 space-y-6 shadow-2xl backdrop-blur-xl">
    <!-- Header with Strategy Selector -->
    <div class="flex items-center justify-between flex-wrap gap-3 border-b border-slate-800 pb-4">
      <div>
        <h3 class="font-bold text-base text-white flex items-center gap-2">
          🚚 Tính Phí Vận Chuyển Real-Time
          <span :class="['text-xs px-3 py-0.5 rounded-full font-mono font-bold border', useOptimization ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' : 'bg-rose-500/20 text-rose-300 border-rose-500/40']">
            {{ useOptimization ? '🟢 Phase 4: Main Thread Yielding' : '🔴 Baseline: Blocking Long Task' }}
          </span>
        </h3>
        <p class="text-xs text-slate-400 mt-1">
          So sánh việc chạy 100.000 phần tử trực tiếp trên Main Thread gây đơ UI với kỹ thuật <code class="text-emerald-300 bg-slate-950 px-1 rounded">scheduler.yield()</code> & Debounce.
        </p>
      </div>

      <!-- Mode Toggle Switch -->
      <div class="flex items-center bg-slate-950 p-1 rounded-2xl border border-slate-800 text-xs font-semibold">
        <button
          @click="useOptimization = false"
          :class="[
            'px-3 py-1.5 rounded-xl transition-all',
            !useOptimization ? 'bg-rose-600 text-white font-bold shadow' : 'text-slate-400 hover:text-white'
          ]"
        >
          🔴 Đồng Bộ (>200ms Long Task)
        </button>
        <button
          @click="useOptimization = true"
          :class="[
            'px-3 py-1.5 rounded-xl transition-all',
            useOptimization ? 'bg-emerald-500 text-black font-bold shadow' : 'text-slate-400 hover:text-white'
          ]"
        >
          ⚡ Chunking (INP &lt; 50ms)
        </button>
      </div>
    </div>

    <!-- Input Form & Real-time Trigger -->
    <div class="space-y-3">
      <label class="text-xs text-slate-400 font-medium">Nhập mã bưu chính hoặc quận/huyện để tính phí:</label>
      <div class="flex gap-3">
        <input
          v-model="postalCode"
          @input="onInputChanged"
          type="text"
          placeholder="Nhập mã bưu chính (vd: 700000)..."
          class="flex-1 px-4 py-3 bg-slate-950 border border-slate-800 rounded-2xl text-sm text-white placeholder-slate-500 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 font-mono"
        />
        <button
          @click="executeCalculation"
          :disabled="isCalculating"
          class="bg-emerald-500 hover:bg-emerald-400 disabled:opacity-50 text-black font-bold px-6 py-3 rounded-2xl text-xs transition-all shadow-lg shadow-emerald-500/20 flex items-center gap-2 whitespace-nowrap"
        >
          <span v-if="isCalculating" class="animate-spin">🌀</span>
          <span>{{ isCalculating ? 'Đang tính...' : 'Tính phí' }}</span>
        </button>
      </div>
    </div>

    <!-- Telemetry Dashboard HUD -->
    <div class="bg-slate-950 rounded-2xl p-4 border border-slate-800 space-y-3 text-xs">
      <div class="flex items-center justify-between text-slate-400 font-mono text-[11px] border-b border-slate-900 pb-2">
        <span>INP Telemetry Status</span>
        <span :class="['font-bold px-2 py-0.5 rounded', inpMetric < 50 ? 'text-emerald-400 bg-emerald-500/10' : 'text-rose-400 bg-rose-500/10']">
          INP: {{ inpMetric !== null ? `${inpMetric.toFixed(1)}ms` : 'Chờ tương tác...' }}
        </span>
      </div>

      <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <div class="bg-slate-900/60 p-3 rounded-xl border border-slate-800 space-y-1">
          <div class="text-slate-500 text-[10px]">Kỹ thuật xử lý</div>
          <div class="font-bold text-white">{{ useOptimization ? 'scheduler.yield()' : 'Sync Blocking Loop' }}</div>
        </div>

        <div class="bg-slate-900/60 p-3 rounded-xl border border-slate-800 space-y-1">
          <div class="text-slate-500 text-[10px]">Thời gian execution</div>
          <div :class="['font-bold font-mono', totalExecutionTime < 50 ? 'text-emerald-400' : 'text-amber-400']">
            {{ totalExecutionTime.toFixed(1) }}ms
          </div>
        </div>

        <div class="bg-slate-900/60 p-3 rounded-xl border border-slate-800 space-y-1">
          <div class="text-slate-500 text-[10px]">Main Thread Yields</div>
          <div class="font-bold text-emerald-400 font-mono">{{ yieldCount }} lần</div>
        </div>

        <div class="bg-slate-900/60 p-3 rounded-xl border border-slate-800 space-y-1">
          <div class="text-slate-500 text-[10px]">Trạng thái UI FPS</div>
          <div :class="['font-bold', isFrozen ? 'text-rose-400' : 'text-emerald-400']">
            {{ isFrozen ? '🔴 Frozen (>50ms)' : '🟢 Smooth (60 FPS)' }}
          </div>
        </div>
      </div>

      <!-- Chunk Progress Bar (Visible during optimized calculation) -->
      <div v-if="isCalculating" class="space-y-1.5 pt-1">
        <div class="flex justify-between text-[11px] text-slate-400 font-mono">
          <span>Tiến trình tính toán (100.000 items):</span>
          <span class="text-emerald-400 font-bold">{{ Math.round(chunkProgress) }}%</span>
        </div>
        <div class="w-full bg-slate-900 h-2 rounded-full overflow-hidden border border-slate-800">
          <div class="bg-gradient-to-r from-emerald-500 to-teal-400 h-full transition-all duration-75" :style="{ width: `${chunkProgress}%` }"></div>
        </div>
      </div>

      <!-- Result View -->
      <div v-if="computedFee !== null" class="pt-2 flex items-center justify-between font-semibold border-t border-slate-900">
        <span class="text-slate-300">Phí vận chuyển ước tính:</span>
        <span class="text-emerald-400 text-base font-black font-mono">${{ computedFee }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
const postalCode = ref('');
const useOptimization = ref(true);
const computedFee = ref(null);
const totalExecutionTime = ref(0);
const yieldCount = ref(0);
const chunkProgress = ref(0);
const isCalculating = ref(false);
const isFrozen = ref(false);
const inpMetric = ref(null);

let debounceTimer = null;

const yieldToMain = async () => {
  // Use native scheduler.yield() if available in modern browsers, otherwise fallback to setTimeout(..., 0)
  if ('scheduler' in window && 'yield' in window.scheduler) {
    await window.scheduler.yield();
  } else {
    await new Promise((resolve) => setTimeout(resolve, 0));
  }
};

const onInputChanged = () => {
  if (useOptimization.value) {
    // 1. Optimization: Debounce 300ms to prevent instant trigger on every keypress
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      executeCalculation();
    }, 300);
  } else {
    // Unoptimized: Fire synchronous blocking execution immediately on input
    executeCalculation();
  }
};

const executeCalculation = async () => {
  if (isCalculating.value) return;
  isCalculating.value = true;
  const startTime = performance.now();

  const TOTAL_ITEMS = 100000;
  let sum = 0;

  if (!useOptimization.value) {
    // ----------------------------------------------------
    // UNOPTIMIZED MODE: Synchronous Loop (Blocks Main Thread > 200ms)
    // ----------------------------------------------------
    isFrozen.value = true;
    yieldCount.value = 0;
    chunkProgress.value = 0;

    for (let i = 0; i < TOTAL_ITEMS; i++) {
      sum += Math.sqrt(i) * Math.sin(i) * Math.cos(i);
    }

    const endTime = performance.now();
    totalExecutionTime.value = endTime - startTime;
    inpMetric.value = totalExecutionTime.value; // High INP score
    computedFee.value = ((sum % 15) + 5).toFixed(2);
    isFrozen.value = false;
    isCalculating.value = false;
  } else {
    // ----------------------------------------------------
    // PHASE 4 OPTIMIZED MODE: Long Task Chunking with scheduler.yield()
    // ----------------------------------------------------
    isFrozen.value = false;
    yieldCount.value = 0;
    chunkProgress.value = 0;
    const CHUNK_SIZE = 10000; // Break 100,000 items into 10 chunks of 10,000

    let firstChunkTime = null;

    for (let i = 0; i < TOTAL_ITEMS; i += CHUNK_SIZE) {
      const chunkEnd = Math.min(i + CHUNK_SIZE, TOTAL_ITEMS);

      for (let j = i; j < chunkEnd; j++) {
        sum += Math.sqrt(j) * Math.sin(j) * Math.cos(j);
      }

      if (firstChunkTime === null) {
        firstChunkTime = performance.now() - startTime;
      }

      chunkProgress.value = (chunkEnd / TOTAL_ITEMS) * 100;

      // Yield control back to Main Thread so browser can render next frame & respond to input!
      if (chunkEnd < TOTAL_ITEMS) {
        yieldCount.value++;
        await yieldToMain();
      }
    }

    const endTime = performance.now();
    totalExecutionTime.value = endTime - startTime;
    // INP is measured by the latency of the first chunk before yielding to main thread!
    inpMetric.value = firstChunkTime || 12; // Fast INP < 50ms
    computedFee.value = ((sum % 15) + 5).toFixed(2);
    isCalculating.value = false;
  }
};
</script>
