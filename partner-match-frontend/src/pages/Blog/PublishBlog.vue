<template>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field
                v-model="blogData.title"
                name="title"
                label="标题"
                placeholder="请输入标题"
                :rules="[{ required: true, message: '请输入标题' }]"
            />
            <van-field
                v-model="blogData.content"
                rows="10"
                autosize
                label="内容"
                type="textarea"
                placeholder="请输入内容"
                :rules="[{ required: true, message: '请输入内容' }]"
            />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                发布
            </van-button>
        </div>
    </van-form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import myAxios from '../../plugins/myAxios';
import { showFailToast, showSuccessToast, showToast } from 'vant';
import { useRouter } from 'vue-router';

const router=useRouter();
const initFormData={
    "title":"",
    "content":""
}

//用户填写的表单数据
const blogData=ref({...initFormData});

const onSubmit = async () => {
    const postData={
        ...blogData.value
    }
    console.log(postData);
    const response= await myAxios.post("/blog/publish",postData);
    if(response.code===200 && response.data){
        showSuccessToast('发布成功');
        router.push({
            path: '/blog',
            replace: true, //不保留原始页面的历史记录
        })
    }
    else{
        showFailToast(response.description);
    }
};

</script>

<style scoped>
</style>