import { ref, computed } from "vue";
import { defineStore } from "pinia";
import { useRouter } from "vue-router";
import { jwtDecode } from "jwt-decode";

import {
  userConfirm,
  findById,
  tokenRegeneration,
  logout,
} from "@/api/user.js";
import { httpStatusCode } from "@/util/http-status";

export const useUserStore = defineStore("userStore", () => {
  const router = useRouter();

  const isLogin = ref(false);
  const isLoginError = ref(false);
  const userInfo = ref(null);
  const userPreferenceInfo = ref(null);
  const isValidToken = ref(false);

  //로그인
  const userLogin = async (loginUser) => {
    console.log("로그인 JWT 인증 중");
    try {
      const response = await userConfirm(loginUser);
      console.log("Full response:", response);

      let { accessToken, refreshToken } = response.data;

      if (accessToken && refreshToken) {
        console.log("로그인 성공");
        isLogin.value = true;
        isLoginError.value = false;
        isValidToken.value = true;
        sessionStorage.setItem("accessToken", accessToken);
        sessionStorage.setItem("refreshToken", refreshToken);

        // 로그인 성공 후 유저 정보 가져오기
        await getUserInfo(accessToken);
      } else {
        throw new Error("토큰 정보 없음");
      }
    } catch (error) {
      console.log("로그인 실패!!", error);
      isLogin.value = false;
      isLoginError.value = true;
      isValidToken.value = false;
    }
  };

  //유저 정보 가져오기
  const getUserInfo = async (token) => {
    if (!token) {
      console.error("Token is undefined");
      return;
    }

    try {
      let decodeToken = jwtDecode(token);
      console.log("디코딩한 토큰 : ", decodeToken);

      // 토큰에서 userId를 추출하는 방식이 다를 수 있습니다.
      // 실제 토큰 구조에 맞게 수정해야 합니다.
      const userId = decodeToken.id || decodeToken.userId || decodeToken.sub;

      if (!userId) {
        console.error("User ID not found in token");
        return;
      }

      const response = await findById(userId);

      if (response.status === httpStatusCode.OK) {
        userInfo.value = response.data.userInfo;
        userPreferenceInfo.value = response.data.userInfo.userPreferenceDto;
        isLogin.value = true;
        isValidToken.value = true;
      } else {
        console.log("유저 정보 없음");
        isLogin.value = false;
        isValidToken.value = false;
      }
    } catch (error) {
      console.error("[오류 발생] : ", error);
      isLogin.value = false;
      isValidToken.value = false;

      if (
        error.response &&
        error.response.status === httpStatusCode.UNAUTHORIZED
      ) {
        await tokenRegenerate();
      }
    }
  };

  const tokenRegenerate = async () => {
    try {
      const refreshToken = sessionStorage.getItem("refreshToken");
      if (!refreshToken) {
        throw new Error("Refresh token not found");
      }

      const response = await tokenRegeneration(refreshToken);
      if (response.data.accessToken) {
        sessionStorage.setItem("accessToken", response.data.accessToken);
        isValidToken.value = true;
        await getUserInfo(response.data.accessToken);
      } else {
        throw new Error("Failed to regenerate access token");
      }
    } catch (error) {
      console.error("Token regeneration failed:", error);
      isLogin.value = false;
      userInfo.value = null;
      userPreferenceInfo.value = null;
      isValidToken.value = false;
      sessionStorage.removeItem("accessToken");
      sessionStorage.removeItem("refreshToken");
      router.push({ name: "login" });
    }
  };

  const userLogout = async () => {
    console.log("로그아웃 아이디 : " + userInfo.value.userId);
    await logout(
      userInfo.value.userId,
      (response) => {
        if (response.status === httpStatusCode.OK) {
          isLogin.value = false;
          userInfo.value = null;
          console.log("로그아웃", userInfo);
          isValidToken.value = false;

          sessionStorage.removeItem("accessToken");
          sessionStorage.removeItem("refreshToken");
        } else {
          console.error("유저 정보 없음!!!!");
        }
      },
      (error) => {
        console.log(error);
      }
    );
  };

  const initializeAuth = async () => {
    console.log("초기화 함수");
    const accessToken = sessionStorage.getItem("accessToken");
    if (accessToken) {
      isLogin.value = true;
      isValidToken.value = true;
      await getUserInfo(accessToken);
    }
  };

  return {
    isLogin,
    isLoginError,
    userInfo,
    userPreferenceInfo,
    isValidToken,
    userLogin,
    getUserInfo,
    tokenRegenerate,
    userLogout,
  };
});
