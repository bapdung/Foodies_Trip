<script setup>
import foodBoardReviewItem from "@/components/foodBoard/items/foodBoardReviewItem.vue";
import { getmyfoodBoard } from "@/api/foodBoard";
import { useRouter } from "vue-router";
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
const useStore = useUserStore();
const { userInfo } = storeToRefs(useStore);
const router = useRouter();

const myBoardList = ref();

onMounted(() => {
  getmyfoodBoard(userInfo.value.userId, (response) => {
    myBoardList.value = response.data.myList;
    console.log(myBoardList);
  });
});

const goToPost = (contentId) => {
  router.push({ name: "food-store-review", params: { foodBoardNo: contentId } });
};
</script>

<template>
  <div>
    <h2><strong>내가 작성한 글</strong></h2>
    <hr />
    <div class="container">
      <div class="row">
        <template v-for="review in myBoardList" :key="review.contentId">
          <foodBoardReviewItem :review="review" @click="goToPost(review.foodBoardNo)" />
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
