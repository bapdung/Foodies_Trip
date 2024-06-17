<script setup>
import { defineProps, ref, watch, onMounted, TrackOpTypes } from "vue";
import { storedPlan, deleteStoredPlan, updatePlanHit } from "@/api/plan";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";

const userStore = useUserStore();
const router = useRouter();
const { userInfo } = storeToRefs(userStore);

/*axios */
//시도리스트, 구군리스트 받아오기
import { localAxios } from "@/util/http-commons";
const local = localAxios();

const sidoList = ref([]);
// const selectedSidoCode = ref(0);
const selectSidoCode = ref(0);
const gugunList = ref([]);

const getSidoList = async () => {
  try {
    const response = await local.get("/attraction/view");
    sidoList.value = response.data.sidoList;
    gugunList.value = response.data.gugunList;
    console.log(sidoList.value);
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

watch(selectSidoCode, async (newVal, oldVal) => {
  console.log(newVal);
  const sido = sidoList.value.filter((sido) => sido.sidoCode == newVal);
  userPlan.value.sido = sido[0].sidoName;
  if (newVal !== null && newVal !== oldVal) {
    await getGugunList(newVal);
  }
});

const getGugunList = async (sidoCode) => {
  try {
    console.log(sidoCode);
    const response = await local.get(`/attraction/gugun`, { params: { sidoCode: sidoCode } });
    gugunList.value = response.data.gugunList;
    // console.log(gugunList.value);
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

onMounted(() => {
  getSidoList();
});

const props = defineProps({
  plan: {
    type: Object,
    required: true,
  },
});

const planDetail = ref(props.plan.planDetailDto);
const storedColor = ref("gray"); //수정

//플랜 게시글 저장(=스크랩)
const handleStoredPlan = async () => {
  const storeInfo = ref({});
    storeInfo.value.planNo = props.plan.planNo;
    storeInfo.value.userId = userInfo.value.userId;
  if (storedColor.value == "gray") {
    await storedPlan(storeInfo.value, (response) => {
      storedColor.value = response.data.color;
    });
  } else {
    await deleteStoredPlan(storeInfo.value, (response) => {
      storedColor.value = response.data.color;
    })
  }
}
let imgSrc = ref("");
let svg = `<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='${storedColor.value}' class='bi bi-star-fill' viewBox='0 0 16 16'><path d='M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z'/></svg>`;
  let encodedSVG = encodeURIComponent(svg);
  // src 업데이트
  imgSrc.value = `data:image/svg+xml,${encodedSVG}`;


watch(storedColor, (newColor) => {
  // 이미지를 base64로 인코딩
  svg = `<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='${newColor}' class='bi bi-star-fill' viewBox='0 0 16 16'><path d='M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z'/></svg>`;
  encodedSVG = encodeURIComponent(svg);
  // src 업데이트
  imgSrc.value = `data:image/svg+xml,${encodedSVG}`;
});

const updateHit = async() => {
  await updatePlanHit(props.plan.planNo);
  router.push({ name: "planBoard" });
}

</script>

<template>
  <div class="d-flex">
    <div class="input-group d-flex">
        <input :value="plan.sido" readonly class="rounded-5 text-center">
        </input>
        <input :value="plan.gugun" readonly class="rounded-5 text-center">
        </input>
    </div>
    <div>
      <h6 id="areaMsg"></h6>
    </div>
  </div>
  <!-- plan name -->
  <div class="mb-3 ms-5 container">
    <div class="row">
     <div class="col">
      <label>Plan Name </label>
    <h3>{{ plan.planTitle }}</h3>
    <div class="d-flex"><label>작성자: </label>
    <h6> {{ plan.userId }}</h6></div>
    <div class="d-flex">조회수: <h6 class="mt-1">{{ plan.planBoardHit + 1 }}</h6></div>
     </div>
     <div class="col">
      <div class="container">
        <div class="row float-end"> <button class="jjim btn float-end" @click="handleStoredPlan">
          <img
    :src="imgSrc"
    alt="Star Icon"
    @click="changeColor"
  />
         
          <br />
          저장
        </button></div>
        <br/>
        <br/>
      <div class="row float-end">
        <RouterLink :to="{ name: 'plan-board-copy', params: { plan:JSON.stringify(plan) } }"> 이 일정으로 플랜세우기</RouterLink>
      </div>
      </div>
     </div>
    </div>
    
    <hr />
  </div>
  <div class="plan">
    <div class="container">
      <div class="row mt-3">
        <div class="col">
          <template v-for="detail in planDetail">
            <div class="row card mb-3 text-center">
              <h5 class="m-0">{{ detail.title }}</h5>
              <span class="addr col">{{ detail.address }}</span>
            </div>
          </template>
        </div>
      </div>
      <button class="float-end" @click="updateHit"> 목록</button>
    </div>
  </div>
</template>

<style scoped>
/* @import "@/assets/css/map.css"; */

.bi {
  transform: scaleX(-1);
}

#search-sido,
#search-gugun {
  width: 10px;
  height: 40px;
}

.btn-box {
  justify-content: center;
}

.input-group{
  justify-content: center;
}
</style>
