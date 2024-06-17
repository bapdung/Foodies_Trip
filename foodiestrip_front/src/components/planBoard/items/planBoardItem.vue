<script setup>
import { defineProps, ref } from "vue";

const props = defineProps({
  plan: {
    type: Object,
  },
});

//myPage 여부
const currentUrl = window.location.href;
const myPage = ref(currentUrl.includes("myPage/trip-plan"));
</script>

<template>
  <div class="card">
    <div class="card-body">
      <h5 class="card-title">{{ plan.planTitle }}</h5>
      <h6 v-if="!myPage" class="card-subtitle mb-3 text-body-secondary">
        {{ plan.userId }}님의 추천일정
      </h6>
      <p class="word card-text">
        <button class="me-1">{{ plan.sido }}</button>
        <button>{{ plan.gugun }}</button>
      </p>
      <p v-if="!myPage" class="card-text">
        찜수 : {{ plan.planBoardJjim }} 조회수 : {{ plan.planBoardHit }}
      </p>
      <RouterLink
        v-if="!myPage"
        :to="{ name: 'plan-board-view', params: { plan: JSON.stringify(plan) } }"
        >구경하러 가기</RouterLink
      >
      <RouterLink v-if="myPage" :to="{ name: 'plan-user-view', params: { plan: plan.planNo } }"
        >일정 보기</RouterLink
      >
    </div>
  </div>
</template>

<style scoped>
@import "@/assets/css/main.css";

.word {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  line-height: 1.2;
  height: 4.8em;
  text-align: left;
  word-wrap: break-word;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.card {
  box-shadow: 1px 1px 1px 1px grey;
  height: auto;
}
</style>
