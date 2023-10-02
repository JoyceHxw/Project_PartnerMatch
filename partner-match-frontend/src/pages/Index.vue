<template>
    <user-card-list :user-list="userList"/>
</template>

<script setup lang="ts">
import UserCardList from '../components/UserCardList.vue';
import { onMounted, ref } from 'vue';
import myAxios from '../plugins/myAxios'
import { showFailToast } from 'vant';

//传递的参数
const userList=ref([]);
const pageNum=ref(1);

const getUserList=async()=>{
    const response =await myAxios.post('/user/recommend',{
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

window.addEventListener('scroll', () => {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight) {
        // 滚动到页面底部，递增页码并加载更多数据
        pageNum.value++;
        getUserList();
    }
});

onMounted(async ()=>{
    getUserList();
    // const userListResult =await myAxios.post('/user/recommend',{
    //     'pageNum':pageNum.value,
    //     'pageSize':10
    // })
    // .then(function (response: any) {
    //     // 处理成功情况
    //     // console.log(response.data);
    //     return response.data;
    // })
    // .catch(function (error: any) {
    //     // 处理错误情况
    //     console.log(error);
    // })
    // if(userListResult){
    //     userListResult.forEach((user: { tags: string; }) =>{
    //         try {
    //             user.tags = JSON.parse(user.tags);
    //         } catch (error) {
    //             user.tags = "";
    //         }

    //     })
    //     userList.value=userListResult;
    // }
})

</script>