<script setup></script>

<!-- 만들다가 말았음 어쩔... ㅠ 윤곽잡히면 수정하겠삼 -->
<template>
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-lg-8 col-md-10 col-sm-12">
        <h2 class="my-3 py-3 text-center">
          <h2><strong>비밀번호 찾기</strong></h2>
        </h2>
      </div>
      <div class="col-lg-8 col-md-10 col-sm-12">
        <form id="form-find-pwd" method="POST" action="">
          <input type="hidden" name="action" value="find-pwd" />
          <div class="mb-3">
            <label for="userId" class="form-label">아이디 : </label>
            <input
              type="text"
              class="form-control"
              id="userId"
              name="userId"
              placeholder="아이디"
            />
          </div>

          <div>
            <h6 class="warning-msg" id="msg" style="color: red"></h6>
          </div>

          <div class="col-auto text-center">
            <button type="button" id="btn-find-pwd-submit" class="mb-3">찾기</button>
          </div>
          <div class="col-lg-8 col-md-10 col-sm-12">
            <div class="card text-center bg-light">
              <span class="pt-3 fs-5"> 찾으신 비밀번호는 <b>${userDto.userPwd}</b> 입니다. </span>
              <div class="card-body">
                <button type="button" id="btn-list">홈으로 이동...</button>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
