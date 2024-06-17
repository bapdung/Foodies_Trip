<script setup>
import { toRefs } from "vue";

const props = defineProps({
  plan: {
    type: Object,
    required: true,
  },
  isActive: {
    type: Boolean,
    default: false,
  },
});

const { plan, isActive } = toRefs(props);

console.log("prop", plan.value);
console.log("isActive", isActive.value);
</script>

<template>
  <div class="caro carousel-item" :class="{ active: isActive }">
    <h6 class="mt-2 mb-1">plan name</h6>
    <h3 class="mt-0">{{ plan.planTitle }}</h3>
    <template v-for="(detail, index) in plan.planDetailDto">
      <div class="card ms-6 me-5 m-3">
        <p class="card-title">{{ index + 1 }}. {{ detail.title }}</p>
        <p class="card-text">{{ detail.address }}</p>
      </div>
    </template>
  </div>
</template>

<style scoped>
.caro {
  text-align: center;
}

.card {
  width: auto;
  background-color: #fcf0f8;
  border-color: #fcf0f800;
  box-shadow: 10px;
}
</style>
