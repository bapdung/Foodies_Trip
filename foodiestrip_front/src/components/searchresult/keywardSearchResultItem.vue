<script setup>
import { defineProps } from "vue";
import { RouterLink } from "vue-router";

const props = defineProps({
  result: Object,
});
</script>

<template>
  <div class="card" style="width: 30rem">
    <div class="card-body">
      <h4 class="card-title">{{ result.title }}</h4>
      <p class="card-text">{{ result.addr1 }}</p>
      <p class="card-text">{{ result.contentTypeName }}</p>

      <RouterLink
        v-if="result.contentTypeId >= 39"
        :to="{ name: 'foodStore', params: { contentId: result.contentId } }"
      >
        <button class="btn-detail">가게 상세정보보기</button>
      </RouterLink>

      <RouterLink v-else :to="{ name: 'attraction', params: { contentId: result.contentId } }">
        <button class="btn-detail">상세정보보기</button>
      </RouterLink>
    </div>
  </div>
</template>

<style scoped>
.btn-detail {
  border-radius: 8px;
  width: fit-content;
  height: auto;
}
.card:hover {
  background-color: rgb(255, 226, 226);
}
</style>
