import { localAxios } from "@/util/http-commons";
const local = localAxios();

async function getReviewSummary(param,response,fail){
    await local.post("/api/generate", param).then(response).catch(fail)
}

async function getReviewSummaryfilter(param,response,fail){
    await local.post("/api/generatebyPrompt", param).then(response).catch(fail)
}

export{
    getReviewSummary,
    getReviewSummaryfilter
}