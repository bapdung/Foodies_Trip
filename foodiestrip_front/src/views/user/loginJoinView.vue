<script setup>
import { ref, watch, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useUserStore } from "@/stores/user.js";
import { useMenuStore } from "@/stores/menu";
import { join, idCheck, nameCheck } from "@/api/user.js";
//import { getGugunList } from "@/api/attraction.js";
import { useRouter } from "vue-router";

const router = useRouter();
const userStore = useUserStore();
const menuStore = useMenuStore();

const { isLogin, isLoginError } = storeToRefs(userStore); //ref
const { userLogin, getUserInfo } = userStore; //method
const { changeMenuState } = menuStore;

const isSignUp = ref(false);

//로그인 확인할 객체
const loginUser = ref({
  userId: "",
  userPwd: "",
});

const sharedUserId = ref("");
const checkUserNickname = ref("");
const checkUserPassword = ref("");

const messageId = ref("");
const messageIdClass = ref("");
const isUseNickname = ref(true);

const messageNickname = ref("");
const messageNicknameClass = ref("");
const isUseId = ref(true);

const messagePwd = ref("");
const messagePwdClass = ref("");
const isUsePwd = ref(true);

//회원가입 확인할 객체
const joinUser = ref({
  userId: "",
  userPwd: "",
  userSalt: "",
  userName: "",
  userNickname: "",
  emailId: "",
  emailDomain: "도메인선택",
  sido: "",
  gugun: "",
  isAdmin: false,
  userPreferenceDto: null,
});

const joinUserPreference = ref({
  userId: "",
  mountainPref: false,
  seaPref: false,
  tourPref: false,
  festivalPref: false,
  sportPref: false,
  shoppingPref: false,
  foodPref: false,
});

//id 체크
watch(sharedUserId, (newValue) => {
  const len = newValue.length;
  joinUser.value.userId = newValue;
  joinUserPreference.value.userId = newValue;
  if (len < 4 || len > 16) {
    isUseId.value = false;
    messageIdClass.value = "text-danger";
    messageId.value = "아이디는 4자이상 16자이하입니다.";
  } else {
    goIdCheck();
  }
});

const goIdCheck = async () => {
  console.log("사용가능한 아이디인지 체크하자");
  const data = await idCheck(
    sharedUserId.value,
    (response) => {
      console.log(response);
      const cnt = response.data.rst;
      resultViewText(cnt);
    },
    (error) => {
      console.error(error);
    }
  );
};

const resultViewText = (cnt) => {
  if (cnt == "0") {
    isUseId.value = true;
    messageIdClass.value = "text-success";
    messageId.value = "사용 가능한 아이디입니다.";
  } else {
    isUseId.value = false;
    messageIdClass.value = "text-danger";
    messageId.value = "이미 사용 중인 아이디입니다.";
  }
};

//닉네임 체크
watch(checkUserNickname, (newValue) => {
  joinUser.value.userNickname = newValue;
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
  if (checkUserPassword.value == joinUser.value.userPwd) {
    isUsePwd.value = true;
    messagePwdClass.value = "text-success";
    messagePwd.value = "비밀번호가 일치합니다.";
  } else {
    isUsePwd.value = false;
    messagePwdClass.value = "text-danger";
    messagePwd.value = "비밀번호가 일치하지 않습니다.";
  }
});

//로그인
const handleSignIn = async () => {
  console.log("로그인하자");
  await userLogin(loginUser.value);
  let token = sessionStorage.getItem("accessToken");
  console.log(token);
  console.log("로그인 됐니?" + isLogin.value);
  if (isLogin.value) {
    await getUserInfo(token);
    changeMenuState();
    router.push({ name: "main" });
  }
};

const handleSignUp = async () => {
  console.log("Sign Up:", joinUser.value, joinUserPreference.value);
  if (!isUseId.value) alert("사용가능한 아이디를 입력해주세요.");
  else if (!isUseNickname.value) alert("욕설없는 닉네임만 사용가능합니다.");
  else if (!isUsePwd.value) alert("비밀번호가 일치하지 않습니다.");
  else if (
    !joinUser.value.userId ||
    !joinUser.value.userPwd ||
    !joinUser.value.userName ||
    !joinUser.value.userNickname ||
    !joinUser.value.emailDomain ||
    !joinUser.value.emailId
  ) {
    alert("아이디, 비밀번호, 이메일, 이름, 닉네임 입력은 필수입니다.");
  } else {
    joinUser.value.userPreferenceDto = joinUserPreference.value;
    await join(joinUser.value, (response) => {
      alert("회원가입 완료");
      showSignIn();
    });
  }
};

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
  joinUser.value.sido = sido[0].sidoName;
  if (newVal !== null && newVal !== oldVal) {
    await getGugunList(newVal);
  }
});

