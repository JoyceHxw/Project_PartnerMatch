
<template>
    <user-card-list :user-list="userList"/>
</template>

<script setup lang="ts">
import UserCardList from '../components/UserCardList.vue';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import myAxios from '../plugins/myAxios'
import { showFailToast } from 'vant';

//传递的参数
const route=useRoute();
// console.log(route.query);
const tags=route.query; //从 route.query 中提取 tags 属性的值，将其赋值给变量 tags
const userList=ref([]);
const pageNum=ref(1);

window.addEventListener('scroll', () => {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight) {
        // 滚动到页面底部，递增页码并加载更多数据
        pageNum.value++;
        searchUser();
    }
});

const searchUser=async()=>{
    const response =await myAxios.post('/user/searchByTag',{
        'tags':JSON.stringify(tags.tags),
        'pageNum':pageNum.value,
        'pageSize':10,
    })
    if(response.code===200){
        for (let i = 0; i < response.data.length; i++) {
            response.data[i].tags = JSON.parse(response.data[i].tags);
            userList.value.push(response.data[i]);
        }
    }
    else{
        showFailToast("队伍信息加载失败");
    }
}


onMounted(async ()=>{
    searchUser();
})

</script>