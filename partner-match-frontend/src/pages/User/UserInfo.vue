<template>
    <template v-if="user">
        <van-cell title="头像" is-link to="/user/edit" @click="toEdit('avatar','头像',user.avatar)">
            <img :src="user.avatar" :style="{ width: '50px', height: '50px' }"/>
        </van-cell>
        <van-cell title="用户名" is-link to="/user/edit" :value="user.username" @click="toEdit('username','用户名',user.username)"/>
        <van-cell title="标签" is-link to="/tagsEdit" @click="toEditTags('tags','标签',user.tags)">
            <van-space wrap>
                <van-tag v-for="tag in JSON.parse(user.tags)" type="primary">
                    {{ tag }}
                </van-tag>
            </van-space>
        </van-cell>
        <van-cell title="简介" is-link to="/user/edit" :value="user.profile" @click="toEdit('profile','简介',user.profile)"/>
        <van-cell title="账号" :value="user.account" />
        <van-cell title="性别" is-link to="/user/edit" :value="user.gender===0?'女':'男'" @click="toEdit('gender','性别',user.gender)"/>
        <van-cell title="电话" is-link to="/user/edit" :value="user.phone" @click="toEdit('phone','电话',user.phone)"/>
        <van-cell title="邮箱" is-link to="/user/edit" :value="user.email" @click="toEdit('email','邮箱',user.email)" />
        <van-cell title="注册时间" :value="formatDate(new Date(user.createTime), 'yyyy-MM-dd')" />
    </template>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { showFailToast } from 'vant';
import getCurrentUser from '../../services/user';
import { formatDate } from '../../plugins/timeFormat';

const user=ref();

//加载当前用户信息
onMounted(async()=>{
    const response=await getCurrentUser();
    if(response.code===200 && response.data){
        user.value=response.data;
    }
    else{
        showFailToast("获取用户信息失败");
    }
})

//跳转修改信息页面
const router=useRouter();
const toEdit=(editKey:string, editName:string, currentValue:string)=>{
    router.push({
        path:'/user/edit',
        query:{
            editKey,
            editName,
            currentValue,
        }
    })
}

const toEditTags=(editKey:string, editName:string, currentValue:string)=>{
    router.push({
        path:'/tagsEdit',
        query:{
            editKey,
            editName,
            currentValue,
        }
    })
}

</script>

<style scoped>
</style>