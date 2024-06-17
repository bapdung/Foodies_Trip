<script setup>
import foodStoreDetailView from "@/views/foodBoard/foodStoreDetailView.vue";
import randomMenuView from "@/views/randomMenuView.vue";
import axios from "axios";
import { ref, watch, onMounted } from "vue";
import { randomStore } from "@/api/foodBoard";

const loadingImg = ref("");

const foodRecommend = () => {
  //추천버튼 눌렀을 때 비동기처리 로직 짜야함 화면 뜨기전에 로딩gif 보이게하자!
};

// 요청 인터셉터
axios.interceptors.request.use(
  function (config) {
    //서버 가기전 할일
    loadingImg.value = "load.gif";
    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
axios.interceptors.response.use(
  function (response) {
    loadingImg.value = "";
    return response;
  },
  function (error) {
    return Promise.reject(error);
  }
);

// axios
/*
여기부터 axios
*/
import { getSidoList, getGugunList } from "@/api/attraction";

const sidoList = ref([]);
const gugunList = ref([]);

const selectedSidoCode = ref(0);
const selectedGugunCode = ref(0);
const selectedfoodCode = ref(0);

watch(selectedSidoCode, async (newVal, oldVal) => {
  console.log(newVal);
  if (newVal !== null && newVal !== oldVal) {
    await getGugunList(newVal, (response) => {
      gugunList.value = response.data.gugunList;
    });
  }
});

onMounted(() => {
  getSidoList("", (response) => {
    sidoList.value = response.data.sidoList;
    gugunList.value = response.data.gugunList;
  });
});

const foodStore = ref();
const isShow = ref(false);
const getRecommend = async () => {
  isShow.value = false;
  const area = {
    sidoCode: selectedSidoCode.value,
    gugunCode: selectedSidoCode.value,
    contentTypeId: selectedfoodCode.value,
  };
  await randomStore(area, (response) => {
    foodStore.value = response.data.store;
    console.log(foodStore.value);
  });
  isShow.value = true;
};

watch(
  () => foodStore.value,
  (newValue, oldValue) => {
    console.log("foodStore 변경:", newValue);
  }
);
</script>

<template>
  <div class="container">
    <h2 class="text-center m-3"><b>오늘 어디서 뭐 먹지?</b></h2>
    <div style="height: 50px"></div>
    <fieldset>
      <div class="keward-result">
        <div class="btnGroup row d-flex search-bar align-items-center">
          <div class="col-lg-2 col-md-3 col-sm-4">
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
          <div class="col-lg-2 col-md-3 col-sm-4">
            <select
              id="search-gugun"
              class="form-select me-2 rounded-5 select"
              v-model="selectedGugunCode"
            >
              <option value="0" selected>읍면동</option>
              <option v-for="gugun in gugunList" :key="gugun.gugunCode" :value="gugun.gugunCode">
                {{ gugun.gugunName }}
              </option>
            </select>
          </div>
          <div class="col-lg-2 col-md-3 col-sm-4">
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
          <div class="col-lg-4 col-md-3 col-sm-12">
            <button id="recommend-button" class="float-end" @click="getRecommend">추천</button>
          </div>
        </div>
      </div>
    </fieldset>
    <hr />
    <h4 v-if="isShow">{{ foodStore.title }}어떠세요?</h4>
    <div class="row" v-if="loadingImg !== ''">
      <img src="@/assets/img/load.gif" alt="" />
    </div>

    <foodStoreDetailView v-if="isShow" :foodStore="foodStore" />
  </div>
</template>

<style scoped>
/* @import "@/assets/css/map.css"; */
* {
  text-align: center;
}

#recommend-button {
  padding: 0.8rem 1.5rem;
  border: none;
  font-weight: bold;
  border-radius: 5px;
  transition: 0.2s;
  border-radius: 30px;
  cursor: pointer;
}
</style>
