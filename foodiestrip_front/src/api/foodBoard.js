import { localAxios } from "@/util/http-commons";
const local = localAxios();

async function selectFoodBoardList(param, success, fail) {
  console.log("in param", param);
  await local
    .get("/foodboard/listall", { params: { sortOrder: param } })
    .then(success)
    .then(fail);
}

async function totalReviewCnt(param, success, fail) {
  await local.get("/foodboard/count").then(success).then(fail);
}

async function foodBoardView(param, success, fail) {
  await local.get(`/foodboard/detail/${param}`).then(success).then(fail);
}

async function getmyfoodBoard(userId, success, fail) {
  await local
    .get("/foodboard/mylist", { params: { userId: userId } })
    .then(success)
    .then(fail);
}

async function getStoreReviews(param, success, fail) {
  await local
    .get("/foodboard/reviews", { params: { contentId: param } })
    .then(success)
    .then(fail);
}

async function updateHit(param, success, fail) {
  await local
    .get("/foodboard/hit", { params: { foodBoardNo: param } })
    .then(success)
    .then(fail);
}

async function deleteReview(param, success, fail) {
  await local
    .delete(`/foodboard/board`, { params: { foodBoardNo: param } })
    .then(success)
    .catch(fail);
}

async function searchByFiltering(param, success, fail) {
  await local.post(`/foodboard/search`, param).then(success).catch(fail);
}

async function randomStore(param, success, fail) {
  await local.post(`/foodboard/random`, param).then(success).catch(fail);
}

//가게 평균 정보 GET
async function getAverage(param, success, fail) {
  await local
    .get(`/foodboard/average`, { params: { contentId: param } })
    .then(success)
    .catch(fail);
}

//인증된 리뷰 리스트 정보 GET
async function getAuthorizedReview(param, success, fail) {
  await local
    .get(`/foodboard/authorize`, { params: { contentId: param } })
    .then(success)
    .catch(fail);
}

async function modifyReview(param, success, fail) {
  await local.post(`/foodboard/modify`, param).then(success).catch(fail);
}

export {
  selectFoodBoardList,
  totalReviewCnt,
  foodBoardView,
  getmyfoodBoard,
  getStoreReviews,
  updateHit,
  deleteReview,
  searchByFiltering,
  randomStore,
  getAverage,
  getAuthorizedReview,
  modifyReview,
};
