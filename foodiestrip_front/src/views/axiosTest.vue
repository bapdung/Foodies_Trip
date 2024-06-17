<script setup>
import { onMounted, ref, computed } from "vue";
import { useRoute } from "vue-router";
import { localAxios } from "@/util/http-commons";

const local = localAxios();
const route = useRoute();

const recommendList = ref([]);
const cityName = ref("");

const data = ref([]) // 전체 데이터를 저장하는 배열
const currentPage = ref(1) // 현재 페이지
const pageSize = ref(10) // 페이지 사이즈
// 보여줄 최대 페이지 번호 개수
const maxPageNumbers = 5;

// 보여줄 페이지 번호들을 계산
const visiblePages = computed(() => {
  let startPage = Math.max(currentPage.value - Math.floor(maxPageNumbers / 2), 1);
  let endPage = startPage + maxPageNumbers - 1;

  if (endPage > totalPages.value) {
    endPage = totalPages.value;
    startPage = Math.max(endPage - maxPageNumbers + 1, 1);
  }

  return Array.from({ length: (endPage - startPage + 1) }, (_, index) => startPage + index);
});

// 총 페이지 수를 동적으로 계산
const totalPages = computed(() => {
  return Math.ceil(data.value.length / pageSize.value)
})

// 현재 페이지에 표시할 데이터를 계산하는 로직
const currentData = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  return data.value.slice(startIndex, startIndex + pageSize.value); 
});

// 페이지를 변경하는 함수
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const getattractionList = async (regionCode) => {
  try {
    const response = await local.get("/attraction/recommend", {
      params: { sidoCode: regionCode },
    });
    data.value = response.data.recommendList; 
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};


onMounted(() => {
  const regionCode = route.params.regionCode;

  if (regionCode == "1") {
    cityName.value = "서울";
  }
  if (regionCode == "3") {
    cityName.value = "대전";
  }
  if (regionCode == "6") {
    cityName.value = "부산";
  }
  if (regionCode == "39") {
    cityName.value = "제주";
  }

  getattractionList(regionCode);
});

</script>

<template>
  <div style="height: 50px; background-color: gray">
    <div class="row justify-content-center">
      <h1 style="margin: 0 auto; text-align: center">
        {{ cityName }}의 관광지 정보입니다
      </h1>
      <div class="col-lg-8 col-md-10 col-sm-12">
        <h2 class="my-3 py-3 shadow-sm bg-light text-center">
          <mark class="sky">여행지 목록</mark>
        </h2>
      </div>
      <div class="col-lg-8 col-md-10 col-sm-12">
        <table class="table table-hover">
          <thead>
            <tr class="text-center">
              <th scope="col">글번호</th>
              <!-- 번호 -->
              <th scope="col">여행 이미지</th>
              <!--  -->
              <th scope="col">제목</th>
              <th scope="col">주소</th>
            </tr>
          </thead>
          <tbody>
    <tr
      class="text-center"
      v-for="(attraction, index) in currentData"
      :key="index"
    >
      <th scope="row">{{ index + 1 + (currentPage - 1) * pageSize }}</th>
      <td><img :src="attraction.firstImage" class="detailImg" /></td>
      <td class="text-start">
        <a href="#" style="text-decoration: none; text-align: center">
          {{ attraction.title }}
        </a>
      </td>
      <td>{{ attraction.addr1 }}</td>
    </tr>
  </tbody>
        </table>
      </div>
      <div class="row">
        <ul class="pagination justify-content-center">
    <li class="page-item" v-if="currentPage > 1">
      <a class="page-link" href="#" @click.prevent="changePage(1)">처음</a>
    </li>
    <li class="page-item" v-if="currentPage > 1">
      <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)">이전</a>
    </li>
    <li class="page-item" v-for="page in visiblePages" :class="{ active: page === currentPage }" :key="page">
      <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
    </li>
    <li class="page-item" v-if="currentPage < totalPages">
      <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">다음</a>
    </li>
    <li class="page-item" v-if="currentPage < totalPages">
      <a class="page-link" href="#" @click.prevent="changePage(totalPages)">마지막</a>
    </li>
  </ul>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 전국 여행지 확인하기 searchmap */
.maps {
  margin: 0 auto;
  justify-content: center;
  text-align: center;
}

.detailImg {
  height: 200px;
  width: 200px;
}
</style>
