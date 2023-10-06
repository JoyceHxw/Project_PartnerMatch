<template>
    <van-tabs v-model:active="active" @click-tab="onClickTab">
        <van-tab title="私信">
            <van-cell-group v-for="user in userList">
            <van-cell @click="doChat(0,user.user.id)">
                <template #title>
                    <span class="cell-span">{{ `${user.user.username}` }}</span>
                    <span style="font-size: 12px; color: darkgray; float: right;">{{formatDate(new Date(user.latestTime), 'yyyy-MM-dd hh:mm')}}</span>
                </template>
                <template #label>
                    <van-text-ellipsis style="margin-left: 10px;" :content="user.latestText" />
                </template>
                <template #icon>
                <van-image :src="user.user.avatar" round width="50" height="50" style="margin-left: 10px"/>       
                </template>
            </van-cell>
            </van-cell-group>
            <van-empty v-if="!userList || userList.length<1" description="暂无消息" />
        </van-tab>
        <van-tab title="群聊">
            <van-cell-group v-for="team in teamList">
            <van-cell @click="doChat(1,team.team.id)">
                <template #title>
                    <span class="cell-span">{{ `${team.team.name}` }}</span>
                    <span style="font-size: 12px; color: darkgray; float: right;">{{formatDate(new Date(team.latestTime), 'yyyy-MM-dd hh:mm')}}</span>
                </template>
                <template #label>
                    <van-text-ellipsis style="margin-left: 10px;" :content="team.user.username +'：'+ team.latestText" />
                </template>
                <template #icon>
                    <van-image :src="team_img_list[team.team.id%5]" radius="5" width="50" height="50" style="margin-left: 10px;"/>       
                </template>
            </van-cell>
            </van-cell-group>
            <van-empty v-if="!teamList || teamList.length<1" description="暂无消息" />
        </van-tab>
    </van-tabs>
    
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {useRouter} from "vue-router";
import { showFailToast } from "vant";
import { formatDate } from '../plugins/timeFormat';
import { team_img_list } from '../constants/team_img';

const active = ref(0);

const onClickTab = () => {
    localStorage.setItem('activeIdx',active.value);
};

const userList = ref()
const teamList = ref()

onMounted(async () => {
    active.value=Number(localStorage.getItem('activeIdx'))?Number(localStorage.getItem('activeIdx')):0;
    //私信消息列表
    let res = await myAxios.get("/chat/getChatUserList",{
        params:{
            "type":0,
        }
    });
    if (res.code === 200) {
        userList.value = res.data;
    }
    else{
        showFailToast("加载消息列表错误");
    }

    //群聊列表
    let res2 = await myAxios.get("/chat/getChatTeamList",{
        params:{
            "type":1,
        }
    });
    if (res2.code === 200) {
        teamList.value = res2.data;
    }
    else{
        showFailToast("加载消息列表错误");
    }
})

let router = useRouter();
const doChat=(type:Number,id:Number)=>{
    router.push({
        path:'/chat/content',
        query:{
            type,
            id,
        }
    });
}

</script>

<style scoped>
#messagePage {
  margin-top: -30px;
}

.icon_area {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #ededed;
  position: relative;
  margin-left: 10px;
}

.van-cell {
  padding-left: 5px;
}

.icon {
  position: absolute;
  left: 14px;
  top: 14px
}

.van-divider {
  margin: 2px;
}

.cell-span {
  margin-top: 15px;
  margin-left: 10px;
}

:root:root {
  --van-grid-item-text-font-size: 14px
}
</style>
