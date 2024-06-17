<template>
  <div>
    <input v-model="prompt" placeholder="여기에 텍스트 입력">
    <button @click="generateText">텍스트 생성</button>
    <div v-if="generatedText">
      생성된 텍스트: {{ generatedText }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const prompt = ref('');
const generatedText = ref(null);

const generateText = () => {
  const requestBody = {
    prompt: `${prompt.value}\n한줄 요약:`,
  };

  axios.post('/api/generate', requestBody)
    .then(response => {
      generatedText.value = response.data.generations.map(gen => gen.text).join('\n');
    })
    .catch(error => {
      console.error("API 요청 중 오류 발생: ", error);
    });
};
</script>
