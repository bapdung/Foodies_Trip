<script setup lang="ts">
import { KakaoMap, KakaoMapMarker, KakaoMapPolyline } from "vue3-kakao-maps";
import draggable from "vuedraggable";
import { useUserStore } from "@/stores/user";
import { insertPlan, insertPlanDetail } from "@/api/plan";
import { storeToRefs } from "pinia";
import { ref, watch, onMounted, defineProps } from "vue";
import planModalItem from "@/components/planBoard/items/planRecommendItem.vue";
import planCheckListItem from "@/components/planBoard/items/planCheckListItem.vue";

import { useRoute } from "vue-router";
//user정보
const userStore = useUserStore();

const { userInfo } = storeToRefs(userStore);

// Route parameter를 사용하기 위해 useRoute 훅을 사용합니다.
const route = useRoute();

// defineProps로 props 정의
const props = defineProps({
  plan: {
    type: Object,
    required: false,
    default: null,
  },
});

const myPlan = ref([]);
const userPlan = ref({
  planTitle: "",
  userId: userInfo.value.userId,
  planShare: false,
  sido: "",
  gugun: 1,
  planDetailDto: null, //세부 일정
  planChecklistDto: null, //체크리스트
});

const getPlan = (plan) => {
  userPlan.value.sido = plan.sido;
  userPlan.value.gugun = plan.gugun;
  myPlan.value = plan.planDetailDto;
  myPlan.value = plan.planDetailDto;
  myPlan.value = myPlan.value.map((item, index) => ({
    planNo: 0, // 실제 plan_no 값으로 교체
    userId: userInfo.value.userId,
    id: item.contentId,
    routeDay: 1, // 실제 날짜 값으로 교체
    routeNo: index + 1, // 순서를 1부터 시작
    place_name: item.title,
    address_name: item.address,
    detail: "", // item.detail 값이 없으면 빈 문자열
  }));
};
if (props.plan) {
  userPlan.value.sido = props.plan.sido;
  userPlan.value.gugun = props.plan.gugun;
  myPlan.value = props.plan.planDetailDto;
  myPlan.value = props.plan.planDetailDto;
  myPlan.value = myPlan.value.map((item, index) => ({
    planNo: 0, // 실제 plan_no 값으로 교체
    userId: userInfo.value.userId,
    id: item.contentId,
    routeDay: 1, // 실제 날짜 값으로 교체
    routeNo: index + 1, // 순서를 1부터 시작
    place_name: item.title,
    address_name: item.address,
    detail: "", // item.detail 값이 없으면 빈 문자열
  }));
}

console.log(userPlan.value);

//map
const kakao = window.kakao;

const map = ref();
const markerList = ref([]);

const searchList = ref([]);

const search = ref({
  keyward: "",
  pgn: "",
  results: [],
});

const toggleCompletion = (item) => {
  item.completed = !item.completed;
};

const tripDay = ref(1);

const lat = ref(37.566826);
const lng = ref(126.9786567);

const onLoadKakaoMap = (mapRef) => {
  map.value = mapRef;
};

const latLngList = ref([]);

const onClickMapMarker = (markerItem) => {
  if (markerItem.infoWindow?.visible !== null && markerItem.infoWindow?.visible !== undefined) {
    markerItem.infoWindow.visible = !markerItem.infoWindow.visible;
  } else {
    markerItem.infoWindow.visible = true;
  }
};

