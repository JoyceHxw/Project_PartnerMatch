<template>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field
            v-model="account"
            name="account"
            label="账号"
            placeholder="请输入账号"
            :rules="[{ required: true, message: '请输入账号' },{pattern: /^.{6,}$/, message: '账号长度不能小于6位',},{pattern:/^[a-zA-Z0-9_.@]+$/,message:'账号不能包含特殊字符'}]"
            />
            <van-field
            v-model="username"
            name="username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请输入账号' }]"
            />
            <van-field 
            v-model="password1"
            type="password"
            name="password1"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请输入密码' },{pattern: /^.{6,}$/, message: '密码长度不能小于6位',}]"
            />
            <van-field 
            v-model="password2"
            type="password"
            name="password"
            label="密码"
            placeholder="请再次输入密码"
            :rules="[{ required: true, message: '请输入密码' },{pattern: /^.{6,}$/, message: '密码长度不能小于6位',}]"
            />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
            注册
            </van-button>
        </div>
    </van-form>
    <span style="font-size: 12px; float: right;" @click="goToLogin">
        已有账号？点击登录
    </span>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import myAxios from '../plugins/myAxios';
import { showFailToast, showSuccessToast} from 'vant';
import { useRouter } from 'vue-router';

const account = ref('');
const username=ref('');
const password1 = ref('');
const password2 = ref('');
const router=useRouter();

const onSubmit = async () => {
    const response=await myAxios.post('/user/register',{
        user:{
            account: account.value,
            username:username.value,
            password:password1.value
        },
        checkPassword: password2.value,
    })
    console.log(response);
    if(response.code===200){
        showSuccessToast('注册成功');
        router.replace('/login');
    }
    else{
        showFailToast(response.description);
    }
};


const goToLogin=()=>{
    router.push({
        path:'/login'
    });
}

</script>

<style scoped>
</style>