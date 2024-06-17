import { ref } from "vue";
import { defineStore } from "pinia";

export const useMenuStore = defineStore("menuStore", () => {
  const menuList = ref([
    { name: "로그인", show: true, routeName: "login" },
    { name: "로그아웃", show: false, routeName: "logout" },
    { name: "my여행일정", show: false, routeName: "userPlan" },
    { name: "MyPage", show: false, routeName: "myPage" },
  ]);

  const changeMenuState = () => {
    menuList.value = menuList.value.map((item) => ({ ...item, show: !item.show }));
  };
  return {
    menuList,
    changeMenuState,
  };
});
