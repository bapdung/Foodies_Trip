import { ref, computed } from "vue";
import { defineStore } from "pinia";
import { useRouter } from "vue-router";
import { jwtDecode } from "jwt-decode";

import { userConfirm, findById, tokenRegeneration, logout } from "@/api/user.js";
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
    await userConfirm(
      loginUser,
      (response) => {
        if (response.status === httpStatusCode.CREATE) {
          console.log("로그인 성공");
          let { data } = response;
          let accessToken = data["access-token"];
          let refreshToken = data["refresh-token"];
          isLogin.value = true;
          isLoginError.value = false;
          isValidToken.value = true;
          sessionStorage.setItem("accessToken", accessToken);
          sessionStorage.setItem("refreshToken", refreshToken);
        }
      },
      (error) => {
        console.log("로그인 실패!!");
        isLogin.value = false;
        isLoginError.value = true;
        isValidToken.value = false;
        console.log(error);
      }
    );
  };

  //유저 정보 가져오기
  const getUserInfo = async (token) => {
    let decodeToken = jwtDecode(token);
    console.log("디코딩한 토큰 : ", decodeToken);
    await findById(
      decodeToken.userId,
      (response) => {
        if (response.status === httpStatusCode.OK) {
          userInfo.value = response.data.userInfo;
          userPreferenceInfo.value = response.data.userInfo.userPreferenceDto;
        } else {
          console.log("유저 정보 없음");
        }
      },
      async (error) => {
        console.error(
          "[토큰 만료되어 사용 불가능] : ",
          error.response.status,
          error.response.statusText
        );
        isValidToken.value = false;

        await tokenRegenerate();
      }
    );
  };

  const tokenRegenerate = async () => {
    await tokenRegeneration(
      JSON.stringify(userInfo.value),
      (response) => {
        if (response.status === httpStatusCode.CREATE) {
          let accessToken = response.data["access-token"];
          sessionStorage.setItem("accessToken", accessToken);
          isValidToken.value = true;
        }
      },
      async (error) => {
        if (error.response.status === httpStatusCode.UNAUTHORIZED) {
          await logout(
            userInfo.value.userId,
            (response) => {
              if (response.status === httpStatusCode.OK) {
                console.log("리프레시 토큰 제거 성공");
              } else {
                console.log("리프레시 토큰 제거 실패");
              }
              alert("RefreshToken 기간 만료!!! 다시 로그인해 주세요.");
              isLogin.value = false;
              userInfo.value = null;
              userPreferenceInfo.value = null;
              isValidToken.value = false;
              router.push({ name: "login" });
            },
            (error) => {
              console.error(error);
              isLogin.value = false;
              userInfo.value = null;
            }
          );
        }
      }
    );
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
