<script setup>
import { ref, watch, onMounted } from "vue";
import { getUserPlanByPlanNo, deletePlan } from "@/api/plan";
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
    //console.log(sidoList.value);
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

const items = ref( {
      planChecklistNo: 0,
      userId: '',
      planNo: 0,
      checkNo: 0,
      isCheck: false,
      checkItem: ''
});
    
const planItem = ref({
  planNo: 0,
  planTitle: "",
  userId: "",
  planShare: false,
  writeDate: "", // 날짜 문자열로 초기화
  planBoardHit: 0,
  planBoardJjim: 0,
  sido: "",
  gugun: "",
  planDetailDto: [], // 빈 배열로 초기화
  planChecklistDto: [], // 빈 배열로 초기화
});


onMounted(async () => {
  await getSidoList();
  await getUserPlanByPlanNo(router.currentRoute.value.params.plan, (response) => {
    planItem.value = response.data.plan;
    items.value = response.data.plan.planChecklistDto;
  });
});

// const planDetail = ref(planItem.value.planDetailDto);

console.log(planItem.value);


//체크리스트
const newItem = ref("");
const checkList = ref([]); //최종 insert시 사용할 리스트

const addItem = () => {
  if (newItem.value.trim() !== "") {
    items.value.push({ userId: userInfo.value.userId, checkItem: newItem.value, isCheck: false });
    newItem.value = "";
  }
};

const removeItem = (index) => {
  items.value.splice(index, 1);
};

const toggleItem = (index) => {
  items.value[index].isCheck = !items.value[index].isCheck;
};

const handleInput = (event) => {
  event.target.parentNode.dataset.replicatedValue = event.target.value;
};

const planDelete = async () => {
  const flag = confirm(`${planItem.value.planTitle} 계획을 정말 삭제하시겠습니까?`);
  if (flag) { 
    await deletePlan(planItem.value.planNo, (response) => {
      alert("삭제가 완료되었습니다.");
      router.push({ name: "user-trip-plan" });
    });
  }
}

//저장버튼 클릭시
// const handleCheckListInsert = () => {
//   checkList.value = items.value.map((item, index) => ({
//     checkNo: index + 1,
//     userId: userInfo.value.userId,
//     checkItem: item.checkItem,
//     isCheck: item.isCheck,
//   }));
//   //console.log(checkList.value);
//   emit("checklist", checkList.value);
// };
</script>

<template>
  <div class="d-flex mt-3">
    <div>
      <h6 id="areaMsg"></h6>
    </div>
  </div>
  <!-- plan name -->
  <div class="mb-3 ms-5 container">
    <div class="row">
     <div class="col">
      <label>Plan Name </label>
    <h3>{{ planItem.planTitle }}</h3>
    <div class="d-flex"><label>작성자: </label>
    <h6> {{ planItem.userId }}</h6></div>
     </div>
    </div>
    
    <hr />
  </div>
  <div class="plan">
    <div class="input-group d-flex mb-3">
        <input :value="planItem.sido" readonly class="rounded-5 text-center sido">
        </input>
        <input :value="planItem.gugun" readonly class="rounded-5 text-center gugun">
        </input>
    </div>
    <div class="container">
      <div class="row">
        <div class="col">
          <template v-for="(detail,idx) in planItem.planDetailDto">
        <div class="row card mb-3 text-center">
          <div class="col">
            <h5 class="m-0">{{ idx + 1}}. {{ detail.title }}</h5>
            <span class="addr">{{ detail.address }}</span>
          </div>
          <div class="col">
            <label for="plan-description"> </label>
            <textarea
              type="text"
              class="plan-description form-control mb-2"
              v-model="detail.detail"
              @input="handleInput"
            ></textarea>
          </div>
        </div>
      </template>
        </div>
        <div class="col">
          <!-- 체크리스트 -->
          <div class="checklist">
    <!-- 닫기 버튼 -->

    <br />
    <h3>
      CheckList
    </h3>
    <input
      class="rounded-start-5 border-0.1 add-search"
      v-model="newItem"
      @keyup.enter="addItem"
      placeholder="챙겨야 할 물건을 입력"
    />
    <button @click="addItem" class="add-btn">
      <i class="bi bi-plus-lg"></i>
    </button>
    <button class="ms-2" @click="handleCheckListInsert">저장</button>
    <ul>
      <li v-for="(item, index) in items" :key="index" @click="toggleItem(index)">
        <span :class="{ checked: item.isCheck }">{{ item.checkItem }}</span>
        <button class="remove-btn" @click.stop="removeItem(index)">
          <i class="bi bi-dash-lg"></i>
        </button>
      </li>
    </ul>
  </div>
        </div>
      </div>
      <button class="float-end" @click="planDelete"> 삭제</button>
      <button class="float-end"> 목록</button>
    </div>
  </div>
</template>

<style scoped>


#search-sido,
#search-gugun {
  width: 10px;
  height: 40px;
}

.btn-box {
  justify-content: center;
}

.input-group {
  justify-content: center;
}

.checklist {
  max-width: 400px;
  margin: 0 auto;
  text-align: center;
}

.checked {
  text-decoration: line-through;
  color: gray;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 10px 0;
  cursor: pointer;
}

li span {
  flex: 1;
}

input[type="text"] {
  padding: 5px;
  margin-right: 5px;
  width: calc(100% - 70px);
}

.remove-btn {
  border-radius: 15px !important;
}

.add-btn {
  padding: 3px 10px;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-top-right-radius: 15px;
  border-bottom-right-radius: 15px;
}
.checklist {
  height: 500px;
  background-color: rgba(240, 248, 255, 0.644);
  border-radius: 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}
.exit-btn {
  background-color: rgba(240, 248, 255, 0);
}

.exit-btn:hover {
  background-color: rgba(240, 248, 255, 0);
}

.hclass {
  text-align: center;
  padding-top: 20px;
  padding-bottom: 20px;
}

.plan-description {
  background-color: #fcf0f8; /* 배경색 지정 */
  border-radius: 0.5rem; /* 둥근 가장자리 설정 */
}

.sido, .gugun{
  background-color: #fcf0f8;
  width: 60px;
  border-color: white;
}

</style>