const getGugunList = async (sidoCode) => {
  try {
    console.log(sidoCode);
    const response = await local.get(`/attraction/gugun`, { params: { sidoCode: sidoCode } });
    gugunList.value = response.data.gugunList;
    console.log(gugunList.value);
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

onMounted(() => {
  getSidoList();
});

//화면이동
const showSignIn = () => {
  isSignUp.value = false;
};

const showSignUp = () => {
  isSignUp.value = true;
};
</script>

<template>
  <div class="container mt-3" :class="{ 'right-panel-active': isSignUp }">
    <div class="form-container sign-up-container">
      <form @submit.prevent="handleSignUp">
        <h4>회원가입</h4>
        <!-- id -->
        <div class="d-flex">
          <label for="userId" class="form-label">아이디</label>
          <input
            type="text"
            class="form-control"
            id="userId"
            name="userId"
            placeholder="아이디"
            v-model="sharedUserId"
          />
        </div>
        <div>
          <h6 id="idMsg" :class="messageIdClass">{{ messageId }}</h6>
        </div>

        <!-- pwd -->
        <div class="d-flex">
          <label for="userPwd" class="form-label">비밀번호</label>
          <input
            type="password"
            class="form-control"
            id="userPwd"
            name="userPwd"
            v-model="joinUser.userPwd"
            placeholder="비밀번호"
          />
        </div>
        <div>
          <h6 id="pwdMsg"></h6>
        </div>

        <!-- pwd 체크 -->
        <div class="d-flex">
          <label for="userPwdCheck" class="form-label">비밀번호확인</label>
          <input
            type="password"
            class="form-control"
            id="userPwdCheck"
            name="userPwdCheck"
            placeholder="비밀번호확인"
            v-model="checkUserPassword"
          />
        </div>
        <div>
          <h6 id="pwdChkMsg" :class="messagePwdClass">{{ messagePwd }}</h6>
        </div>

        <!-- 이름 -->
        <div class="d-flex">
          <label for="userName" class="form-label">이름</label>
          <input
            type="text"
            class="form-control"
            id="userName"
            name="userName"
            value="이름"
            placeholder="이름"
            v-model="joinUser.userName"
          />
        </div>
        <div>
          <h6 id="nameMsg"></h6>
        </div>

        <!-- 닉네임 -->
        <div class="d-flex">
          <label for="userNickname" class="form-label">닉네임</label>
          <input
            type="text"
            class="form-control"
            id="userNickname"
            name="userNickname"
            v-model="checkUserNickname"
            placeholder="닉네임"
          />
        </div>
        <div>
          <h6 id="nicknameMsg" :class="messageNicknameClass">{{ messageNickname }}</h6>
        </div>

        <!-- 이메일 -->
        <div class="d-flex">
          <label for="emailId" class="form-label">이메일</label>
          <div class="input-group">
            <input
              type="text"
              class="form-control"
              id="emailId"
              name="emailId"
              v-model="joinUser.emailId"
              placeholder="이메일"
            />
            <span class="input-group-text mt-2">@</span>
            <select
              class="form-select mt-2"
              id="emailDomain"
              name="emailDomain"
              aria-label="이메일 도메인 선택"
              v-model="joinUser.emailDomain"
            >
              <option value="" disabled selected>도메인 선택</option>
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
        <div class="mb-2">
          <label for="sido" class="form-label">선호하는 여행 유형</label>
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
              v-model="joinUserPreference.mountainPref"
            />
            <label class="btn" for="btncheck1">산</label>

            <input
              type="checkbox"
              class="btn-check"
              id="btncheck2"
              autocomplete="off"
              v-model="joinUserPreference.seaPref"
            />
            <label class="btn" for="btncheck2">바다</label>

            <input
              type="checkbox"
              class="btn-check"
              id="btncheck3"
              autocomplete="off"
              v-model="joinUserPreference.sportPref"
            />
            <label class="btn" for="btncheck3">레포츠</label>

            <input
              type="checkbox"
              class="btn-check"
              id="btncheck4"
              autocomplete="off"
              v-model="joinUserPreference.tourPref"
            />
            <label class="btn" for="btncheck4">관광명소</label>

            <input
              type="checkbox"
              class="btn-check"
              id="btncheck5"
              autocomplete="off"
              v-model="joinUserPreference.festivalPref"
            />
            <label class="btn" for="btncheck5">축제</label>

            <input
              type="checkbox"
              class="btn-check"
              id="btncheck6"
              autocomplete="off"
              v-model="joinUserPreference.foodPref"
            />
            <label class="btn" for="btncheck6">맛집</label>
            <input
              type="checkbox"
              class="btn-check"
              id="btncheck7"
              autocomplete="off"
              v-model="joinUserPreference.shoppingPref"
            />
            <label class="btn" for="btncheck7">쇼핑</label>
          </div>
        </div>
        <div>
          <h6 id="tripPreference"></h6>
        </div>

        <!-- 지역 -->
        <div class="mb-3 d-flex">
          <label for="sido" class="form-label">지역 </label>
          <div class="input-group">
            <select
              id="search-sido"
              class="col form-select me-2 rounded-5 select"
              v-model="selectSidoCode"
            >
              <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoCode">
                {{ sido.sidoName }}
              </option>
            </select>
            <select
              id="search-gugun"
              class="col form-select me-2 rounded-5 select"
              v-model="joinUser.gugun"
            >
              <option v-for="gugun in gugunList" :key="gugun.gugunCode" :value="gugun.gugunName">
                {{ gugun.gugunName }}
              </option>
            </select>
          </div>
          <div>
            <h6 id="areaMsg"></h6>
          </div>
        </div>
        <!--  -->
        <button type="submit">회원가입하기</button>
      </form>
    </div>
    <div class="form-container sign-in-container">
      <form @submit.prevent="handleSignIn">
        <h1 class="navbar__logo">Foodies Trip</h1>
        <div class="social-container">
          <span>간편로그인</span>
          <br />
          <a href="#" class="social">
            <i class="fab fa-facebook-f"></i>
            <svg
              fill="#000000"
              width="800px"
              height="800px"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M12 2.03998C6.5 2.03998 2 6.52998 2 12.06C2 17.06 5.66 21.21 10.44 21.96V14.96H7.9V12.06H10.44V9.84998C10.44 7.33998 11.93 5.95998 14.22 5.95998C15.31 5.95998 16.45 6.14998 16.45 6.14998V8.61998H15.19C13.95 8.61998 13.56 9.38998 13.56 10.18V12.06H16.34L15.89 14.96H13.56V21.96C15.9164 21.5878 18.0622 20.3855 19.6099 18.57C21.1576 16.7546 22.0054 14.4456 22 12.06C22 6.52998 17.5 2.03998 12 2.03998Z"
              />
            </svg>
          </a>
          <a href="#" class="social">
            <i class="fab fa-google-plus-g"></i>
            <svg
              width="800px"
              height="800px"
              viewBox="0 0 512 512"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                fill="#000000"
                d="M255.5 48C299.345 48 339.897 56.5332 377.156 73.5996C414.415 90.666 443.871 113.873 465.522 143.22C487.174 172.566 498 204.577 498 239.252C498 273.926 487.174 305.982 465.522 335.42C443.871 364.857 414.46 388.109 377.291 405.175C340.122 422.241 299.525 430.775 255.5 430.775C241.607 430.775 227.262 429.781 212.467 427.795C148.233 472.402 114.042 494.977 109.892 495.518C107.907 496.241 106.012 496.15 104.208 495.248C103.486 494.706 102.945 493.983 102.584 493.08C102.223 492.177 102.043 491.365 102.043 490.642V489.559C103.126 482.515 111.335 453.169 126.672 401.518C91.8486 384.181 64.1974 361.2 43.7185 332.575C23.2395 303.951 13 272.843 13 239.252C13 204.577 23.8259 172.566 45.4777 143.22C67.1295 113.873 96.5849 90.666 133.844 73.5996C171.103 56.5332 211.655 48 255.5 48Z"
              ></path>
            </svg>
          </a>
          <a href="#" class="social">
            <i class="fab fa-linkedin-in"></i>
            <svg
              width="800px"
              height="800px"
              viewBox="-32 0 512 512"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                fill="#000000"
                d="M16 32C11.8333 32 8.125 33.5833 4.875 36.75C1.625 39.9167 0 43.6667 0 48V464C0 468.333 1.625 472.083 4.875 475.25C8.125 478.417 11.8333 480 16 480H432C436.167 480 439.875 478.417 443.125 475.25C446.375 472.083 448 468.333 448 464V48C448 43.6667 446.375 39.9167 443.125 36.75C439.875 33.5833 436.167 32 432 32H16ZM100.25 144H186.5L261.5 256V144H347.75V368H261.5L186.5 256V368H100.25V144Z"
              ></path>
            </svg>
          </a>
        </div>
        <input type="id" placeholder="Id" v-model="loginUser.userId" />
        <input
          type="password"
          placeholder="Password"
          @keyup.enter="login"
          v-model="loginUser.userPwd"
        />
        <p v-if="isLoginError === true">아이디 또는 비밀번호를 확인해주세요</p>
        <a href="#">비밀번호를 잊으셨나요?</a>
        <button type="submit">로그인</button>
      </form>
    </div>
    <div class="overlay-container">
      <div class="overlay">
        <div class="overlay-panel overlay-left">
          <h1>푸디즈 어서와!</h1>
          <p>푸디즈 회원이시라면 로그인 버튼을 클릭해 여행 정보를 확인하세요!</p>
          <button class="ghost" @click="showSignIn">로그인하기</button>
        </div>
        <div class="overlay-panel overlay-right">
          <h1>푸디즈에 온 걸 환영해!</h1>
          <p>푸디즈의 회원이 되고 싶다면?</p>
          <button class="ghost" @click="showSignUp">회원가입하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "@/assets/css/loginJoinView.css";
</style>
