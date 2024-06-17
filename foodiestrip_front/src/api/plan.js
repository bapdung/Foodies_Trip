import { localAxios } from "@/util/http-commons";

const local = localAxios();

async function insertPlan(param, success, fail) {
  console.log("insertPlan 실행");
  await local.post(`/plan/insert`, param).then(success).catch(fail);
}

async function insertPlanDetail(param, success, fail) {
  console.log("insertPlanDetail 실행");
  await local.post(`/plan/insert/detail`, param).then(success).catch(fail);
}

async function selectAllPlanList(param, success, fail) {
  console.log("selectAllPlanList 실행");
  await local.get(`/plan/list`, param).then(success).catch(fail);
}

async function storedPlan(param, success, fail) {
  console.log("storedPlan 실행");
  await local.post(`/plan/store`, param).then(success).catch(fail);
}

async function deleteStoredPlan(param, success, fail) {
  console.log("deleteStoredPlan 실행");
  await local.post(`/plan/deleteStore`, param).then(success).catch(fail);
}

async function updatePlanHit(planNo, success, fail) {
  await local.get(`/plan/hit/${planNo}`).then(success).catch(fail);
}

async function selectAllPlanListByOrder(param, success, fail) {
  await local.get(`/plan/order/${param}`).then(success).catch(fail);
}

async function searchByFiltering(param, success, fail) {
  await local.post(`/plan/search`, param).then(success).catch(fail);
}

//userPlan
async function getUserPlan(userId, success, fail) {
  await local.get(`/plan/user/${userId}`).then(success).catch(fail);
}

//userPlanByPlanNo
async function getUserPlanByPlanNo(planNo, success, fail) {
  await local.get(`/plan/user/planNo/${planNo}`).then(success).catch(fail);
}

//userStoredPlan
async function getUserStoredPlan(param, success, fail) {
  await local.get(`/plan/stored/${param}`).then(success).catch(fail);
}

//userGetRandomPlan
async function getRandomPlan(area, success, fail) {
  await local.post(`/plan/random`, area).then(success).catch(fail);
}

async function deletePlan(planNo, success, fail) {
  await local.get(`plan/delete/${planNo}`).then(success).catch(fail);
}

export {
  insertPlan,
  insertPlanDetail,
  selectAllPlanList,
  storedPlan,
  deleteStoredPlan,
  updatePlanHit,
  selectAllPlanListByOrder,
  getUserPlan,
  getUserStoredPlan,
  getUserPlanByPlanNo,
  getRandomPlan,
  searchByFiltering,
  deletePlan,
};
