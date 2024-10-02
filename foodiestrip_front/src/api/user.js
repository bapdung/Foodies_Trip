import { localAxios } from '@/util/http-commons';

const local = localAxios();

async function userConfirm(param, success, fail) {
    console.log('userConfirm 실행');
    try {
        const response = await local.post(`/user/login`, param);
        console.log('response값', response);
        return response;
    } catch (error) {
        console.error('Login error:', error);
        fail(error);
    }
}

async function findById(userid) {
    console.log('findById 실행, userId:', userid);
    try {
        local.defaults.headers['Authorization'] = `Bearer ${sessionStorage.getItem('accessToken')}`;
        const response = await local.get(`/user/info/${userid}`);
        console.log('findById response:', response);
        return response;
    } catch (error) {
        console.error('findById error:', error);
        throw error;
    }
}

async function tokenRegeneration(user, success, fail) {
    const refreshToken = sessionStorage.getItem('refreshToken');
    await local
        .post(`/user/refresh`, { refreshToken })
        .then((response) => {
            if (response.data.accessToken) {
                sessionStorage.setItem('accessToken', response.data.accessToken);
                success(response.data);
            } else {
                fail(new Error('Token regeneration failed'));
            }
        })
        .catch(fail);
}

async function logout(userid, success, fail) {
    console.log('logout 실행');
    local.defaults.headers['Authorization'] = `Bearer ${sessionStorage.getItem('accessToken')}`;
    await local
        .get(`/user/logout/${userid}`)
        .then((response) => {
            sessionStorage.removeItem('accessToken');
            sessionStorage.removeItem('refreshToken');
            success(response);
        })
        .catch(fail);
}

async function modify(user, success, fail) {
    console.log('modify 실행');
    await local.post(`/user/modify`, user).then(success).catch(fail);
}

//회원가입
async function join(user, success, fail) {
    console.log('join 실행');
    await local.post(`/user/join`, user).then(success).catch(fail);
}

//아이디체크
async function idCheck(checkId, success, fail) {
    console.log('idCheck 실행');
    await local.get(`/user/idCheck/${checkId}`).then(success).catch(fail);
}

//닉네임체크
async function nameCheck(checkName, success, fail) {
    console.log('nameCheck 실행');
    await local.get(`/user/nameCheck/${checkName}`).then(success).catch(fail);
}

//회원탈퇴
async function userDelete(userId, success, fail) {
    console.log('userDelete 실행');
    await local.get(`/user/delete/${userId}`).then(success).catch(fail);
}
export { userConfirm, findById, tokenRegeneration, logout, join, idCheck, nameCheck, userDelete, modify };
