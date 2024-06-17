<script setup>
import keywardSearchResultItem from "@/components/searchresult/keywardSearchResultItem.vue";
import { KakaoMap } from "vue3-kakao-maps";
import { ref, watch, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getSidoList, getGugunList, attractionList } from "@/api/attraction";

const kakao = window.kakao;
const route = useRoute();

const attractionDto = ref({
  contentId: 0,
  sidoCode: 0,
  gugunCode: 0,
  contentTypeId: 0,
  title: "",
});

const map = ref(null);
const markerList = ref([]);
const search = ref({
  keyward: "",
  pgn: "",
  results: [],
});

const lat = ref(37.566826);
const lng = ref(126.9786567);
const level = ref(3);

const onLoadKakaoMap = (mapRef) => {
  map.value = mapRef;
};

const showPlace = (place, lev = 5) => {
  if (map.value) {
    console.log(place);
    map.value.panTo(new kakao.maps.LatLng(place.latitude, place.longitude));
    level.value = lev;
  }
};

/*
여기부터 sido gugun
*/

const sidoList = ref([]);
const gugunList = ref([]);

const selectedSidoCode = ref(0);
const selectedGugunCode = ref(0);
const selectedfoodCode = ref(0);

const searchKeyward = ref("");
const hotplaceList = ref([]);

watch(selectedSidoCode, async (newVal, oldVal) => {
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
  attractionDto.value.title = route.params.keyword;
  await attractionList(attractionDto.value, (response) => {
    hotplaceList.value = response.data.attractionList;
    console.log(hotplaceList.value);
  });

  await getSidoList("", (response) => {
    sidoList.value = response.data.sidoList;
    gugunList.value = response.data.gugunList;
  });
});

/*keyward Search 를 위한 코드 */

const searchHotPlace = async () => {
  console.log("핫플검색시작", attractionDto.value);
  attractionDto.value.sidoCode = selectedSidoCode.value;
  attractionDto.value.gugunCode = selectedGugunCode.value;
  attractionDto.value.contentTypeId = selectedfoodCode.value;
  attractionDto.value.title = searchKeyward.value;
  await attractionList(attractionDto.value, (response) => {
    hotplaceList.value = response.data.attractionList;
    console.log(hotplaceList.value);
  });
};

/*마커 변경 */
// hotplaceList가 변경될 때 실행될 함수
watch(
  hotplaceList,
  (newList) => {
    // 기존 마커를 모두 지우기
    clearMarkers();
    // 새로운 마커를 추가하기
    addMarkers(newList);
  },
  { deep: true }
);

// 기존 마커를 저장할 배열
let markers = [];

// 마커를 모두 지우는 함수
function clearMarkers() {
  for (let marker of markers) {
    marker.setMap(null);
  }
  markers = [];
}

// 새로운 마커를 추가하는 함수
function addMarkers(places) {
  // 마커 클러스터러를 생성합니다
  const clusterer = new kakao.maps.MarkerClusterer({
    map: map.value, // 마커를 표시할 지도 객체
    averageCenter: true, // 클러스터에 포함된 마커들의 평균 위치를 클러스터 마커 위치로 설정
    minLevel: 10, // 클러스터 할 최소 지도 레벨
  });

  const newMarkers = places
    .map((place) => {
      if (
        place.latitude >= 33 &&
        place.latitude <= 43 &&
        place.longitude >= 124 &&
        place.longitude <= 132
      ) {
        const position = new kakao.maps.LatLng(place.latitude, place.longitude);
        const marker = new kakao.maps.Marker({
          position: position,
        });

        const infowindow = new kakao.maps.InfoWindow({
          content: `<div>${place.title}</div>`, // 마커에 표시될 내용
        });

        kakao.maps.event.addListener(marker, "click", function () {
          infowindow.open(map.value, marker);
        });

        return marker;
      } else {
        return null; // 유효하지 않은 위치의 경우 null 반환
      }
    })
    .filter((marker) => marker !== null); // null 값을 필터링하여 유효한 마커만 남김

  // 클러스터러에 마커를 추가합니다
  clusterer.addMarkers(newMarkers);
  markers = newMarkers;

  if (places.length > 0) {
    showPlace(places[0], 5);
  }
}
</script>

<template>
  <div class="keward-result">
    <div class="btnGroup row d-flex search-bar align-items-center">
      <div class="col-lg-2 col-md-3 col-sm-4">
        <select
          id="search-sido"
          class="form-select me-2 rounded-5 select w-100"
          v-model="selectedSidoCode"
          style="height: 50px"
        >
          <option value="0">시군구</option>
          <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoCode">
            {{ sido.sidoName }}
          </option>
        </select>
      </div>
      <div class="col-lg-2 col-md-3 col-sm-4">
        <select
          id="search-gugun"
          class="form-select me-2 rounded-5 select w-100"
          v-model="selectedGugunCode"
          style="height: 50px"
        >
          <option value="0">읍면동</option>
          <option v-for="gugun in gugunList" :key="gugun.gugunCode" :value="gugun.gugunCode">
            {{ gugun.gugunName }}
          </option>
        </select>
      </div>
      <div class="col-lg-2 col-md-3 col-sm-4">
        <select
          id="search-food"
          class="form-select me-2 rounded-5 select w-100"
          v-model="selectedfoodCode"
          style="height: 50px"
        >
          <option value="0" selected>분류</option>
          <option value="12">관광지</option>
          <option value="14">문화시설</option>
          <option value="15">축제공연행사</option>
          <option value="25">여행코스</option>
          <option value="28">레포츠</option>
          <option value="32">숙박</option>
          <option value="28">쇼핑</option>
          <option value="39">한식</option>
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
        <div class="input-group">
          <input
            type="text"
            class="form-control"
            aria-describedby="button-addon2"
            @keyup.enter="searchHotPlace"
            v-model="searchKeyward"
            style="height: 50px"
          />
          <button
            type="button"
            id="button-addon2"
            class="search-btn btn rounded-end-5"
            @click="searchHotPlace"
          >
            <i class="bi bi-search"></i>
          </button>
        </div>
      </div>
    </div>

    <div>
      <template v-show="!hotplaceList.value">
        <h3>"{{ searchKeyward }}" 키워드 검색 결과</h3>
      </template>
    </div>
    <div style="height: 50px"></div>
    <div class="mapBox">
      <KakaoMap
        :level="level"
        :lat="lat"
        :lng="lng"
        @onLoadKakaoMap="onLoadKakaoMap"
        width="800px"
        height="400px"
      >
      </KakaoMap>
    </div>
    <div style="height: 50px"></div>
    <div class="results">
      <div class="row justify-content-center">
        <div
          class="resultBox col-md-6"
          v-for="rs in hotplaceList"
          :key="rs.id"
          @click="showPlace(rs)"
        >
          <keywardSearchResultItem :result="rs" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "@/assets/css/map.css";

.resultBox {
  display: flex;
  padding: 10px 5px 10px 5px;
  justify-content: center;
}

.btn {
  background-color: #fb3640;
}
</style>
