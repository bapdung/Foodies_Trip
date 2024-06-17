<script setup>
import { RouterView, RouterLink, onBeforeRouteUpdate } from "vue-router";
import { ref, provide, computed, watch } from "vue";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
const userStore = useUserStore();
const { userInfo, userPreferenceInfo } = storeToRefs(userStore);
console.log(userInfo.value);
console.log(userPreferenceInfo.value);
const userInfoExists = ref(!!userInfo.value);

watch(userInfo, (newValue) => {
  userInfoExists.value = !userInfoExists.value;
});

import themeItem from "@/components/mainPage/items/themeItem.vue";
import AdsItem from "@/components/mainPage/items/adsItem.vue";

const domesticTrip = [
  {
    city: "서울",
    content: "현대와 전통이 공존하는 서울로 놀러가기",
    img: "/images/citys/seoul.jpg",
    regionCode:'1'
  },
  {
    city: "대전",
    content: "공원과 문화의 도시 대전으로 놀러가기",
    img: "/images/citys/daejeon.jpg",
    regionCode:'3'
  },
  {
    city: "제주",
    content: "바람과 바다, 돌이 아름다운 제주로 놀러가기",
    img: "/images/citys/jeju.jpg",
    regionCode:'39'
  },
  {
    city: "부산",
    content: "바다향기 물씬느껴지는 부산으로 놀러가기",
    img: "/images/citys/busan.jpg",
    regionCode:'6'
  },
];

const foreignTrip = [
  { city: "방콕", content: "설명", img: "/images/citys/bangkoc.jpg",regionCode:'6' },
  { city: "바르셀로나", content: "설명", img: "/images/citys/barcelona.jpg",regionCode:'6' },
  { city: "베를린", content: "설명", img: "/images/citys/brrlin.jpg",regionCode:'6' },
  { city: "방콕", content: "설명", img: "/images/citys/douku.jpg",regionCode:'6' },
];
</script>

<template>
  <div v-if="userInfoExists" id="recomandInfo">
    <h2>
      <template v-if="userPreferenceInfo.mountainPref === true"> "산" </template>
      <template v-if="userPreferenceInfo.seaPref === true"> "바다" </template>
      <template v-if="userPreferenceInfo.foodPref === true"> "음식" </template>
      <template v-if="userPreferenceInfo.festivalPref === true"> "축제" </template>
      <template v-if="userPreferenceInfo.shoppingPref === true"> "쇼핑" </template>
      <template v-if="userPreferenceInfo.tourPref === true"> "관광명소" </template>
      <template v-if="userPreferenceInfo.sportPref === true"> "스포츠" </template>
      (을)를 좋아하는
      <span :textContent="userInfo.userNickname"></span>님!<br />
      여행지로 "양양"은 어떠신가요?
    </h2>
  </div>

  <section class="cards">
    <div class="col-md-10 mt-3">
      <div class="detail_title">
        <h2>국내 여행지 추천</h2>
        <p>올해는 국내여행 어때요?</p>
      </div>
      <div class="row mt-4">
        <themeItem v-for="(item, index) in domesticTrip" :key="index" :item="item"></themeItem>
      </div>

      <div style="height: 50px"></div>
      <AdsItem />
      <div style="height: 80px"></div>

      <div class="detail_title">
        <h2>해외 여행지 추천</h2>
        <p>올해는 해외여행 어때요?</p>
      </div>
      <div class="row mt-4">
        <themeItem v-for="(item, index) in foreignTrip" :key="index" :item="item"></themeItem>
      </div>
    </div>
  </section>
  <div style="height: 80px"></div>

  <div class="container">
    <div class="row">
      <!-- 첫 번째 섹션 -->
      <div class="col-md-6">
        <h2 class="detail_title">오늘 어디서 뭐 먹지?</h2>
        <RouterLink :to="{ name: 'food-recommend' }">
          <img src="/images/mainViewImg/randomfoodImg.png" class="randomImg" />
        </RouterLink>
      </div>

      <!-- 두 번째 섹션 -->
      <div class="col-md-6">
        <h2 class="detail_title">P들을 위한 여행지 룰렛</h2>
        <RouterLink :to="{ name: 'tripRoulette' }">
          <img src="/images/mainViewImg/randomtripImg.jpg" class="randomImg" />
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "@/assets/css/mainView.css";
@import "@/assets/css/themeItem.css";
</style>
