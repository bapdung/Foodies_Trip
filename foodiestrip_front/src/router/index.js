import { createRouter, createWebHistory } from "vue-router";
import { storeToRefs } from "pinia";
import { useUserStore } from "@/stores/user";

const onlyAuthUser = async (to, from, next) => {
  const userStore = useUserStore();
  const { userInfo, isValidToken } = storeToRefs(userStore);
  const { getUserInfo } = userStore;
  let token = sessionStorage.getItem("acessToken");

  if (userInfo.value != null && token) {
    await getUserInfo(token);
  }
  if (!isValidToken.value || userInfo.value === null) {
    next({ name: "login" });
  } else {
    next();
  }
};
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "main",
      component: () => import("@/views/mainView.vue"),
    },
    {
      path: "/attraction/:contentId",
      name: "attraction",
      component: () => import("@/views/attraction/attractionDetailView.vue"),
      props: true,
    },
    {
      path: "/planBoard",
      name: "planBoard",
      component: () => import("@/views/planBoard/planBoardView.vue"),
    },
    {
      path: "/planBoard/view/:plan",
      name: "plan-board-view",
      props: (route) => ({ plan: JSON.parse(route.params.plan) }),
      component: () => import("@/components/planBoard/items/planBoardDetailItem.vue"),
    },
    {
      path: "/user/:plan",
      name: "plan-user-view",
      props: (route) => ({ plan: JSON.parse(route.params.plan) }),
      component: () => import("@/components/myPage/items/userPlanDetailItem.vue"),
    },
    {
      path: "/planBoard/userPlan/:plan",
      name: "plan-board-copy",
      beforeEnter: onlyAuthUser,
      props: (route) => ({ plan: JSON.parse(route.params.plan) }),
      component: () => import("@/views/planBoard/userPlanView.vue"),
    },
    {
      path: "/foodBoard",
      name: "foodBoard",
      component: () => import("@/views/foodBoard/foodBoardView.vue"),
    },
    {
      path: "/foodStore/:contentId",
      name: "foodStore",
      component: () => import("@/views/foodBoard/foodStoreDetailView.vue"),
      props: true,
    },
    {
      path: "/foodStoreReview/:foodBoardNo",
      name: "food-store-review",
      component: () => import("@/views/foodBoard/foodBoardReviewView.vue"),
      props: true,
    },
    {
      path: "/write",
      name: "food-board-write",
      beforeEnter: onlyAuthUser,
      component: () => import("@/views/foodBoard/writingReviews.vue"),
    },
    {
      path: "/modify/:review",
      name: "food-board-modify",
      component: () => import("@/views/foodBoard/modifyReviews.vue"),
    },
    {
      path: "/tripRoulette",
      name: "tripRoulette",
      component: () => import("@/views/randomEvent/tripRouletteView.vue"),
    },
    {
      path: "/myPage",
      name: "myPage",
      component: () => import("@/views/user/myPageView.vue"),
      redirect: "/mypage/info",
      children: [
        {
          path: "info",
          name: "user-info",
          component: () => import("@/components/myPage/userInfoComp.vue"),
        },
        {
          path: "post",
          name: "user-post",
          component: () => import("@/components/myPage/userPostComp.vue"),
        },
        {
          path: "stored-post",
          name: "user-stored-post",
          component: () => import("@/components/myPage/userStoredPostComp.vue"),
        },
        {
          path: "trip-plan",
          name: "user-trip-plan",
          component: () => import("@/components/myPage/userTripPlanComp.vue"),
        },
      ],
    },
    {
      path: "/userPlan",
      name: "userPlan",
      component: () => import("@/views/planBoard/userPlanView.vue"),
    },
    {
      // path: "/faq",
      // name: "faq",
      // //component추가해야함
    },
    {
      path: "/login",
      name: "login",
      component: () => import("@/views/user/loginJoinView.vue"),
    },
    {
      path: "/",
      name: "logout",
      component: () => "@/views/mainView.vue",
    },
    {
      path: "/search/:keyword",
      name: "search",
      component: () => import("@/views/keywardSearchResult.vue"),
    },
    {
      path: "/food-recommend",
      name: "food-recommend",
      component: () => import("@/views/randomEvent/foodRecommendView.vue"),
    },

    {
      // 이름 바꿔야됨ㅋ
      path: "/test:regionCode",
      name: "test",
      component: () => import("@/views/axiosTest.vue"),
    },
  ],
});

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  const isLoginRequired = to.matched.some(record => record.meta.requiresAuth);

  if (!userStore.isLogin) {
    await userStore.initializeAuth();
  }

  if (isLoginRequired && !userStore.isLogin) {
    next({ name: 'login' });
  } else {
    next();
  }
});

export default router;
