<template>
    <van-tabs v-model:active="active">
        <van-tab title="密码登录">
            <van-form @submit="onSubmitPwd">
                <van-cell-group inset>
                    <van-field
                    v-model="account"
                    name="account"
                    label="账号"
                    placeholder="请输入账号"
                    :rules="[{ required: true, message: '请输入账号' }]"
                    />
                    <van-field 
                    v-model="password"
                    type="password"
                    name="password"
                    label="密码"
                    placeholder="请输入密码"
                    :rules="[{ required: true, message: '请输入密码' }]"
                    />
                </van-cell-group>
                <div style="margin: 16px;">
                    <van-button round block type="primary" native-type="submit">
                    登录
                    </van-button>
                </div>
            </van-form>
        </van-tab>
        <van-tab title="短信登录">
            <van-form @submit="onSubmitSms">
                <van-cell-group inset>
                    <van-field
                    v-model="phone"
                    name="phone"
                    label="手机号"
                    type="tel"
                    placeholder="请输入手机号"
                    :rules="[{ required: true, message: '请输入手机号' },{ pattern: /^1\d{10}$/, message: '不合法的手机号！',}]"
                    />
                    <van-space>
                        <van-field
                        style="width: 220px;"
                        v-model="code"
                        name="code"
                        label="验证码"
                        placeholder="请输入验证码"
                        :rules="[{ required: true, message: '请输入验证码' }]"
                        />
                        <van-button native-type="submit" style="width: 100px; height: 30px; font-size: small;" @click="sendSms" :disabled="disabled">
                            {{ buttonText }}
                        </van-button>
                    </van-space>
                </van-cell-group>
                <div style="margin: 16px;">
                    <van-button round block type="primary" native-type="submit">
                    登录
                    </van-button>
                </div>
            </van-form>
        </van-tab>
    </van-tabs>
    <span style="font-size: 12px; float: right;" @click="goToRegister">
        没有账号？点击注册
    </span>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import myAxios from '../plugins/myAxios';
import { showFailToast, showSuccessToast, showToast } from 'vant';
import { useRouter } from 'vue-router';

const active = ref(0);

const account = ref('');
const password = ref('');
const router=useRouter();
const onSubmitPwd = async () => {
    const response=await myAxios.post('/user/login',{
        account: account.value,
        password: password.value,
    })
    console.log(response);
    if(response.code===200){
        showSuccessToast('登录成功');
        router.replace('/');
    }
    else{
        showFailToast(response.description);
    }
};


const phone = ref('');
const code = ref('');

const disabled=ref(false);
const buttonText=ref('发送验证码');

let countdown=60;

const sendSms=async ()=>{
    const response=await myAxios.get('/user/sendSms',{
        params:{
            phone: phone.value,
        }
    })
    if(response.code===200){
        showSuccessToast('发送成功');
        disabled.value=true;
        alert(response.data);
        const timer = setInterval(() => {
            countdown--;
            if (countdown <= 0) {
                // 倒计时结束，恢复按钮状态
                disabled.value = false;
                countdown = 60; // 重置倒计时秒数
                clearInterval(timer); // 清除定时器
                buttonText.value='发送验证码';
            }
            else{
                buttonText.value=`${countdown} 秒后重新获取`;
            }
        }, 1000);
    }
    else{
        showFailToast(response.description);
    }
}

const onSubmitSms = async () => {
    const response=await myAxios.post('/user/loginSms',{
        phone: phone.value,
        code: code.value,
    })
    console.log(response);
    if(response.code===200){
        showSuccessToast('登录成功');
        router.replace('/');
    }
    else{
        showFailToast(response.description);
    }
};

const goToRegister=()=>{
    router.push({
        path:'/register'
    });
}

</script>

<style scoped>
</style>