<script setup>
import { onMounted } from "vue";
import { RouterLink } from "vue-router";
const props = defineProps({
  item: Object,
});

onMounted(() => {
  // console.log(props.item)
});

</script>

<template>
  <div class="card" style="width: 18rem">
    <div class="imageincard">
      <img :src="props.item.img" class="card-img-top" alt="..." />
    </div>

    <div class="card-body">
      <h3 class="card-title">{{ props.item.city }}</h3>
      <p class="card-text">{{ props.item.content }}</p>
      <RouterLink :to="{ name: 'test', params: { regionCode: item.regionCode } }"
        >여행상품 보러가기</RouterLink
      >
    </div>
  </div>
</template>

<style scoped>
@import "@/assets/css/themeItem.css";
</style>
