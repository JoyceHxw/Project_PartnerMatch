<template>
    <!-- <van-button type="primary" class="build-button" @click="doBuildTeam">创建队伍</van-button> -->
    <van-button icon="plus" type="primary" id="build-button" @click="doBuildTeam"></van-button>
    <van-search v-model="searchText" placeholder="请输入搜索关键词" @search="onSearch"/>
    <team-card-list :team-list="teamList"/>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import TeamCardList from '../components/TeamCardList.vue';
import { onMounted, ref } from 'vue';
import myAxios from '../plugins/myAxios';
import { showFailToast } from 'vant';

const router=useRouter();
const doBuildTeam=()=>{
    router.push({
        path:"/team/build"
    })
}

const searchText=ref('');
const teamList=ref([]);
const pageNum=ref(1);

const listTeam =async(val="")=>{
    const response=await myAxios.post("/team/search",{
        'searchText': val,
        'pageNum':pageNum.value,
        'pageSize':10,
    })
    if(response.code===200){
        for (let i = 0; i < response.data.length; i++) {
            teamList.value.push(response.data[i])
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
        listTeam();
    }
});


onMounted(async ()=>{
    listTeam();
})

const onSearch = (val: string) => {
    listTeam(val);
}


</script>