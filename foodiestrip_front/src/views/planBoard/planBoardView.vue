<script setup>
import planBoardItem from "@/components/planBoard/items/planBoardItem.vue";
import { ref, onMounted, watch } from "vue";
import { selectAllPlanList, selectAllPlanListByOrder, searchByFiltering } from "@/api/plan";

const planList = ref([]);

onMounted(async () => {
  await selectAllPlanList(
    "",
    (response) => {
      planList.value = response.data.planList;
    },
    (error) => {
      console.error(error);
    }
  );
});

const startDate = ref("");
const endDate = ref("");

const orderList = async (rule) => {
  await selectAllPlanListByOrder(rule, (response) => {
    planList.value = response.data.planList;
    console.log("정렬완료", planList.value);
  });
};

/*
여기부터 sido gugun
*/
import { getSidoList, getGugunList } from "@/api/attraction";

//보낼 값
const filter = ref({
  planTitle: "",
  sido: "",
  gugun: 0,
});

const sidoList = ref([]);
const gugunList = ref([]);

const selectedSidoCode = ref(0);

watch(selectedSidoCode, async (newVal, oldVal) => {
  sidoList.value.forEach((sido) => {
    if (sido.sidoCode == newVal) {
      filter.value.sido = sido.sidoName;
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
    gugunList.value = response.data.gugunList;
  });
});

//검색
const getSearch = async () => {
  await searchByFiltering(filter.value, (response) => {
    planList.value = response.data.planList;
  });
};
</script>

<template>
  <div class="container mt-3">
    <div class="row d-flex" id="planBoardSearchBar">
      <div class="col-lg-2 col-md-3 col-sm-4">
        <select
          id="search-sido"
          class="form-select me-2 rounded-5 select"
          v-model="selectedSidoCode"
        >
          <option value="0" v-if="selectedSidoCode==0">시도</option>
          <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoCode">
            {{ sido.sidoName }}
          </option>
        </select>
      </div>
      <div class="col-lg-2 col-md-3 col-sm-4">
        <select id="search-gugun" class="form-select me-2 rounded-5 select" v-model="filter.gugun">
          <option value="0" selected disabled>읍면동</option>
          <option v-for="gugun in gugunList" :key="gugun.gugunCode" :value="gugun.gugunName">
            {{ gugun.gugunName }}
          </option>
        </select>
      </div>
      <!-- 검색 바 -->
      <div class="col container-fluid">
        <div class="d-flex float-end">
          <div class="d-flex justify-content-end">
            <input
              class="form-control search rounded-start-5"
              type="search"
              placeholder="일정이름으로 검색하기"
              aria-label="Search"
              v-model="filter.planTitle"
            />
            <button class="search-btn btn rounded-end-5" type="button" @click="getSearch">
              <i class="bi bi-search"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="dropdown col mt-2 mb-2">
        <a
          class="order-btn btn float-end dropdown-toggle rounded-5"
          href="#"
          role="button"
          data-bs-toggle="dropdown"
          aria-expanded="false"
        >
          정렬
        </a>

        <ul class="dropdown-menu">
          <li><a class="dropdown-item" @click="orderList('plan_board_jjim')">찜순</a></li>
          <li><a class="dropdown-item" @click="orderList('write_date')">최신순</a></li>
          <li><a class="dropdown-item" @click="orderList('plan_board_hit')">조회순</a></li>
        </ul>
      </div>
    </div>
  </div>
  <div class="row justify-content-center">
    <div class="col-lg-10">
      <div class="row row-cols-md-4 row-cols-sm-2 g-4">
        <div class="col" v-for="plan in planList" :key="plan.planNo">
          <planBoardItem :plan="plan" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "@/assets/css/main.css";

#planBoardSearchBar {
  display: flex;
  justify-content: space-between;
}

.search-btn {
  border-bottom-left-radius: 0 !important;
  border-bottom-right-radius: 15px !important;
  border-top-right-radius: 15px !important;
  border-top-left-radius: 0px !important;
}

.order-btn {
  background-color: #efebec;
  color: black;
}
</style>
