<script setup>
import { onMounted } from "vue";
import { RouterView } from "vue-router";
import { useUserStore } from '@/stores/user';
import mainHeader from "@/components/layout/mainHeader.vue";
import mainFooter from "@/components/layout/mainFooter.vue";

const userStore = useUserStore();

onMounted(async () => {
  await userStore.initializeAuth();
});
</script>

<template>
  <mainHeader />
  <RouterView />
  <mainFooter />
</template>

<style>
@import "@/assets/css/main.css";
@import url("https://fonts.googleapis.com/css2?family=Madimi+One&display=swap");
</style>
