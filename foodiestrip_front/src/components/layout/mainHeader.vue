<script setup>
import { ref } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { useMenuStore } from "@/stores/menu";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";

const menuStore = useMenuStore();
const userStore = useUserStore();
const { menuList } = storeToRefs(menuStore);
const { userInfo } = storeToRefs(userStore);
const { changeMenuState } = menuStore;
const { userLogout } = userStore;

const navigateToSearch = () => {
  const trimmedWord = word.value.trim();
  word.value = " ";
  if (trimmedWord !== "") {
    router.push({ name: "search", params: { keyword: trimmedWord } });
  }
};

if(userInfo.value!=null){
  console.log("in mainheader: ",userInfo.value)
}

const router = useRouter();

//로그아웃
const logout = async () => {
  await userLogout();
  await changeMenuState();
  router.push({ name: "main" });
};

const word = ref(" ");
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
            <RouterLink :to="{ name: 'search', params: { keyword: word } }"
              ><img class="icon" src="/images/icons/search.png"
            /></RouterLink>
          </div>
        </div>

        <div class="ms-auto">
          <ul v-if="menuList[0].show" class="beforeLogin">
            <li class="login-item">
              <RouterLink :to="{ name: 'login' }" class="login-item" >Login</RouterLink>
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
            <template v-for="(menu, index) in menuList" :key="menu.routeName">
              <template v-if="menu.show && index >= 2">
                <template v-if="index == 3">
                  <li class="nav-item">
                    <router-link :to="{ name: menu.routeName }" class="nav-link">{{
                      menu.name
                    }}</router-link>
                  </li></template
                >
                <template v-else>
                  <li class="nav-item">
                    <router-link :to="{ name: menu.routeName }" class="nav-link">{{
                      menu.name
                    }}</router-link>
                  </li>
                </template>
              </template>
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
