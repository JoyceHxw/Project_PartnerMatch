<template>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field
                v-model="blogDetail.title"
                name="title"
                label="标题"
                placeholder="请输入标题"
                :rules="[{ required: true, message: '请输入标题' }]"
            />
            <van-field
                v-model="blogDetail.content"
                rows="10"
                autosize
                label="留言"
                type="textarea"
                placeholder="请输入留言"
                :rules="[{ required: true, message: '请输入内容' }]"
            />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                提交
            </van-button>
        </div>
    </van-form>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import myAxios from '../../plugins/myAxios';
import { showFailToast, showSuccessToast } from 'vant';
import { useRoute, useRouter } from 'vue-router';

const router=useRouter();
const route=useRoute();

const blogDetail=ref({});

//加载博文信息
onMounted(async()=>{
    const response=await myAxios.get('/blog/getById',{
        params:{
            id:route.query.id,
        }
    })
    if(response.code===200 && response.data){
        blogDetail.value=response.data;
    }
    else{
        showFailToast("获取博文信息失败");
    }
})

const onSubmit = async () => {
    const response= await myAxios.post("/blog/update",{
        'id':blogDetail.value.id,
        'authorId':blogDetail.value.authorId,
        'title':blogDetail.value.title,
        'content':blogDetail.value.content,
    });
    if(response.code===200 && response.data){
        showSuccessToast('更新成功');
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