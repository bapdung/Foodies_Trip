<script setup>
import { ref } from "vue";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";

const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);

const emit = defineEmits(["checklist", "goCheckList"]);

const items = ref([]);
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

//저장버튼 클릭시
const handleCheckListInsert = () => {
  checkList.value = items.value.map((item, index) => ({
    checkNo: index + 1,
    userId: userInfo.value.userId,
    checkItem: item.checkItem,
    isCheck: item.isCheck,
  }));
  //console.log(checkList.value);
  emit("checklist", checkList.value);
  emit("goCheckList", "goCheckList");
};
</script>

<template>
  <div class="checklist">
    <!-- 닫기 버튼 -->
    <button class="float-end exit-btn" @click="handleCheckListInsert">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        fill="#FB3640"
        class="bi bi-x-square-fill"
        viewBox="0 0 16 16"
      >
        <path
          d="M2 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2zm3.354 4.646L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 1 1 .708-.708"
        />
      </svg>
    </button>
    <br />
    <h3 id="plan-checklist">Trip Checklist</h3>
    <input
      class="rounded-start-5 border-0.1 add-search"
      v-model="newItem"
      @keyup.enter="addItem"
      placeholder="챙겨야 할 물건을 입력"
    />
    <button @click="addItem" class="add-btn">
      <i class="bi bi-plus-lg"></i>
    </button>
    <ul>
      <li v-for="(item, index) in items" :key="index" @click="toggleItem(index)">
        <span :class="{ checked: item.isCheck }">{{ item.checkItem }}</span>
        <button class="remove-btn" @click.stop="removeItem(index)">
          <i class="bi bi-dash-lg"></i>
        </button>
      </li>
    </ul>
  </div>
</template>

<style>
@import url("https://fonts.googleapis.com/css2?family=Madimi+One&display=swap");
#plan-checklist {
  font-family: "Madimi One", sans-serif;
  font-weight: 400;
  font-style: normal;
  color: #fb3640;
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
  border-top-left-radius: 0 !important;
  border-bottom-left-radius: 0 !important;
  border-top-right-radius: 15px !important;
  border-bottom-right-radius: 15px !important;
}
.checklist {
  position: absolute;
  top: 200px;
  left: 25%;
  right: 33%;
  height: 500px;
  background-color: rgba(240, 248, 255, 0.844);
  z-index: 10000;
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
</style>
