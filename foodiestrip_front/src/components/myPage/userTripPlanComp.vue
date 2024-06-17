<script setup>
import planBoardItem from "@/components/planBoard/items/planBoardItem.vue";
import { ref, onMounted } from "vue";
import { RouterView } from "vue-router";
import { useUserStore } from "@/stores/user";
import { getUserPlan } from "@/api/plan";
import { storeToRefs } from "pinia";
const useStore = useUserStore();
const { userInfo } = storeToRefs(useStore);

const planList = ref();

onMounted(() => {
  getUserPlan(userInfo.value.userId, (response) => {
    planList.value = response.data.planList;
  });
});
</script>

<template>
  <div>
    <h2><strong>내 여행 일정</strong></h2>
    <hr />
    <div class="container">
      <div class="row">
        <template v-for="plan in planList">
          <planBoardItem :plan="plan" />
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
