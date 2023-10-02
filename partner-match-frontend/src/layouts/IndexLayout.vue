<template>
    <van-nav-bar 
        title="伙伴匹配" 
        left-arrow 
        @click-left="onClickLeft"
        @click-right="onClickRight" 
        >
        <template #right>
            <van-icon name="search" size="18" />
        </template>
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
import { useRouter } from 'vue-router';

const router=useRouter();

const onClickLeft = () => {
    router.back()
}
const onClickRight = () => {
    router.push('/search')
}


</script>

<style scoped>

</style>
