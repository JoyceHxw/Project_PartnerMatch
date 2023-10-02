<template>
    <van-nav-bar 
        :title="title" 
        left-arrow 
        @click-left="onClickLeft"
        >
    </van-nav-bar>

    <div id="content" style="padding-bottom: 80px;">
        <router-view/>
    </div>

    <van-tabbar route >
        <van-tabbar-item replace to="/" icon="home-o" name="index">主页</van-tabbar-item>
        <van-tabbar-item replace to="/team" icon="friends-o" name="team">队伍</van-tabbar-item>
        <van-tabbar-item replace to="/blog" icon="fire-o" name="team">广场</van-tabbar-item>
        <van-tabbar-item replace to="/chat" icon="chat-o" name="chat">聊天</van-tabbar-item>
        <van-tabbar-item replace to="/user" icon="setting-o" name="person">个人</van-tabbar-item>
    </van-tabbar>

</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import routes from '../config/route';
import { onMounted, ref } from 'vue';

const router=useRouter();
const route=useRoute();

const title=ref();
onMounted(async ()=>{
    const routeFind=routes.find((routeTemp)=>{
        return route.path==routeTemp.path;
    })
    title.value=routeFind?.title;
})

router.beforeEach((to,from,next)=>{
    const toPath=to.path;
    const route=routes.find((route)=>{
        return toPath==route.path;
    })
    title.value=route?.title;
    next();
})


const onClickLeft = () => {
    router.back()
}

</script>

<style scoped>

</style>
