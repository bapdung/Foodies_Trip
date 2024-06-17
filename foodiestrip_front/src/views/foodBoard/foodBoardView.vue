<script setup>
import { useRouter } from "vue-router";

import { ref, onMounted, watch } from "vue";
import foodBoardReviewItem from "@/components/foodBoard/items/foodBoardReviewItem.vue";

const router = useRouter();

const changeRouterView = () => {
  console.log("글쓰기 클릭");
  router.push({ name: "food-board-write" });
};

const replace = (foodBoardNo) => {
  console.log("replace", foodBoardNo);
  router.push({ name: "food-store-review", params: { foodBoardNo: foodBoardNo } });
};

/*
여기부터 axios
*/
import { getSidoList, getGugunList } from "@/api/attraction";
import { selectFoodBoardList, searchByFiltering } from "@/api/foodBoard";

const sidoList = ref([]);
const gugunList = ref([]);

const selectedSidoCode = ref(0);
const selectedGugunCode = ref(0);
const selectedfoodCode = ref(0);
const selectedTitle = ref("");

const reviewList = ref([]);

watch(selectedSidoCode, async (newVal, oldVal) => {
  console.log(newVal)
  if (newVal !== null && newVal !== oldVal) {
    await getGugunList(newVal, (response) => {
      gugunList.value = response.data.gugunList;
      selectedGugunCode.value = 1;
    });
  }
});

onMounted(async () => {
  await getSidoList("", (response) => {
    sidoList.value = response.data.sidoList;
    gugunList.value = response.data.gugunList;
  });
  await selectFoodBoardList("", (response) => {
    reviewList.value = response.data.allList;
    console.log(reviewList.value);
  });
});

const sortOrder = ref('write_Date desc')
const order = async (order) => {
  sortOrder.value = order;
  await selectFoodBoardList(sortOrder.value, (response) => {
    reviewList.value = response.data.allList;
    console.log(reviewList.value);
  });
};

const goSearch = async () => {
  const foodDto = {
    sidoCode: selectedSidoCode.value,
    gugunCode: selectedGugunCode.value,
    contentTypeId: selectedfoodCode.value,
    title: selectedTitle.value,
  };
  console.log(foodDto);
  await searchByFiltering(foodDto, (response) => {
    reviewList.value = response.data.reviewList;
  });
};
</script>

<template>
  <!-- 글쓰기 -->
  <div class="container mt-2">
    <div class="row">
      <div class="col">
        <button class="btn write-btn rounded-5 float-end" @click="changeRouterView">글쓰기</button>
      </div>
    </div>
    <div class="row d-flex search-bar">
      <div class="col-lg-6 col-md-12 col-sm-12">
        <div class="row">
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

          <select
            id="search-gugun"
            class="form-select me-2 rounded-5 select"
            v-model="selectedGugunCode"
          >
            <option value="0" v-if="selectedGugunCode==0">구군</option>
            <option v-for="gugun in gugunList" :key="gugun.gugunCode" :value="gugun.gugunCode">
              {{ gugun.gugunName }}
            </option>
          </select>

          <select
            id="search-food"
            class="form-select me-2 rounded-5 select"
            v-model="selectedfoodCode"
          >
            <option value="0" selected>음식종류</option>
            <option value="40">술집</option>
            <option value="51">카페</option>
            <option value="43">치킨</option>
            <option value="44">고기</option>
            <option value="45">경양식</option>
            <option value="46">패스트푸드</option>
            <option value="41">한식</option>
            <option value="47">일식</option>
            <option value="48">중국식</option>
            <option value="49">해외(동남아/유럽)</option>
            <option value="42">분식</option>
            <option value="50">뷔페</option>
          </select>
        </div>
      </div>

      <div class="col container-fluid">
        <form class="d-flex float-end" role="search">
          <input
            class="form-control search rounded-start-5"
            type="search"
            placeholder="상호명으로 검색하기"
            aria-label="Search"
            v-model="selectedTitle"
          />
          <button class="search-btn btn rounded-end-5" type="button" @click="goSearch">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="white"
              class="bi bi-search mt-2"
              viewBox="0 0 16 16"
            >
              <path
                d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"
              />
            </svg>
          </button>
        </form>
      </div>
    </div>
    <div class="row">
      <div class="dropdown col">
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
          <li><p class="dropdown-item" @click="order('write_Date desc')">최신순</p></li>
          <li><p class="dropdown-item" @click="order('food_board_hit desc')">조회순</p></li>
          <li><p class="dropdown-item" @click="order('food_board_rank desc')">별점 높은순</p></li>
          <li><p class="dropdown-item" @click="order('food_board_jjim desc')">찜 많은순</p></li>
        </ul>
      </div>
    </div>

    <!-- 글 부분 -->
    <div class="row mt-4">
      <foodBoardReviewItem
        v-for="review in reviewList"
        :key="review"
        @click="replace(review.foodBoardNo)"
        :review="review"
      ></foodBoardReviewItem>
    </div>
  </div>
</template>

<style scoped>
@import "@/assets/css/main.css";

.write-btn {
  width: 90px;
}

.search {
  width: 300px;
}

.form-select {
  width: 100px;
}

.search-bar {
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

.row {
  margin-bottom: 7px;
}
</style>
