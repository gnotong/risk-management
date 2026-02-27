<template>
  <div class="min-h-screen bg-[#0f1115] text-gray-100 font-sans selection:bg-blue-500 selection:text-white">
    <nav class="sticky top-0 z-50 bg-[#161920]/80 backdrop-blur-md border-b border-gray-800 px-6 py-4 flex justify-between items-center">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center font-bold text-xl shadow-lg shadow-blue-500/20">
          GRC
        </div>
        <h1 class="text-2xl font-bold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-indigo-400">
          Risk Management
        </h1>
      </div>
      <div class="flex gap-6">
        <router-link to="/" class="text-gray-400 hover:text-white transition-colors uppercase text-sm tracking-wider font-semibold">Risques</router-link>
        <router-link to="/action-plans" class="text-gray-400 hover:text-white transition-colors uppercase text-sm tracking-wider font-semibold">Plans d'Action</router-link>
      </div>
    </nav>
    <main class="max-w-7xl mx-auto p-6">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
