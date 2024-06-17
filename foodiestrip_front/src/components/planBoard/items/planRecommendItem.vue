<script setup>
import { ref, watch, onMounted } from "vue";
import { getRandomPlan } from "@/api/plan";
import planCarouselItem from "@/components/planBoard/items/planCarouselItem.vue";
import { getSidoList, getGugunList } from "@/api/attraction";

const emit = defineEmits(["isModalVisible", "myPlan"]);

const items = ref([
  { id: 0, alt: "First image", content: "content1" },
  { id: 1, alt: "Second image", content: "content2" },
  { id: 2, alt: "Third image", content: "content3" },
]);

const sidoList = ref([]);
const gugunList = ref([]);

const selectedSidoCode = ref(0);
const selectedGugunCode = ref(0);

const area = ref({
  sido: "",
  gugun: "",
});

watch(selectedSidoCode, async (newVal, oldVal) => {
  sidoList.value.forEach((sido) => {
    if (sido.sidoCode == newVal) {
      area.value.sido = sido.sidoName;
    }
  });
  if (newVal !== null && newVal !== oldVal) {
    await getgugun(newVal);
  }
});

const getgugun = async (sidoCode) => {
  await getGugunList(
    sidoCode,
    (response) => {
      gugunList.value = response.data.gugunList;
    },
    (error) => {
      console.error(error);
    }
  );
};

onMounted(async () => {
  await getSidoList("", (response) => {
    sidoList.value = response.data.sidoList;
  });
});

const isModalVisible = () => {
  emit("isModalVisible");
};
const planIdx = ref(0);

const planList = ref();
const planListLen = ref();
const getRecommend = async () => {
  planIdx.value = 0;
  console.log("area", area.value);
  await getRandomPlan(area.value, (response) => {
    planList.value = response.data.planList;
    console.log("planList", planList.value);
    if (planList.value) {
      planListLen.value = planList.value.length;
    }
  });
};

const handlePlanIdx = (i) => {
  planIdx.value = (planIdx.value + i + planList.value.length) % planList.value.length;
  console.log(planIdx.value);
};

const goPlanWrite = async (i) => {
  emit("isModalVisible");
  emit("myPlan", planList.value[planIdx.value]);
};
</script>

<template>
  <div class="recommendPlan card">
    <button class="float-end exit-btn" @click="isModalVisible">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        fill="#FB3640"
        class="bi bi-x-square-fill float-end"
        viewBox="0 0 16 16"
      >
        <path
          d="M2 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2zm3.354 4.646L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 1 1 .708-.708"
        />
      </svg>
    </button>
    <br />
    <div id="recommend-plan-title" class="hclass recommend-plan-title">
      <h4>Foodies Recommended Plan</h4>
    </div>
    <div class="btnGroup d-flex search-bar justify-content-center">
      <div>
        <select
          id="search-sido"
          class="form-select me-2 rounded-5 select"
          v-model="selectedSidoCode"
        >
          <option value="0" selected>시군구</option>
          <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoCode">
            {{ sido.sidoName }}
          </option>
        </select>
      </div>
      <div>
        <select id="search-gugun" class="form-select me-2 rounded-5 select" v-model="area.gugun">
          <option value="0" selected>읍면동</option>
          <option v-for="gugun in gugunList" :key="gugun.gugunCode" :value="gugun.gugunName">
            {{ gugun.gugunName }}
          </option>
        </select>
      </div>
      <button class="recommend-btn mb-3" @click="getRecommend">추천</button>
    </div>
    <template v-if="planListLen >= 1" class="align-center"
      ><button @click="goPlanWrite">이 계획으로 일정짤래요</button></template
    >
    <div class="planBox mt-5">
      <div id="carouselExampleIndicators" class="carousel slide">
        <div class="carousel-indicators">
          <button
            type="button"
            v-for="(plan, index) in planList"
            :key="plan.planNo"
            data-bs-target="#carouselExampleIndicators"
            :data-bs-slide-to="index"
            :class="{ active: index === 0 }"
            aria-current="index === 0 ? 'true' : 'false'"
            :aria-label="'Slide ' + (index + 1)"
          ></button>
        </div>
        <div class="carousel-inner">
          <template v-for="(plan, index) in planList">
            <plan-carousel-item :plan="plan" :is-active="index === 0" />
          </template>
        </div>

        <button
          class="carousel-control-prev"
          type="button"
          data-bs-target="#carouselExampleIndicators"
          data-bs-slide="prev"
          @click="handlePlanIdx(-1)"
        >
          <span class="carousel-control-prev-icon" aria-hidden="true"></span>
          <span class="visually-hidden">Previous</span>
        </button>
        <button
          class="carousel-control-next"
          type="button"
          data-bs-target="#carouselExampleIndicators"
          data-bs-slide="next"
          @click="handlePlanIdx(1)"
        >
          <span class="carousel-control-next-icon" aria-hidden="true"></span>
          <span class="visually-hidden">Next</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Madimi+One&display=swap");
h4 {
  font-family: "Madimi One", sans-serif;
  font-weight: 400;
  font-style: normal;
  color: #fb3640;
  font-size: 30px;
}
#recommend-plan-title {
  font-family: "Madimi One", sans-serif;
  font-weight: 400;
  font-style: normal;
  color: #fb3640;
}

.recommendPlan {
  position: absolute;
  top: 200px;
  left: 25%;
  right: 33%;
  height: 800px;
  background-color: rgba(240, 248, 255, 0.844);
  z-index: 10000;
  border-color: rgba(240, 248, 255, 0.844);
  border-radius: 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}
.exit-btn {
  background-color: rgba(240, 248, 255, 0);
}
.exit-btn:hover {
  background-color: rgba(240, 248, 255, 0);
}
.hclass {
  text-align: center;
  padding-top: 20px;
  padding-bottom: 20px;
}

.recommend-btn {
  width: 55px;
  height: 35px;
  border-radius: 15px !important;
}

#search-sido,
#search-gugun {
  width: 160px;
}

#carousel-control-next-icon,
#carousel-control-prev-icon {
}
</style>
