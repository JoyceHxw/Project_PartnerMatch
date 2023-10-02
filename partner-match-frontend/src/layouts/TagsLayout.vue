<template>
    <van-nav-bar 
        title="选择标签" 
        left-arrow 
        @click-left="onClickLeft"
        >
    </van-nav-bar>

    <div id="content" style="padding-bottom: 80px;">
        <router-view/>
    </div>

</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router=useRouter();


const onClickLeft = () => {
    router.push('/user/info')
}

</script>

<style scoped>

</style>
