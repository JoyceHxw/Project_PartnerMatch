<template>
    <van-button icon="plus" type="primary" id="build-button" @click="doPublishBlog"></van-button>
    <van-search v-model="searchText" placeholder="请输入搜索关键词" @search="onSearch"/>
    <blog-card-list :blog-list="blogList"/>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import BlogCardList from '../../components/BlogCardList.vue';
import { onMounted, ref } from 'vue';
import myAxios from '../../plugins/myAxios';
import { showFailToast } from 'vant';
import getCurrentUser from '../../services/user';

const router=useRouter();
const doPublishBlog=()=>{
    router.push({
        path:"/blog/publish"
    })
}

const searchText=ref('');
const blogList=ref([]);

const listBlog =async(val="")=>{
    const userData=await getCurrentUser();
    const response=await myAxios.post("/blog/search",{
        'searchText': val,
        'authorId': userData.data.id,
    })
    if(response.code===200){
        blogList.value=response.data;
    }
    else{
        showFailToast("队伍信息加载失败");
    }
}


onMounted(async ()=>{
    listBlog();
})

const onSearch = (val: string) => {
    listBlog(val);
}


</script>