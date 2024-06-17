<script setup>
import planBoardItem from "@/components/planBoard/items/planBoardItem.vue";
import foodBoardReviewItem from "@/components/foodBoard/items/foodBoardReviewItem.vue";
import { ref, onMounted } from "vue";
import { useUserStore } from "@/stores/user";
import { getUserStoredPlan } from "@/api/plan";
import { storeToRefs } from "pinia";
const useStore = useUserStore();
const { userInfo } = storeToRefs(useStore);

import { getLikeList } from "@/api/storedStore";

const planList = ref();
const likeBoardList = ref();

onMounted(() => {
  getUserStoredPlan(userInfo.value.userId, (response) => {
    planList.value = response.data.planList;
  });

  getLikeList(userInfo.value.userId, (response) => {
    likeBoardList.value = response.data.myLikeList;
    console.log(likeBoardList.value);
  });
});
</script>

<template>
  <div>
    <!-- 저장된 맛집 -->
    <h2><strong>저장된 맛집</strong></h2>
    <hr />
    <div class="container">
      <div class="row">
        <template v-for="review in likeBoardList" :key="review">
          <foodBoardReviewItem :review="review" />
        </template>
      </div>
    </div>

    <!-- 저장된 여행 일정 -->
    <h2><strong>저장된 여행 일정</strong></h2>
    <!-- 여행게시판 생기면 그거로 수정해야됨 -->
    <hr />
    <div class="container">
      <div class="row">
        <template v-for="plan in planList" :key="plan">
          <planBoardItem :plan="plan" />
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
