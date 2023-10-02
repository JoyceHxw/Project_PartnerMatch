import { createApp } from 'vue'
import './style.css'
import 'vant/lib/index.css';
import App from './App.vue'
import { Button, Card, Cell, CellGroup, ContactCard, Field, Form, NavBar, PickerGroup, Space, Sticky, Tabbar, TabbarItem, Toast, TreeSelect } from 'vant';
import * as VueRouter from 'vue-router';
import routes from './config/route';
import ChatLayoutVue from './layouts/ChatLayout.vue';
import BasicLayoutVue from './layouts/BasicLayout.vue';
import LoginLayoutVue from './layouts/LoginLayout.vue';
import RegisterLayoutVue from './layouts/RegisterLayout.vue';
import TagsLayoutVue from './layouts/TagsLayout.vue';
import IndexLayoutVue from './layouts/IndexLayout.vue';

import 'vant/es/toast/style';

const app = createApp(App);

app.component("default-layout", BasicLayoutVue);
app.component("chat-layout", ChatLayoutVue);
app.component("login-layout",LoginLayoutVue);
app.component("register-layout",RegisterLayoutVue);
app.component("tags-layout",TagsLayoutVue);
app.component("index-layout",IndexLayoutVue);

app.use(Button);
app.use(NavBar);
app.use(Tabbar);
app.use(TabbarItem);
app.use(TreeSelect);
app.use(Cell);
app.use(CellGroup);
app.use(Card);
app.use(Space);
app.use(Form);
app.use(Field);
app.use(Toast);
app.use(PickerGroup);
app.use(Sticky);
app.use(ContactCard);

// 3. 创建路由实例并传递 `routes` 配置
const router = VueRouter.createRouter({
  // 4. 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
  // history: VueRouter.createWebHashHistory(),
  history: VueRouter.createWebHistory(),
  routes, // `routes: routes` 的缩写
})


app.use(router)

app.mount('#app')

// 现在，应用已经启动了！
