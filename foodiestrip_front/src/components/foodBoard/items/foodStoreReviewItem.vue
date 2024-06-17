<script setup>
import { defineProps } from "vue";
const props = defineProps({
  review: Object,
});
// console.log('review',props.review)
</script>

<template>
  <div class="card container m-2 p-3">
    <div class="row">
      <div class="col">
        <p>
          <strong class="userId">{{ review.userId }}</strong>
          별점
          <img
            class="mb-1"
            src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='%23FFBD39' class='bi bi-star-fill' viewBox='0 0 16 16'%3E%3Cpath d='M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z'/%3E%3C/svg%3E"
            alt="Star Icon"
          />
          {{ review.foodBoardRank }}
        </p>
        <p>{{ review.foodBoardReview }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.userId {
  font-size: 20px;
}
</style>
