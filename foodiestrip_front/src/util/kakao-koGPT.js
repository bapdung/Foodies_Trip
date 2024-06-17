import axios from "axios";
// import { httpStatusCode } from "./http-status";

const { VITE_REST_API_KEY, VITE_KAKAO_REST_API_URL } = import.meta.env;

// local vue api axios instance
function kakaoAxios() {
  const instance = axios.create({
    baseURL: VITE_KAKAO_REST_API_URL,
    headers: {
      "Authorization": "KakaoAK " + VITE_REST_API_KEY,

      "Access-Control-Allow-Origin": `http://localhost:5173`,
      'Access-Control-Allow-Credentials':"true",
    },
  });

  // Request 발생 시 적용할 내용.
  instance.defaults.headers.common["Authorization"] = "";
  instance.defaults.headers.post["Content-Type"] = "application/json";
  instance.defaults.headers.put["Content-Type"] = "application/json";

  return instance;
}

export { kakaoAxios };