const searchPlace = (e) => {
  const keyward = e.target.value.trim();
  if (keyward.length === 0) {
    return;
  }

  const ps = new kakao.maps.services.Places();
  ps.keywordSearch(keyward, (data, status, pgn) => {
    search.value.keyward = keyward;
    search.value.pgn = pgn;
    search.value.results = data;
    searchList.value = data;
    console.log(searchList.value);
    markerList.value = [];

    if (status === kakao.maps.services.Status.OK) {
      const bounds = new kakao.maps.LatLngBounds();

      for (let marker of data) {
        const markerItem = {
          lat: marker.y,
          lng: marker.x,
          infoWindow: {
            content: marker.place_name,
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

// watch myPlan 변경 사항
watch(myPlan, (newVal) => {
  console.log("myplan change", myPlan.value);
  latLngList.value = newVal.map((item) => ({ lat: item.y, lng: item.x }));
  updateMarkersAndPolyline();
  console.log('latlngDX change',latlngDX.value)
});

let polyline = null;
const latlngDX = ref([]);
// 두 지점 간의 거리 계산 함수
const calculateDistance = (latlng1, latlng2) => {
  const lat1 = latlng1.lat;
  const lng1 = latlng1.lng;
  const lat2 = latlng2.lat;
  const lng2 = latlng2.lng;

  const radLat1 = (Math.PI * lat1) / 180;
  const radLat2 = (Math.PI * lat2) / 180;
  const theta = lng1 - lng2;
  const radTheta = (Math.PI * theta) / 180;
  let dist =
    Math.sin(radLat1) * Math.sin(radLat2) +
    Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radTheta);
  dist = Math.acos(dist);
  dist = (dist * 180) / Math.PI;
  dist = dist * 60 * 1.1515;
  dist = dist * 1.609344; // 단위 mile 에서 km 변환
  dist = dist * 1000; // 단위 km 에서 m 로 변환
  return Math.round(dist);
};

// updateMarkersAndPolyline 함수 수정
const updateMarkersAndPolyline = () => {
  if (polyline) {
    polyline.setMap(null);
  }

  // 폴리라인 초기화
  polyline = new kakao.maps.Polyline({
    path: [],
    strokeWeight: 3,
    strokeColor: "#FF0000",
    strokeOpacity: 0.7,
    strokeStyle: "solid",
  });

  // 기존 폴리라인 제거
  polyline.setMap(null);
  latlngDX.value = [];
  // 새로운 마커 리스트를 이용하여 폴리라인 다시 그리기
  const path = latLngList.value.map((latlng, index) => {
    if (index > 0) {
      const prevLatLng = latLngList.value[index - 1];
      const distance = calculateDistance(prevLatLng, latlng);
      console.log(`Distance between ${index - 1} and ${index}: ${distance}m`);
      latlngDX.value.push(distance)
    }

    return new kakao.maps.LatLng(latlng.lat, latlng.lng);
  });

  polyline.setPath(path);
  polyline.setMap(map.value);
};


const cloneItem = (item) => {
  return { ...item };
};

const showPlace = (place) => {
  map.value.panTo(new kakao.maps.LatLng(place.y, place.x));
};

const isModalVisible = ref(false);
const recommendPlan = () => {
  isModalVisible.value = !isModalVisible.value;
  console.log(isModalVisible.value);
};

const isCheckListVisible = ref(false);
const checkListPlan = () => {
  isCheckListVisible.value = !isCheckListVisible.value;
};

/*axios */
//시도리스트, 구군리스트 받아오기
import { getSidoList, getGugunList } from "@/api/attraction.js";

const sidoList = ref([]);
const gugunList = ref([]);
const selectSidoCode = ref(0);
const selectGugnCode = ref(0);

watch(selectSidoCode, async (newVal, oldVal) => {
  const sido = sidoList.value.filter((sido) => sido.sidoCode == newVal);
  userPlan.value.sido = sido[0].sidoName;
  if (newVal !== null && newVal !== oldVal) {
    await getGugunList(newVal, (response) => {
      gugunList.value = response.data.gugunList;
      selectGugnCode.value = 1;
    });
  }
});

watch(selectGugnCode, (newVal, oldVal) => {
  const gugun = gugunList.value.filter((gugun) => gugun.gugunCode == newVal);
  userPlan.value.gugun = gugun[0].gugunName;
  console.log(userPlan.value.sido,userPlan.value.gugun);
})

onMounted(() => {
  getSidoList("", (response) => {
    sidoList.value = response.data.sidoList;
    gugunList.value = response.data.gugunList;
  });
});

const mapToDatabaseSchema = (planList) => {
  return planList.map((item, index) => ({
    content_id: item.id, // 원본 객체의 id를 content_id로 매핑
    route_day: 1, // 예를 들어, 모두 첫째 날의 계획이라고 가정
    route_no: index + 1, // 순서 번호는 리스트에서의 인덱스로 설정
    title: item.name,
    detail: item.detail || "", // 상세 정보가 없으면 빈 문자열로 설정
  }));
};

//플랜

const userPlanDetail = ref([]); //여행일정
const checklist = ref(); //체크리스트

const handlePlanInsert = async () => {
  //plan_no 기반으로 plan_detail insert
  userPlanDetail.value = myPlan.value.map((item, index) => ({
    planNo: 0, // 실제 plan_no 값으로 교체
    userId: userInfo.value.userId,
    contentId: item.id,
    routeDay: 1, // 실제 날짜 값으로 교체
    routeNo: index + 1, // 순서를 1부터 시작
    title: item.place_name,
    address: item.address_name,
    detail: item.detail || "", // item.detail 값이 없으면 빈 문자열
    x: item.x,
    y: item.y,
  }));

  const isStored = ref(confirm(`'${userPlan.value.planTitle}'을 저장하시겠습니까?`));
  if (isStored.value) {
    const isUpload = ref(confirm("일정표를 사람들에게 공유하시겠습니까?"));
    if (isUpload.value) {
      userPlan.value.planShare = true;
    } else {
      userPlan.value.planShare = false;
    }
    userPlan.value.planDetailDto = userPlanDetail.value;
    userPlan.value.planChecklistDto = checklist.value;
    console.log(userPlan.value);
    console.log(userPlan.value.planChecklistDto);
    await insertPlan(
      userPlan.value,
      (response) => {
        console.log(response.data.planNo);
      },
      (error) => {
        console.error(error);
      }
    );
  }
};

const addPlanItem = (element) => {
  console.log(element);
  myPlan.value = [...myPlan.value, element];
};

const deletePlanItem = (element) => {
  console.log("ele", element);
  myPlan.value = myPlan.value.filter((item) => item.id !== element.id);
  console.log(myPlan.value);
};

const getCheckList = (checkList) => {
  checklist.value = checkList;
};
</script>

<template>
  <div class="map-area">
    <div class="searchbox">
      <div class="indraggable mb-3 mt-2 d-flex">
        <input
          type="text"
          class="form-control"
          aria-describedby="button-addon2"
          @keyup.enter="searchPlace"
        />
        <button
          class="btn btn-outline-secondary"
          type="button"
          id="button-addon2"
          @click="searchPlace"
        >
          검색
        </button>
      </div>

      <div class="results">
        <draggable
          v-model="searchList"
          :group="{ name: 'places', pull: 'clone', put: false }"
          :clone="cloneItem"
          v-if="searchList.length > 0"
          item-key="id"
        >
          <template #item="{ element }">
            <div class="place" @click="showPlace(element)">
              <div class="row">
                <div class="col-9">
                  <h4>{{ element.place_name }}</h4>
                  <span class="addr">{{ element.address_name }}</span>
                </div>
                <div class="col-3">
                  <button class="float-end" @click="addPlanItem(element)">추가</button>
                </div>
              </div>
            </div>
          </template>
        </draggable>
      </div>
    </div>

    <KakaoMap
      level="5"
      :lat="lat"
      :lng="lng"
      @onLoadKakaoMap="onLoadKakaoMap"
      width="100%"
      height="1000"
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
      <!-- <KakaoMapPolyline :latLngList="latLngList" :endArrow="true" /> -->
    </KakaoMap>

    <div class="planBox">
      <div class="planTitle" :value="userInfo.userName">
        <h6 class="mt-2">
          <b>{{ userInfo.userNickname }} </b> 's Plan
        </h6>
        <div>
          <button class="planBtn" @click="handlePlanInsert">저장</button>
        </div>
      </div>
      <!-- 지역 -->
      <div class="d-flex">
        <div class="input-group ms-5">
          <select
            id="search-sido"
            class="col-4 form-select me-2 rounded-5 select"
            v-model="selectSidoCode"
          >
            <option value="0" v-if="selectSidoCode==0">도시</option>
            <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoCode">
              {{ sido.sidoName }}
            </option>
          </select>
          <select
            id="search-gugun"
            class="col-4 form-select me-2 rounded-5 select"
            v-model="selectGugnCode"
          >
          <option value="0" v-if="selectGugnCode==0">구군</option>
            <option v-for="gugun in gugunList" :key="gugun.gugunCode" :value="gugun.gugunCode">
              {{ gugun.gugunName }}
            </option>
          </select>
        </div>
        <div class="col-4 d-flex">
          <p><input type="text" class="form-control text-center day rounded-5" /></p>
          <p class="mt-2">days</p>
        </div>
      </div>
      <!-- plan name -->
      <div class="mb-3 ms-5">
        <label for="userId" class="form-label">Plan Name </label>
        <input
          type="text"
          class="form-control rounded-5"
          id="planTitle"
          name="planTitle"
          v-model="userPlan.planTitle"
        />
      </div>
      <div class="d-flex btn-box">
        <button class="planBtn" @click="checkListPlan">Trip Checklist</button>
        <button class="planBtn" @click="recommendPlan">Plan recommend</button>
      </div>
      <div class="plan">
        <draggable v-model="myPlan" :group="{ name: 'places', put: true }" item-key="id">
          <template #item="{ element, index }">
            <div
              v-if="element"
              class="place planItem"
              :class="{ 'bg-completed': element.completed }"
              @click="showPlace(element)"
            >
              <div class="container">
                <div class="row">
                  <div class="col-5 mt-4">
                    <div class="row">
                      <h5
                        class="m-0"
                        :class="{ completed: element.completed }"
                        @click.stop="toggleCompletion(element)"
                      >
                        {{ index + 1 }}. {{ element.place_name }}
                      </h5>
                      <span class="addr col">{{ element.address_name }}</span>
                    </div>
                  </div>
                  <div class="col-5 mt-0">
                    <label for="plan-description"> </label>
                    <textarea
                      type="text"
                      class="plan-description"
                      v-model="element.detail"
                      onInput="this.parentNode.dataset.replicatedValue = this.value"
                    />
                  </div>
                  <div class="col-2 mt-4">
                    <button class="float-end" @click="deletePlanItem(element)">삭제</button>
                  </div>
                </div>
                <div class="distDiv row" v-if="latlngDX.length>index">
                  <p><i class="bi bi-arrow-down"></i> {{ latlngDX[index] }} m</p>
                </div>
              </div>
            </div>
          </template>
        </draggable>
      </div>
    </div>
  </div>
  <planModalItem
    v-show="isModalVisible == true"
    @isModalVisible="recommendPlan"
    @myPlan="getPlan"
  />
  <planCheckListItem
    v-show="isCheckListVisible == true"
    @checklist="getCheckList"
    @goCheckList="checkListPlan"
  />
</template>

<style scoped>
@import "@/assets/css/map.css";
.distDiv{
  margin-left: 10px;
  margin-bottom: 0;
  p{
    margin-left: 20px;
  }
  
}
</style>
