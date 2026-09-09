import { defineNuxtConfig } from "nuxt/config";

declare const process: {
  env: Record<string, string | undefined>;
};

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },

  // Anti-pattern: No Nuxt Image or Font optimization modules in Phase 0
  modules: [],

  css: ["~/assets/css/main.css"],

  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },

  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || "http://localhost:8080",
    },
  },

  app: {
    head: {
      title: "Phase 0 Baseline - E-Commerce PDP Lab",
      meta: [
        {
          name: "description",
          content: "Unoptimized Phase 0 E-commerce Product Detail Page",
        },
      ],
      // Anti-pattern: No <link rel="preconnect"> or <link rel="preload"> for Hero Image or Font
    },
  },
});
