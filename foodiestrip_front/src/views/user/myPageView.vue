<script setup>
import userInfoComp from "@/components/myPage/userInfoComp.vue";
</script>

<template>
  <div class="container-fluid">
    <div class="row flex-nowrap">
      <div class="sidebar col-auto col-md-3 col-xl-2 px-sm-2 px-0">
        <div
          class="d-flex flex-column align-items-center align-items-sm-start px-3 pt-2 min-vh-100"
        >
          <a
            href="/"
            class="d-flex align-items-center pb-3 mb-md-0 me-md-auto text-decoration-none"
          >
            <span class="fs-5 d-none d-sm-inline">MyPage</span>
          </a>
          <ul
            class="nav nav-pills flex-column mb-sm-auto mb-0 align-items-center align-items-sm-start"
            id="menu"
          >
            <li class="nav-item">
              <RouterLink :to="{ name: 'user-info' }" href="#" class="nav-link align-middle px-0">
                <i class="fs-4 bi-house"></i> <span class="ms-1 d-none d-sm-inline">내 정보</span>
              </RouterLink>
            </li>
            <li>
              <RouterLink
                :to="{ name: 'user-trip-plan' }"
                ata-bs-toggle="collapse"
                class="nav-link px-0 align-middle"
              >
                <i class="fs-4 bi-speedometer2"></i>
                <span class="ms-1 d-none d-sm-inline">내 여행 일정</span>
              </RouterLink>
            </li>
            <li>
              <RouterLink :to="{ name: 'user-stored-post' }" class="nav-link px-0 align-middle">
                <i class="fs-4 bi-table"></i>
                <span class="ms-1 d-none d-sm-inline">저장된 게시물</span>
              </RouterLink>
            </li>
            <li>
              <RouterLink :to="{ name: 'user-post' }" class="nav-link px-0 align-middle">
                <i class="fs-4 bi-bootstrap"></i>
                <span class="ms-1 d-none d-sm-inline">내가 쓴 게시물</span>
              </RouterLink>
            </li>
          </ul>
          <hr />
        </div>
      </div>
      <!-- 내용 들어가야돼 -->
      <div class="col py-3">
        <RouterView />
        <!-- <userInfoComp /> -->
        <!-- <userPostComp />
        <userStoredPostComp />
        <userTripPlanComp /> -->
      </div>
    </div>
  </div>
</template>

<style scoped>
.sidebar * {
  color: black;
  background-color: rgb(255, 255, 255);
}

.sidebar {
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
}
</style>
