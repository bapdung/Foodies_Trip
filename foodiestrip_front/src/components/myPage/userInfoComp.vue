<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { useMenuStore } from "@/stores/menu";
import { nameCheck, modify, userDelete, logout } from "@/api/user.js";
import { storeToRefs } from "pinia";

const router = useRouter();
const userStore = useUserStore();
const menuStore = useMenuStore();
const { userInfo } = storeToRefs(userStore);
const { userLogout } = userStore;
const { changeMenuState } = menuStore;
const userModifyInfo = ref({ ...userInfo.value });
// console.log(userModifyInfo);

userModifyInfo.value.userPwd = "";

const userPreferenceInfo = ref(userModifyInfo.value.userPreferenceDto);

const checkUserNickname = ref(userModifyInfo.value.userNickname);
const checkUserPassword = ref(userModifyInfo.value.userPwd);

const isUseNickname = ref(true);

const messageNickname = ref("");
const messageNicknameClass = ref("");

const messagePwd = ref("");
const messagePwdClass = ref("");
const isUsePwd = ref(true);

//닉네임 체크
watch(checkUserNickname, (newValue) => {
  userModifyInfo.value.userNickname = newValue;
  goNicknameCheck(newValue);
});

const goNicknameCheck = async (nickname) => {
  await nameCheck(
    nickname,
    (response) => {
      console.log(response);
      messageNickname.value = response.data.nameMsg;
      if (messageNickname.value != null) {
        isUseNickname.value = false;
        messageNicknameClass.value = "text-danger";
      } else {
        messageNickname.value = "사용가능한 닉네임입니다.";
        isUseNickname.value = true;
        messageNicknameClass.value = "text-success";
      }
      console.log(messageNickname.value);
    },
    (error) => {
      console.error(error);
    }
  );
};

//비밀번호 동일 여부 체크
watch(checkUserPassword, (newValue) => {
  if (checkUserPassword.value == userModifyInfo.value.userPwd) {
    isUsePwd.value = true;
    messagePwdClass.value = "text-success";
    messagePwd.value = "비밀번호가 일치합니다.";
  } else {
    isUsePwd.value = false;
    messagePwdClass.value = "text-danger";
    messagePwd.value = "비밀번호가 일치하지 않습니다.";
  }
});

const handleUserModify = async () => {
  console.log(userModifyInfo.value);
  userModifyInfo.value.userPreferenceDto = userPreferenceInfo.value;
  await modify(
    userModifyInfo.value,
    (response) => {
      userInfo.value = userModifyInfo.value;
      alert("회원정보 수정이 완료되었습니다.");
    },
    (error) => {
      console.error(error);
    }
  );
};

//회원탈퇴
const handleUserDelete = async () => {
  const flag = confirm("정말로 Foodies Trip을 탈퇴하시겠습니까?");
  if (flag) {
    await userLogout();
    await userDelete(
      userModifyInfo.value.userId,
      (response) => {
        alert("회원 탈퇴가 완료되었습니다.");
        changeMenuState();
        router.push({ name: "main" });
      },
      (error) => {
        console.error(error);
      }
    );
  }
};

//시도, 구군리스트
import { getSidoList, getGugunList } from "@/api/attraction";

const selectedSidoName = ref("");
const selectedgugunName = ref("");
const sidoList = ref([]);
const gugunList = ref([]);
const selectedSidoCode = ref(1);
const selectedGugnCode = ref(0);

const getSidoCode = (sidoName) => {
  console.log("getSidoCode: ", sidoName);
  const sido = sidoList.value.filter((sido) => sido.sidoName == sidoName);
  selectedSidoCode.value = sido[0].sidoCode;
};

watch(selectedSidoCode, async (newVal, oldVal) => {
  if (newVal !== null && newVal !== oldVal) {
    await getGugunList(newVal, (response) => {
      gugunList.value = response.data.gugunList;
      selectedGugnCode.value = 1;
    });
  }
});

