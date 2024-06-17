import { localAxios } from "@/util/http-commons";
const local = localAxios();

async function getSidoList(param, success, fail) {
  await local.get("/attraction/view", param).then(success).catch(fail);
}

async function getGugunList(param, success, fail) {
  await local.get(`/attraction/gugun/${param}`).then(success).catch(fail);
}

async function attractionList(param, success, fail) {
  console.log(param);
  await local.post("/attraction/search", param).then(success).catch(fail);
}

async function attractionView(param, success, fail) {
  await local.get(`/attraction/detail/${param}`).then(success).then(fail);
}

export { getSidoList, getGugunList, attractionList, attractionView };
