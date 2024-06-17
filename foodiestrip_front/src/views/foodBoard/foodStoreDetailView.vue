<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { getAverage } from "@/api/foodBoard";
import { postStoreinsert, postStoredelete, canStore } from "@/api/storedStore";
import foodStoreReviewItem from "@/components/foodBoard/items/foodStoreReviewItem.vue";
const props = defineProps({
  foodStore: Object,
});

// console.log(props.foodStore);

const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);

/*route*/
const route = useRoute();
const contentId = ref("");
const content = ref({});

/*axios*/
import { localAxios } from "@/util/http-commons";
const local = localAxios();

// 사용자가 선택한 가게 정보 가져옴
const getStoreDetail = async (contentId) => {
  try {
    const response = await local.get("/foodboard/getDetail", { params: { contentId: contentId } });
    content.value = response.data.foodStore;
    console.log(content.value);
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

import { getStoreReviews, getAuthorizedReview } from "@/api/foodBoard";
import { getReviewSummary, getReviewSummaryfilter } from "@/api/summaryAI";
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

  //가게 평균정보 가져오기
  await getAverage(
    contentId.value,
    (response) => {
      avgRank.value = response.data.avgRank;
      avgRankAuthorized.value = response.data.avgRankAuthorized;
      countReivew.value = response.data.countReivew;
      countReivewAuthorized.value = response.data.countReivewAuthorized;
      console.log(avgRank.value);
    },
    (error) => {
      console.error(error);
    }
  );
  const storeinfo = ref({
    userId: "",
    contentId: 0,
  });
  storeinfo.value.contentId = contentId.value;
  storeinfo.value.userId = userInfo.value.userId;
  await canStore(storeinfo.value, (response) => {
    storedColor.value = response.data.color;
    console.log(storedColor.value);
  });

  if (props.foodStore) {
    content.value = props.foodStore;
    contentId.value = content.value.contentId;
    otherReviews.value = getStoreReviews(content.value.contentId, (response) => {
      otherReviews.value = response.data.reviewList;
    });
  } else {
    getStoreDetail(contentId.value);
    otherReviews.value = getStoreReviews(contentId.value, (response) => {
      otherReviews.value = response.data.reviewList;
    });
  }
  await getAuthorizedReview(contentId.value, (response) => {
    authorizedReviews.value = response.data.reviewList;
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

const storeinfo = ref({
  userId: "",
  contentId: 0,
});

const storeStore = async () => {
  storeinfo.value.userId = userInfo.value.userId;
  storeinfo.value.contentId = contentId.value;

  if (storedColor.value === "gray") {
    await postStoreinsert(storeinfo.value, (response) => {
      storedColor.value = response.data.color;
    });
  } else {
    await postStoredelete(storeinfo.value, (response) => {
      storedColor.value = response.data.color;
    });
  }
};

const showAll = ref(true);
const reviewfilter = ref("전체 리뷰");

//리뷰 필터링
const changeShow = async (isCertified) => {
  const prompt = ref("");
  if (isCertified === "yes") {
    showAll.value = false;
    reviewfilter.value = "인증된 리뷰";
    prompt.value = authorizedReviews.value.map((review) => review.foodBoardReview).join(" ");

    await getReviewSummaryfilter(
      prompt.value,
      (response) => {
        generatedText.value = response.data.generations.map((gen) => gen.text).join("\n");
      },
      (error) => {
        console.error("API 요청 중 오류 발생: ", error);
      }
    );
  } else if (isCertified === "no") {
    showAll.value = true;
    reviewfilter.value = "전체 리뷰";
    prompt.value = otherReviews.value.map((review) => review.foodBoardReview).join(" ");

    await getReviewSummaryfilter(
      prompt.value,
      (response) => {
        generatedText.value = response.data.generations.map((gen) => gen.text).join("\n");
      },
      (error) => {
        console.error("API 요청 중 오류 발생: ", error);
      }
    );
  }
};

getStoreDetail(contentId.value);

getReviewSummary(
  route.params.contentId,
  (response) => {
    console.log("contentId", contentId);
    generatedText.value = response.data.generations.map((gen) => gen.text).join("\n");
  },
  (error) => {
    console.error("API 요청 중 오류 발생: ", error);
  }
);
otherReviews.value = getStoreReviews(contentId.value, (response) => {
  otherReviews.value = response.data.reviewList;
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
          {{ content.contentTypeName }}
          <hr />
          <div class="col d-flex justify-content-md-start align-items-center text-center mb-2">
            <img class="kakaoDevImg" src="/images/icons/kogpt.png" alt="" />
            <p class="infoFont">kakao KoGPT를 통해 요약한 리뷰입니다 !</p>
          </div>

          <!-- 리뷰 -->
          <div class="card p-4 summarized-review">
            <h3>
              <strong>{{ reviewfilter }}요약</strong>
            </h3>

            <p>{{ generatedText }}</p>
          </div>
          <!-- 내돈내산 필터링 -->
          <div class="d-flex m-3">
            <div class="btn-group my-money-button">
              <button class="btn" aria-current="page" @click="changeShow('no')">전체 리뷰</button>
              <button class="btn" @click="changeShow('yes')">인증된 리뷰</button>
            </div>
          </div>
          <!-- 리뷰 필터링 -->
          <div class="dropdown col">
            <a
              class="order-btn btn dropdown-toggle rounded-5"
              href="#"
              role="button"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              정렬
            </a>

            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="#">별점높은순</a></li>
              <li><a class="dropdown-item" href="#">별점낮은순</a></li>
              <li><a class="dropdown-item" href="#">최신순</a></li>
              <li><a class="dropdown-item" href="#">오래된순</a></li>
            </ul>
          </div>

          <!-- 리뷰 아이템 -->
          <foodStoreReviewItem
            v-show="showAll"
            v-for="review in otherReviews"
            :key="review"
            :review="review"
          />
          <foodStoreReviewItem
            v-show="!showAll"
            v-for="review in authorizedReviews"
            :key="review"
            :review="review"
          />
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
</style>
