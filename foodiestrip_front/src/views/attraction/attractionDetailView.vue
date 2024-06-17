<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { attractionView } from "@/api/attraction";
const props = defineProps({
  foodStore: Object,
});

/*route*/
const route = useRoute();
const contentId = ref("");
const content = ref({});

// 사용자가 선택한 가게 정보 가져옴
const otherReviews = ref({});
const authorizedReviews = ref({});

const avgRank = ref(0);
const avgRankAuthorized = ref(0);
const countReivew = ref(0);
const countReivewAuthorized = ref(0);

const storedColor = ref("gray"); // 초기값
const generatedText = ref(null);

onMounted(async () => {
  contentId.value = route.params.contentId;
  console.log("contentId.value", contentId.value);

  await attractionView(route.params.contentId, (response) => {
    content.value = response.data.attraction;
    console.log(content.value);
  });
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
</script>

<template>
  <div class="container card mt-5 p-5">
    <div class="row">
      <!-- 가게 상호명 및 정보 -->
      <div class="col-9 store-info">
        <h2>
          <strong>{{ content.title }}</strong>
        </h2>
        <p class="mb-0">
          <strong
            >내돈내산 리뷰 {{ countReivewAuthorized }}개
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="5"
              height="5"
              fill="gray"
              class="bi bi-circle-fill"
              viewBox="0 0 16 16"
            >
              <circle cx="8" cy="8" r="8" />
            </svg>
            방문자 전체리뷰 {{ countReivew }}개</strong
          >
        </p>
        <p>
          푸디즈 인증 평균 평점
          <img
            src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='%23FFBD39' class='bi bi-star-fill' viewBox='0 0 16 16'%3E%3Cpath d='M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z'/%3E%3C/svg%3E"
            alt="Star Icon"
          />
          {{ avgRankAuthorized }}
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="5"
            height="5"
            fill="gray"
            class="bi bi-circle-fill"
            viewBox="0 0 16 16"
          >
            <circle cx="8" cy="8" r="8" />
          </svg>
          방문자 평균 평점
          <img
            src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='%23FFBD39' class='bi bi-star-fill' viewBox='0 0 16 16'%3E%3Cpath d='M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z'/%3E%3C/svg%3E"
            alt="Star Icon"
          />
          {{ avgRank }}
        </p>
      </div>
      <!-- 저장 버튼 -->
      <div class="col" v-show="!props.foodStore">
        <button class="jjim btn float-end" @click="storeStore">
          <img :src="imgSrc" alt="Star Icon" />

          <br />
          저장
        </button>
      </div>
      <div class="row">
        <div class="col">
          <!-- 가게 상세정보 -->
          <p>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="currentColor"
              class="bi bi-geo-alt-fill"
              viewBox="0 0 16 16"
            >
              <path
                d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10m0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6"
              />
            </svg>
            {{ content.addr1 }}
          </p>
          <p>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="currentColor"
              class="bi bi-telephone-fill"
              viewBox="0 0 16 16"
            >
              <path
                fill-rule="evenodd"
                d="M1.885.511a1.745 1.745 0 0 1 2.61.163L6.29 2.98c.329.423.445.974.315 1.494l-.547 2.19a.68.68 0 0 0 .178.643l2.457 2.457a.68.68 0 0 0 .644.178l2.189-.547a1.75 1.75 0 0 1 1.494.315l2.306 1.794c.829.645.905 1.87.163 2.611l-1.034 1.034c-.74.74-1.846 1.065-2.877.702a18.6 18.6 0 0 1-7.01-4.42 18.6 18.6 0 0 1-4.42-7.009c-.362-1.03-.037-2.137.703-2.877z"
              />
            </svg>
            {{ content.tel }} 전화번호
          </p>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="16"
            height="16"
            fill="currentColor"
            class="bi bi-egg-fried"
            viewBox="0 0 16 16"
          >
            <path d="M8 11a3 3 0 1 0 0-6 3 3 0 0 0 0 6" />
            <path
              d="M13.997 5.17a5 5 0 0 0-8.101-4.09A5 5 0 0 0 1.28 9.342a5 5 0 0 0 8.336 5.109 3.5 3.5 0 0 0 5.201-4.065 3.001 3.001 0 0 0-.822-5.216zm-1-.034a1 1 0 0 0 .668.977 2.001 2.001 0 0 1 .547 3.478 1 1 0 0 0-.341 1.113 2.5 2.5 0 0 1-3.715 2.905 1 1 0 0 0-1.262.152 4 4 0 0 1-6.67-4.087 1 1 0 0 0-.2-1 4 4 0 0 1 3.693-6.61 1 1 0 0 0 .8-.2 4 4 0 0 1 6.48 3.273z"
            />
          </svg>
          <span v-if="content.contentTypeId === 12"> 관광지</span>
          <span v-else-if="content.contentTypeId === 14"> 문화시설</span>
          <span v-else-if="content.contentTypeId === 15"> 축제공연행사</span>
          <span v-else-if="content.contentTypeId === 25"> 여행코스</span>
          <span v-else-if="content.contentTypeId === 28"> 레포츠</span>
          <span v-else-if="content.contentTypeId === 32"> 숙박</span>
          <span v-else-if="content.contentTypeId === 38"> 쇼핑</span>
          <span v-else-if="content.contentTypeId === 39"> 음식점</span>
          <span v-else>기타</span>

          <hr />
          <div class="d-flex align-center">
            <img
              class="store-img"
              :src="content.firstImage"
              alt="storeImage"
              v-if="content.firstImage"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.jjim {
  background-color: white;
  font-size: small;
  /* border-color: black; */
  border-radius: 10px;
  box-shadow: 0px 0px 5px 0px rgba(0, 0, 0, 0.1);
}

.jjim:hover {
  border-color: black;
}

.summarized-review {
  background-color: #ffedee;
  border: 0;
}

.store-img {
  object-fit: cover;
  width: 100%;
  /* height: 30; */
}
.d-flex {
  justify-content: center;
}
.my-money-button * {
  background-color: #fb3640;
  color: white;
  justify-content: center;
}

.my-money-button .btn.active,
.my-money-button .btn:hover {
  background-color: #d1010b;
  color: white;
}

.store-info {
  text-align: start;
}
.infoFont {
  color: gray;
  font-size: small;
  margin-bottom: 0;
}

.kakaoDevImg {
  width: 35px;
  height: fit-content;
  margin-right: 10px;
}

.store-img {
  width: 100%;
  height: auto;
  object-fit: cover;
  border-radius: 10px; /* 둥근 모서리 추가 */
}
</style>
