<script setup>
// import foodBoardReviewItem from "@/components/foodBoard/items/foodBoardReviewItem.vue";

import { foodBoardView, updateHit, deleteReview } from "@/api/foodBoard";
import { onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { postStoreinsert, postStoredelete, canStore } from "@/api/storedStore";

import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);

const route = useRoute();
const router = useRouter();
const review = ref({
  contentId: 0,
  foodBoardAddr: "",
  foodBoardHit: 0,
  foodBoardImage: "",
  foodBoardJjim: 0,
  foodBoardMyMoney: false,
  foodBoardNo: 0,
  foodBoardRank: 0,
  foodBoardReceiptImage: "",
  foodBoardReview: "",
  foodStoreTitle: "",
  userId: "",
});
const imageRef = ref("");
const storedColor = ref("gray"); // 초기값

const storeinfo = ref({
  userId: "",
  contentId: 0,
});

onMounted(async () => {
  const foodBoardNo = route.params.foodBoardNo;

  console.log("확인", foodBoardNo);

  await updateHit(foodBoardNo);

  await foodBoardView(foodBoardNo, (response) => {
    review.value = response.data.reviewDetail;
  });

  if (review.value.foodBoardImage == "") {
    imageRef.value = "/no-image.png";
  } else {
    imageRef.value = `data:image/jpeg;base64,${review.value.foodBoardImage}`;
  }
  if (userInfo.value) {
    storeinfo.value.userId = userInfo.value.userId;
    storeinfo.value.contentId = review.value.contentId;
    await canStore(storeinfo.value, (response) => {
      storedColor.value = response.data.color;
    });
  }
});

const imgSrc = ref("");
const updateSVG = (color) => {
  const svg = `<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='${color}' class='bi bi-star-fill' viewBox='0 0 16 16'><path d='M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z'/></svg>`;
  const encodedSVG = encodeURIComponent(svg);
  imgSrc.value = `data:image/svg+xml,${encodedSVG}`;
};

updateSVG(storedColor.value); // 초기값 설정

watch(storedColor, (newColor) => {
  updateSVG(newColor);
});

const storeStore = async () => {
  storeinfo.value.userId = userInfo.value.userId;
  storeinfo.value.contentId = review.value.contentId;

  if (storedColor.value === "gray") {
    await postStoreinsert(storeinfo.value, (response) => {
      storedColor.value = response.data.color;
    });
  } else {
    await postStoredelete(storeinfo.value, (response) => {
      storedColor.value = response.data.color;
    });
  }
};
</script>

<template>
  <div class="bufferBox"></div>
  <div class="container show-grid shadow-sm">
    <div class="infoBox row">
      <!-- 이미지를 담은 div -->
      <div
        class="col shadow-sm"
        style="text-align: center; display: flex; justify-content: center"
      >
        <img :src="imageRef" alt="" class="storeImg" />
      </div>
      <!-- 설명을 담은 div -->
      <div class="row">
        <div class="col d-flex justify-content-between mt-3">
          <div class="col-6 d-flex border-bottom border-2 ">
            <button class="mt-3 ms-4 me-4" style="width: fit-content; height: fit-content;">{{ review.contentTypeName }}</button>
            <h3 class="mt-3">{{ review.foodStoreTitle }}</h3>
          </div>
          <button class="jjim btn float-end" @click="storeStore" style="height: 50px; ">
              <img :src="imgSrc" alt="Star Icon"/>
              <br />
              저장
            </button>
        </div>
        <h6 class="addr ms-4 mt-2">{{ review.foodBoardAddr }}</h6>
        <div class="col d-flex ms-3">
            <template v-for="i in Math.floor(review.foodBoardRank)" :key="i">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="#FFBD39"
              class="bi bi-star-fill"
              viewBox="0 0 16 16"
            >
              <path
                d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"
              />
            </svg>
          </template>
          
      </div>
      <p class="ms-3">조회수: {{ review.foodBoardHit }}</p>
      <div class="reviewArea ms-3 col-7 "> <p class="mt-1">{{ review.foodBoardReview }}</p></div>
      </div>      

        <div class="all-btn">
          <template v-if="userInfo && userInfo.userId === review.userId">
            <RouterLink
              class="ms-2"
              :to="{ name: 'food-board-modify', params: { review: JSON.stringify(review) } }"
              ><button>수정</button></RouterLink
            >
            <RouterLink :to="{ name: 'user-post' }" class="ms-2"><button>목록</button></RouterLink>
          </template>
          <template v-else>
            <RouterLink :to="{ name: 'foodBoard' }" class="ms-2"><button>목록</button></RouterLink>
          </template>

          <button
            class="ms-3"
            v-if="userInfo && userInfo.userId === review.userId"
            @click="removeReview"
          >
            삭제
          </button>

          <RouterLink
            class="ms-2"
            :to="{ name: 'foodStore', params: { contentId: review.contentId } }"
            ><button>가게 정보 더보기</button></RouterLink
          >
        </div>
      </div>
    </div>
    <div class="bufferBox"></div>
</template>

<style scoped>
@import "@/assets/css/foodBoardReview.css";
.reviewArea{
  scroll-behavior: auto;
  height: 100px;
  border-radius: 15px;
  box-shadow: 1px 1px 1px 1px grey;
}
</style>
