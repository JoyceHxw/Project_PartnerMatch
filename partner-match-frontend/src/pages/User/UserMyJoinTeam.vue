<template>
    <van-search v-model="searchText" placeholder="请输入搜索关键词" @search="onSearch"/>
    <team-card-list :team-list="teamList"/>
</template>

<script setup lang="ts">
import TeamCardList from '../../components/TeamCardList.vue';
import { onMounted, ref } from 'vue';
import myAxios from '../../plugins/myAxios';
import { showFailToast } from 'vant';
import getCurrentUser from '../../services/user';

const searchText=ref('');
const teamList=ref([]);

const listTeam =async()=>{
    const userData=await getCurrentUser();
    const response=await myAxios.post("/team/search/myJoin",{
        'searchText': searchText.value,
        'userId':userData.data.id,
        'isRelative':1,
    })
    if(response.code===200){
        teamList.value=response.data;
    }
    else{
        showFailToast("队伍信息加载失败");
    }
}

onMounted(async ()=>{
    listTeam();
})

const onSearch = () => {
    listTeam();
}


</script>