watch(selectedSidoName, async (newVal, oldVal) => {
  console.log(newVal);
  userModifyInfo.value.sido = selectedSidoName.value;
  if (newVal !== null && newVal !== oldVal) {
    getSidoCode(newVal);
  }
});

onMounted(() => {
  getSidoList("", (response) => {
    sidoList.value = response.data.sidoList;
    gugunList.value = response.data.gugunList;
    getSidoCode(selectedSidoName.value);
  });
  selectedSidoName.value = userModifyInfo.value.sido;
});
</script>

<template>
  <div>
    <h2><strong>회원 정보</strong></h2>
    <hr />
    <div class="container">
      <div class="row justify-content-center">
        <!-- <div class="col-lg-8 col-md-10 col-sm-12">
          <h2 class="my-3 py-3 shadow-sm bg-light text-center">
            <mark style="color: white; background-color: #e9967a; border-radius: 10px"
              >회원정보 수정</mark
            >
          </h2>
        </div> -->
        <div class="col-lg-8 col-md-10 col-sm-12">
          <form id="form-modify" method="POST" action="">
            <!-- id -->
            <div class="mb-3">
              <label for="userId" class="form-label read-only">아이디 : </label>
              <input
                type="text"
                class="form-control"
                id="userId"
                name="userId"
                v-model="userModifyInfo.userId"
                readonly
                disabled
              />
            </div>
            <div>
              <h6 id="idMsg"></h6>
            </div>

            <!-- pwd -->
            <div class="mb-3">
              <label for="userPwd" class="form-label">비밀번호 : </label>
              <input
                type="password"
                class="form-control"
                id="userPwd"
                name="userPwd"
                placeholder="비밀번호..."
                v-model="userModifyInfo.userPwd"
              />
            </div>
            <div>
              <h6 id="pwdMsg"></h6>
            </div>

            <!-- pwd 체크 -->
            <div class="mb-3">
              <label for="userPwdCheck" class="form-label">비밀번호확인 : </label>
              <input
                type="password"
                class="form-control"
                id="userPwdCheck"
                name="userPwdCheck"
                placeholder="비밀번호확인..."
                v-model="checkUserPassword"
              />
            </div>
            <div>
              <h6 id="pwdChkMsg" :class="messagePwdClass">{{ messagePwd }}</h6>
            </div>

            <!-- 이름 -->
            <div class="mb-3">
              <label for="userName" class="form-label">이름 : </label>
              <input
                type="text"
                class="form-control"
                id="userName"
                name="userName"
                v-model="userModifyInfo.userName"
              />
            </div>
            <div>
              <h6 id="nameMsg"></h6>
            </div>

            <!-- 닉네임 -->
            <div class="mb-3">
              <label for="userNickname" class="form-label">닉네임 : </label>
              <input
                type="text"
                class="form-control"
                id="userNickname"
                name="userNickname"
                v-model="checkUserNickname"
              />
            </div>
            <div>
              <h6 id="nicknameMsg" :class="messageNicknameClass">{{ messageNickname }}</h6>
            </div>

            <!-- 이메일 -->
            <div class="mb-3">
              <label for="emailId" class="form-label">이메일 : </label>
              <div class="input-group">
                <input
                  type="email"
                  class="form-control"
                  id="emailId"
                  name="emailId"
                  v-model="userModifyInfo.emailId"
                />
                <span class="input-group-text">@</span>
                <select
                  class="form-select"
                  id="emailDomain"
                  name="emailDomain"
                  aria-label="이메일 도메인 선택"
                  v-model="userModifyInfo.emailDomain"
                >
                  <option selected>
                    {{ userModifyInfo.emailDomain }}
                  </option>
                  <option value="hanmail.com">hanmail.com</option>
                  <option value="gmail.com">gmail.com</option>
                  <option value="naver.com">naver.com</option>
                  <option value="kakao.com">kakao.com</option>
                </select>
              </div>
            </div>
            <div>
              <h6 id="emailMsg"></h6>
            </div>

            <!-- 선호하는 여행지 -->
            <div class="mb-3">
              <label for="sido" class="form-label">선호하는 여행 유형 : </label>
              <br />
              <div
                class="btn-group trip-preference-btn"
                role="group"
                aria-label="Basic checkbox toggle button group"
              >
                <input
                  type="checkbox"
                  class="btn-check"
                  id="btncheck1"
                  autocomplete="off"
                  v-model="userPreferenceInfo.mountainPref"
                />
                <label class="btn" for="btncheck1">산</label>

                <input
                  type="checkbox"
                  class="btn-check"
                  id="btncheck2"
                  autocomplete="off"
                  v-model="userPreferenceInfo.seaPref"
                />
                <label class="btn" for="btncheck2">바다</label>

                <input
                  type="checkbox"
                  class="btn-check"
                  id="btncheck3"
                  autocomplete="off"
                  v-model="userPreferenceInfo.sportPref"
                />
                <label class="btn" for="btncheck3">레포츠</label>

                <input
                  type="checkbox"
                  class="btn-check"
                  id="btncheck4"
                  autocomplete="off"
                  v-model="userPreferenceInfo.tourPref"
                />
                <label class="btn" for="btncheck4">관광</label>

                <input
                  type="checkbox"
                  class="btn-check"
                  id="btncheck5"
                  autocomplete="off"
                  v-model="userPreferenceInfo.festivalPref"
                />
                <label class="btn" for="btncheck5">축제</label>

                <input
                  type="checkbox"
                  class="btn-check"
                  id="btncheck6"
                  autocomplete="off"
                  v-model="userPreferenceInfo.foodPref"
                />
                <label class="btn" for="btncheck6">맛집</label>

                <input
                  type="checkbox"
                  class="btn-check"
                  id="btncheck7"
                  autocomplete="off"
                  v-model="userPreferenceInfo.shoppingPref"
                />
                <label class="btn" for="btncheck7">쇼핑</label>
              </div>
            </div>
            <div>
              <h6 id="tripPreference"></h6>
            </div>

            <!-- 지역 -->
            <div class="mb-3">
              <label for="sido" class="form-label">지역 : </label>
              <div class="input-group">
                <select
                  id="search-sido"
                  class="col form-select me-2 rounded-5 select"
                  v-model="selectedSidoName"
                >
                  <option :value="userModifyInfo.sido" selected>{{ userModifyInfo.sido }}</option>
                  <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoName">
                    {{ sido.sidoName }}
                  </option>
                </select>
                <select
                  id="search-gugun"
                  class="col form-select me-2 rounded-5 select"
                  v-model="userModifyInfo.gugun"
                >
                  <!-- <option value="0" selected>{{ userModifyInfo.gugun }}</option> -->
                  <option
                    v-for="gugun in gugunList"
                    :key="gugun.gugunCode"
                    :value="gugun.gugunName"
                  >
                    {{ gugun.gugunName }}
                  </option>
                </select>
              </div>
              <div>
                <h6 id="areaMsg"></h6>
              </div>
            </div>
            <div class="col-auto text-center">
              <button
                type="button"
                @click="handleUserModify"
                id="btn-modify-submit"
                class="btn btn-outline-primary mb-3"
              >
                수정하기
              </button>
            </div>
            <div class="col-auto text-end">
              <a @click="handleUserDelete" class="btn btn-danger">회원탈퇴</a>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.trip-preference-btn *,
btn {
  background-color: #fb3640;
  color: white;
  justify-content: center;
}

.trip-preference-btn {
  display: flex;
  justify-content: center;
  align-items: center;
}

.trip-preference-btn .btn-check:checked + .btn,
.trip-preference-btn {
  background-color: #aa0009;
  outline-color: #aa0009;
  color: white;
}
</style>
