<script setup>
import { ref, computed, watch } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { useMenuStore } from "@/stores/menu";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";

const menuStore = useMenuStore();
const userStore = useUserStore();
const { menuList } = storeToRefs(menuStore);
const { isLogin, isValidToken } = storeToRefs(userStore);
const { changeMenuState } = menuStore;
const { userLogout, initializeAuth } = userStore;

const router = useRouter();
const word = ref("");

const visibleMenus = computed(() => {
  return menuList.value.filter(menu => isLogin.value);
});

watch(isLogin, (newValue) => {
  changeMenuState();
});

const navigateToSearch = () => {
  const trimmedWord = word.value.trim();
  if (trimmedWord !== "") {
    router.push({ name: "search", params: { keyword: trimmedWord } });
    word.value = "";
  }
};

const logout = async () => {
  console.log('Logging out');
  await userLogout();
  router.push({ name: "main" });
};

// 컴포넌트 마운트 시 인증 상태 초기화
initializeAuth();

</script>

<template>
  <header>
    <nav class="navbar">
      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <div class="navbar__logo">
          <RouterLink :to="{ name: 'main' }"><i class="fas fa-blog"></i>Foodies Trip</RouterLink>
          <div class="searchbox">
            <input
              id="search"
              type="text"
              v-model="word"
              @keyup.enter="navigateToSearch"
              placeholder="도시나 여행지를 검색해보세요"
            />
            <button @click="navigateToSearch" class="searchBtn">
              <img class="icon" src="/images/icons/search.png" alt="Search" />
            </button>
          </div>
        </div>

        <div class="ms-auto">
          <ul v-if="!isLogin" class="beforeLogin">
            <li class="login-item">
              <RouterLink :to="{ name: 'login' }" class="login-item">Login</RouterLink>
            </li>
          </ul>

          <ul v-else>
            <button class="btn nav-item logout" @click="logout">Logout</button>
          </ul>
        </div>
      </div>
    </nav>
    <div class="container-fluid">
      <nav class="navbar navbar-expand-lg">
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="true"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <RouterLink class="nav-link" :to="{ name: 'planBoard' }">
                푸디's 여행일정
              </RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" :to="{ name: 'foodBoard' }"> 푸디's 맛슐랭 </RouterLink>
            </li>
            <template v-if="isLogin">
              <li v-for="menu in visibleMenus" :key="menu.routeName" class="nav-item">
                <RouterLink :to="{ name: menu.routeName }" class="nav-link">
                  {{ menu.name }}
                </RouterLink>
              </li>
            </template>
            <li class="nav-item">
              <a class="nav-link" href="#"> 지역별 추천 여행지 </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#"> FAQ </a>
            </li>
          </ul>
          <div class="navbar-icons"><i class="fab fa-google"></i> <i class="fab fa-slack"></i></div>
        </div>
      </nav>
    </div>
  </header>
</template>

<style scoped>
@import "@/assets/css/navbar.css";
</style>