<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { localAxios } from "@/util/http-commons";

const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);
const router = useRouter();
const local = localAxios();

const kakao = window.kakao;

const map = ref();
const markerList = ref([]);
const search = ref({
  keyward: "",
  pgn: "",
  results: [],
});

// custom overlay
const lat = ref(37.566826);
const lng = ref(126.9786567);
const level = ref(4);

const onLoadKakaoMap = (mapRef) => {
  map.value = mapRef;
  const ps = new kakao.maps.services.Places();
  ps.keywordSearch("역삼역 맛집", placesSearchCB);
};

const placesSearchCB = (data, status) => {
  if (status === kakao.maps.services.Status.OK) {
    const bounds = new kakao.maps.LatLngBounds();
    for (let marker of data) {
      const markerItem = {
        lat: marker.y,
        lng: marker.x,
        infoWindow: {
          content: marker.place_name,
          id: marker.id,
          categoryName: marker.category_name,
          phone: marker.phone,
          url: marker.place_url,
          addr: marker.road_address_name,
          visible: false,
        },
      };
      markerList.value.push(markerItem);
      bounds.extend(new kakao.maps.LatLng(Number(marker.y), Number(marker.x)));
    }
    map.value?.setBounds(bounds);
  }
};

const keyward = ref("");
const addr = ref("");
const imageSrc = ref(null);
const ratings = ref(0);
const previewImage = (e) => {
  const file = e.target.files[0];
  foodBoard.value.multipartfile = file;
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      const base64String = e.target.result.split(",")[1];
      foodBoard.value.foodBoardImage = base64String;
      imageSrc.value = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

const goBack = () => {
  window.history.back();
};

const selectedfoodName = ref("기타");
const foodBoard = ref({
  contentId: 0,
  userId: "",
  foodStoreTitle: "",
  foodBoardReview: "",
  foodBoardHit: 0,
  foodBoardJjim: 0,
  foodBoardRank: 0,
  foodBoardMyMoney: false,
  foodBoardAddr: "",
  foodBoardReceiptImage: " ",
  foodBoardImage: "",
  contentTypeName: "",
  multipartfile: null, // null로 초기화
});

const insertReview = () => {
  markerList.value.filter((marker) => {
    if (marker.infoWindow.content === keyward.value) {
      foodBoard.value.contentId = marker.infoWindow.id;
      foodBoard.value.foodBoardAddr = marker.infoWindow.addr;
      foodBoard.value.foodStoreTitle = marker.infoWindow.content;
      foodBoard.value.userId = userInfo.value.userId;
      foodBoard.value.foodBoardRank = ratings.value;
      foodBoard.value.contentTypeName = selectedfoodName.value;
    }
  });
  postReviewData();
};

const postReviewData = async () => {
  try {
    const formData = new FormData();

    // 다른 필드 추가
    for (const key in foodBoard.value) {
      if (key !== "multipartfile") {
        // 파일이 아닌 필드만 추가
        formData.append(key, foodBoard.value[key]);
      }
    }

    // 파일을 'multipartfile'이라는 이름으로 추가
    if (foodBoard.value.multipartfile) {
      console.log(foodBoard.value.multipartfile);
      formData.append("multipartfile", foodBoard.value.multipartfile);
    }

    // axios로 전송
    await local.post(`/foodboard/board`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    window.alert("저장되었습니다!");
    router.replace({ name: "foodBoard" });
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};
</script>

<template>
  <button @click="goBack" class="back-button">&lt;</button>
  <div class="keward-result">
    <div class="row">
      <div class="storeBox">
        <KakaoMap
          :level="level"
          :lat="lat"
          :lng="lng"
          @onLoadKakaoMap="onLoadKakaoMap"
          width="400px"
          height="400px"
        >
          <KakaoMapMarker
            v-for="(marker, index) in markerList"
            :key="marker.key === undefined ? index : marker.key"
            :lat="marker.lat"
            :lng="marker.lng"
            :infoWindow="marker.infoWindow"
            :clickable="true"
            @onClickKakaoMapMarker="onClickMapMarker(marker)"
          />
        </KakaoMap>
        <div class="buf"></div>
        <div class="col-lg-5 col-md-4 col-sm-12">
          <fieldset>
            <div class="form-check">
              <input type="checkbox" @change="buyMyMoney" v-model="foodBoard.foodBoardMyMoney" />
              <label for="buyMyMoney">내돈 내산</label>
            </div>
            <div v-if="foodBoard.foodBoardMyMoney" id="showBill">
              <p>영수증 인증하기</p>
              <input type="file" @change="previewImage" accept="image/*" />
            </div>
            <div class="star-rating space-x-4 mx-auto">
              <input type="radio" id="5-stars" name="rating" value="5" v-model="ratings" />
              <label for="5-stars" class="star pr-4">★</label>
              <input type="radio" id="4-stars" name="rating" value="4" v-model="ratings" />
              <label for="4-stars" class="star">★</label>
              <input type="radio" id="3-stars" name="rating" value="3" v-model="ratings" />
              <label for="3-stars" class="star">★</label>
              <input type="radio" id="2-stars" name="rating" value="2" v-model="ratings" />
              <label for="2-stars" class="star">★</label>
              <input type="radio" id="1-star" name="rating" value="1" v-model="ratings" />
              <label for="1-star" class="star">★</label>
            </div>
            <div class="input-group mb-5">
              <input
                type="text"
                class="form-control"
                aria-describedby="button-addon2"
                placeholder="정확한 주소 또는 가게명을 입력해주세요"
                @keyup.enter="searchPlace"
              />
              <button
                class="btn btn-outline-secondary"
                type="submit"
                id="button-addon2"
                @click="searchPlace"
              >
                주소 검색하기
              </button>
            </div>
            <div>
              <h6>선택된 주소</h6>
              <h5 class="mb-0">{{ keyward }}</h5>
              <p class="mt-0">{{ addr }}</p>
              <!-- contentTypeId 선택 -->
              <h6>음식점 분류</h6>
              <select
                id="search-food"
                class="form-select me-2 rounded-5 select"
                v-model="selectedfoodName"
              >
                <option value="술집">술집</option>
                <option value="카페">카페</option>
                <option value="치킨">치킨</option>
                <option value="고기">고기</option>
                <option value="경양식">경양식</option>
                <option value="패스트푸드">패스트푸드</option>
                <option value="한식">한식</option>
                <option value="일식">일식</option>
                <option value="중국식">중국식</option>
                <option value="해외(동남아/유럽)">해외(동남아/유럽)</option>
                <option value="분식">분식</option>
                <option value="뷔페">뷔페</option>
                <option value="기타">기타</option>
              </select>
              <button @click="clearSearch">x</button>
            </div>
            <input type="file" @change="previewImage" accept="image/*" />

            <div class="reviewBox">
              <div class="mb-3">
                <label for="exampleFormControlTextarea1" class="form-label">후기 작성</label>
                <textarea
                  class="form-control"
                  id="exampleFormControlTextarea1"
                  rows="3"
                  v-model="foodBoard.foodBoardReview"
                ></textarea>
              </div>
            </div>
            <button type="submit" @click="insertReview">저장하기</button>
          </fieldset>
        </div>
      </div>
      <div v-if="imageSrc" id="preview">
        <img :src="imageSrc" style="max-width: 100%; height: auto" />
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "@/assets/css/map.css";

.storeBox {
  display: flex;
  padding: 10px 5px 10px 5px;
  justify-content: center;
}

.buf {
  width: 20px;
  height: 20px;
}

.star-rating {
  display: flex;
  flex-direction: row-reverse;
  font-size: 2.25rem;
  line-height: 2.5rem;
  justify-content: space-around;
  padding: 0 0.2em;
  text-align: center;
  width: 5em;
}

.star-rating input {
  display: none;
}

.star-rating label {
  -webkit-text-fill-color: transparent; /* Will override color (regardless of order) */
  -webkit-text-stroke-width: 2.3px;
  -webkit-text-stroke-color: #2b2a29;
  cursor: pointer;
}

.star-rating :checked ~ label {
  -webkit-text-fill-color: gold;
}

.star-rating label:hover,
.star-rating label:hover ~ label {
  -webkit-text-fill-color: #fff58c;
}

#search-food {
  width: 150px;
}
</style>
