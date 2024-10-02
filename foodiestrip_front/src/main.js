import { createApp } from 'vue';
import { createPinia } from 'pinia';

import App from './App.vue';
import { useKakao } from 'vue3-kakao-maps/@utils';

import router from './router';

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap';

const app = createApp(App);

app.use(createPinia());
app.use(router);
useKakao('58009bacc033e9770620c790caacb9c7', ['clusterer', 'services', 'drawing']);

app.mount('#app');
