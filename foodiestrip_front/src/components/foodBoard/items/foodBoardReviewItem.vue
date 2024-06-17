<script setup>
import { defineProps, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();

const props = defineProps({
  review: Object,
});

const imageRef = ref();
const rank = ref(0);

onMounted(() => {
  if (!props.review || !props.review.foodBoardImage || props.review.foodBoardImage === "") {
    imageRef.value = "/no-image.png";
  } else if (props.review) {
    imageRef.value = `data:image/jpeg;base64,${props.review.foodBoardImage}`;
  }
});

const goToStore = () => {
  router.push({ name: "foodStore", params: { contentId: props.review.contentId } });
};
</script>

<template>
  <div @click="goToStore" class="card m-3" style="width: 18rem">
    <img :src="imageRef" class="cardImg card-img-top" alt="..." />
    <div class="card-body">
      <h5 class="card-title">{{ review.foodStoreTitle ? review.foodStoreTitle : review.title }}</h5>
      <p class="card-text">{{ review.foodBoardAddr ? review.foodBoardAddr : review.addr1 }}</p>
      <button class="contentType" disabled>{{ review.contentTypeName }}</button>
      <br />
      <template v-for="i in Math.floor(review.foodBoardRank)">
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
      <p>조회수: {{ review.foodBoardHit ? review.foodBoardHit : 0 }}</p>
      <p class="contentBox card-text">{{ review.foodBoardReview }}</p>
    </div>
  </div>
</template>

<style scoped>
.contentBox {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 13rem;
}
.cardImg {
  margin: 10px;
  width: 15rem;
  height: auto;
}

.contentType:hover {
  background-color: #fb3640;
}
</style>
