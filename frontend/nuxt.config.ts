import { defineNuxtConfig } from "nuxt/config";

declare const process: {
  env: Record<string, string | undefined>;
};

declare module "@nuxt/schema" {
  interface NuxtConfig {
    image?: {
      domains?: string[];
      quality?: number;
      format?: string[];
      [key: string]: any;
    };
  }
}

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },

  modules: ["@nuxt/image", "@nuxtjs/tailwindcss"],

  image: {
    domains: ["images.unsplash.com", "i.pravatar.cc"],
    quality: 80,
    format: ["webp", "avif", "jpg"],
  },

  css: ["~/assets/css/main.css"],

  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },

  runtimeConfig: {
    // Internal API URL for Nuxt Server SSR calls inside Docker network
    apiInternal: process.env.NUXT_API_INTERNAL || "http://localhost:8080",

    // Public API URL for Client Browser calls outside Docker container
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || "http://localhost:8080",
    },
  },

  app: {
    head: {
      title: "E-Commerce Product Detail Page - Performance & Security Lab",
      meta: [
        {
          name: "description",
          content:
            "E-commerce Product Detail Page với tối ưu LCP & Critical Rendering Path",
        },
      ],
      link: [
        // Phase 2 Optimization: Preconnect & DNS-Prefetch cho CDN chứa ảnh
        { rel: "preconnect", href: "https://images.unsplash.com" },
        { rel: "dns-prefetch", href: "https://images.unsplash.com" },
        { rel: "preconnect", href: "https://fonts.googleapis.com" },
        {
          rel: "preconnect",
          href: "https://fonts.gstatic.com",
          crossorigin: "anonymous",
        },
      ],
    },
  },
});
