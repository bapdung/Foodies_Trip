<script setup>
import { KakaoMap, KakaoMapMarker } from "vue3-kakao-maps";

import { ref } from "vue";
import { useRouter } from "vue-router";

//유저 확인
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";

const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);
const router = useRouter();

const kakao = window.kakao;

const map = ref();
const markerList = ref([]);
const search = ref({
  keyward: "",
  pgn: "",
  results: [],
});

//custom overlay

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

const onClickMapMarker = (markerItem) => {
  console.log(markerItem.infoWindow);
  keyward.value = markerItem.infoWindow.content;
  addr.value = markerItem.infoWindow.addr;
  if (markerItem.infoWindow?.visible !== null && markerItem.infoWindow?.visible !== undefined) {
    markerItem.infoWindow.visible = !markerItem.infoWindow.visible;
  } else {
    markerItem.infoWindow.visible = true;
  }
};

// 카테고리를 입력받아 검색
const searchPlace = (e) => {
  keyward.value = "";
  keyward.value = e.target.value.trim();
  console.log("keyward", keyward.value);
  console.log(e);
  if (keyward.value.length === 0) {
    return;
  }

  const ps = new kakao.maps.services.Places();
  ps.keywordSearch(keyward.value, (data, status, pgn) => {
    console.log(data);
    search.value.keyward = keyward.value;
    search.value.pgn = pgn;
    search.value.results = data;
    markerList.value = [];

    if (status === kakao.maps.services.Status.OK) {
      const bounds = new kakao.maps.LatLngBounds();

      for (let marker of data) {
        const markerItem = {
          lat: marker.y,
          lng: marker.x,
          infoWindow: {
            content: marker.place_name,
            categoryName: marker.category_name,
            id: marker.id,
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
  });
};

const imageSrc = ref(null);
const ratings = ref(0);
const previewImage = (e) => {
  const file = e.target.files[0];
  if (file) {
    const reader = new FileReader();

    reader.onload = (e) => {
      // 이미지 파일의 데이터 URL을 데이터 속성에 할당
      const base64String = e.target.result.split(",")[1];
      foodBoard.value.foodBoardImage = base64String;
      imageSrc.value = e.target.result;
    };

    reader.readAsDataURL(file); // 파일을 Data URL 형태로 읽음
  }
};

//뒤로가기
const goBack = () => {
  window.history.back();
};

const selectedfoodName = ref("기타"); //contentTypeName으로 넣을겨
//저장하려는 코드
const foodBoard = ref({
  contentId: 0, // 맛집 번호
  userId: "", // 사용자 ID
  foodStoreTitle: "", // 가게명
  foodBoardReview: "", // 리뷰 내용
  foodBoardHit: 0, // 조회수
  foodBoardJjim: 0, // 찜 수
  foodBoardRank: 0, // 평점
  foodBoardMyMoney: false, // 내 돈 주고 싶은지 여부
  foodBoardAddr: "", // 가게 주소
  foodBoardReceiptImage: " ", // 영수증 이미지
  foodBoardImage: "", // 음식 이미지
  contentTypeName: "",
  multifile: "", //음식 멀티파일 이미지
});

const insertReview = () => {
  markerList.value.filter((marker) => {
    if (marker.infoWindow.content == keyward.value) {
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

import { localAxios } from "@/util/http-commons";
const local = localAxios();

const postReviewData = async () => {
  try {
    console.log("axios data", foodBoard.value);
    await local.post(`/foodboard/board`, foodBoard.value, {
      headers: {
        "Content-Type": "application/json",
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
