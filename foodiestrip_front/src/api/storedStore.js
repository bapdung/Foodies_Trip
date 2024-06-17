import { localAxios } from "@/util/http-commons";
const local = localAxios();

async function getLikeList(param, success, fail) {
  await local
    .get("/mystore/store", { params: { userId: param } })
    .then(success)
    .then(fail);
}

async function postStoreinsert(param, success, fail) {
  await local.post("/mystore/store", param).then(success).then(fail);
}

async function postStoredelete(param, success, fail) {
  await local.delete("/mystore/store", { data: param }).then(success).then(fail);
}

async function canStore(param, succes, fail) {
  await local.post("/mystore/can", param).then(succes).catch(fail);
}

export { getLikeList, postStoreinsert, postStoredelete, canStore };
