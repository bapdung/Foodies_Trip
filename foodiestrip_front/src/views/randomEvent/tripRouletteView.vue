<script setup>
import { RouterLink, RouterView } from "vue-router";
import { ref, onMounted } from "vue";

const canvas = ref(null);
const result = ref(false);
const country = ref(" ");

const product = ["제주도", "강릉", "부산", "여수", "대전", "대구", "서울", "속초", "포항"];

const colors = [
  "#f08d8d",
  "#f4a896",
  "#f9c89e",
  "#fde9b2",
  "#c0eb75",
  "#a7d993",
  "#a6dcef",
  "#b8b7f0",
  "#d2a5e3",
  "#e598e8",
  "#f8bad3",
];

const newMake = () => {
  const ctx = canvas.value.getContext(`2d`);
  const [cw, ch] = [canvas.value.width / 2, canvas.value.height / 2];
  const arc = Math.PI / (product.length / 2);

  for (let i = 0; i < product.length; i++) {
    ctx.beginPath();
    ctx.fillStyle = colors[i % (colors.length - 1)];
    ctx.moveTo(cw, ch);
    ctx.arc(cw, ch, cw, arc * (i - 1), arc * i);
    ctx.fill();
    ctx.closePath();
  }

  ctx.fillStyle = "#fff";
  ctx.font = "19px Pretendard";
  ctx.textAlign = "center";

  for (let i = 0; i < product.length; i++) {
    const angle = arc * i + arc / 2;

    ctx.save();

    ctx.translate(cw + Math.cos(angle) * (cw - 50), ch + Math.sin(angle) * (ch - 50));

    ctx.rotate(angle + Math.PI / 2);

    product[i].split(" ").forEach((text, j) => {
      ctx.fillText(text, 0, 30 * j);
    });

    ctx.restore();
  }
};

const rotate = () => {
  result.value = false;
  const $c = canvas.value;
  $c.style.transform = `initial`;
  $c.style.transition = `initial`;

  setTimeout(() => {
    const ctx = $c.getContext(`2d`);
    const ran = Math.floor(Math.random() * product.length);

    const arc = 360 / product.length;
    const rotate = ran * arc + 3600 + arc * 3 - arc / 4;

    $c.style.transform = `rotate(-${rotate}deg)`;
    $c.style.transition = `2s`;

    setTimeout(() => {
      result.value = true;
      country.value = product[ran];
    }, 2000);
  }, 1);
};

onMounted(() => {
  newMake();
});
</script>

<template>
  <div class="container card">
    <h1>어디로 여행을 떠나볼까요?</h1>
    <div id="roullette" class="mt-5 mb-4">
      <canvas ref="canvas" width="380" height="380"></canvas>
      <button class="roulletteButton" @click="rotate">룰렛 돌리기</button>
    </div>
    <div id="roullette-result" v-show="result">
      <h1>이번 여행지는 {{ country }} 어떠신가요?</h1>
      <!-- navbar 검색버튼 클릭했을 때 푸디's 지도조회 결과로 가면될듯 -->
      <!-- {{country}} 키워드 검색 결과로! -->
      <RouterLink :to="{ name: 'search', params: { keyword: country } }"
        >{{ country }}에 있는 관광지를 보러갈까요?</RouterLink
      >
    </div>
  </div>
  <routerView />
</template>

<style scoped>
/* @import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css"); */
@import "@/assets/css/main.css";

body {
  background: #f7f8fc;
}

canvas {
  transition: 2s;
}

.roulletteButton {
  /* background: #e61c5d; */
  /* color: #f7f8fc; */
  margin-top: 1rem;
  padding: 0.8rem 1.8rem;
  border: none;
  font-size: 1.5rem;
  font-weight: bold;
  border-radius: 5px;
  transition: 0.2s;
  border-radius: 30px;
  cursor: pointer;
}

/* .roulletteButton:hover {
  background: #ffcbdb;
  color: black;
} */
button:active {
  background: #444;
  color: #f9f9f9;
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  /* height: 100vh; */
  flex-direction: column;
  padding: 50px;
}

#roullette {
  width: 380px;
  display: flex;
  align-items: center;
  flex-direction: column;
  position: relative;
}

#roullette::before {
  content: "";
  position: absolute;
  width: 10px;
  height: 50px;
  border-radius: 5px;
  background: #fb3640;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 22;
}

#roullette-result {
  align-items: center;
  text-align: center;
  /* width: 18rem; */
}
</style>
