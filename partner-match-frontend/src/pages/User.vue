<template>
    <van-cell title="当前用户" :value="user?.username" />
    <van-cell title="我的信息" is-link to="/user/info" />
    <van-cell title="我创建的队伍" is-link to="/user/myBuildTeam" />
    <van-cell title="我加入的队伍" is-link to="/user/myJoinTeam" />
    <van-cell title="我发布的博文" is-link to="/user/myBlog" />
    <div style="margin: 16px;">
        <van-button round block type="primary" @click="logout">
            退出
        </van-button>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import getCurrentUser from '../services/user';
import { showFailToast, showSuccessToast } from 'vant';
import { useRouter } from 'vue-router';
import myAxios from '../plugins/myAxios';

const user=ref();

onMounted(async()=>{
    const response=await getCurrentUser();
    if(response.code===200 && response.data){
        user.value=response.data;
    }
    else{
        showFailToast("获取用户信息失败");
    }
})

const router=useRouter();

const logout = async () => {
    const response=await myAxios.post('/user/logout')
    if(response.code===200){
        showSuccessToast('退出成功');
        router.push('/login')
    }
    else{
        showFailToast(response.description);
    }
};
</script>

<style scoped>
</style>