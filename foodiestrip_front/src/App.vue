<script setup>
import { RouterView, RouterLink } from "vue-router";
import mainHeader from "@/components/layout/mainHeader.vue";
import mainFooter from "@/components/layout/mainFooter.vue";
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
