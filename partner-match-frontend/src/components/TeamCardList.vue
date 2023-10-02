<template>
    <van-card
        v-for="team in props.teamList"
        :desc="team.description"
        :title="team.name"
        :thumb="team_img_list[team.id%5]"
    >
        <template #tags>
            <van-space wrap>
                <van-tag type="primary">
                    {{ teamStatusEnum[team.status] }}
                </van-tag>
            </van-space>
        </template>
        <template #bottom>
            <div>
                {{ '成员人数：'+team.num+'/'+team.maxNum }}
            </div>
            <div>
                <span>{{ formatDate(new Date(team.createTime), 'yyyy-MM-dd') + ' 至 '}}</span>
                <span v-if="team.expireTime">{{ formatDate(new Date(team.expireTime), 'yyyy-MM-dd') }}</span>
            </div>
        </template>
        <template #footer>
            <van-button v-if="showButton('/team')" size="mini" @click="preJoinTeam(team)">加入队伍</van-button>
            <van-button v-if="showButton('/user/myBuildTeam')" size="mini" @click="doDeleteTeam(team.id)">解散队伍</van-button>
            <van-button v-if="showButton('/user/myBuildTeam')" size="mini" @click="doUpdateTeam(team.id)">更新队伍</van-button>
            <van-button v-if="showButton('/user/myBuildTeam')" size="mini" @click="doChat(1,team.id)">进入群聊</van-button>
            <van-button v-if="showButton('/user/myJoinTeam')" size="mini" @click="doQuitTeam(team.id)">退出队伍</van-button>
            <van-button v-if="showButton('/user/myJoinTeam')" size="mini" @click="doChat(1,team.id)">进入群聊</van-button>
        </template>
    </van-card>
    <van-dialog v-model:show="showPasswordDialog" title="请输入密码" show-cancel-button @confirm="doJoinTeam" @cancel="doJoinCancel">
        <van-field v-model="password" placeholder="请输入密码"/>
    </van-dialog>
    <van-empty v-if="!teamList || teamList.length<1" description="没有相关队伍" />
</template>

<script setup lang="ts">
import { TeamType } from '../models/team';
import {teamStatusEnum} from '../constants/team';
import myAxios from '../plugins/myAxios';
import { showFailToast, showSuccessToast } from 'vant';
import { useRoute, useRouter } from 'vue-router';
import { ref } from 'vue';
import { team_img_list } from '../constants/team_img';
import { formatDate } from '../plugins/timeFormat';


interface teamCardListProps{
    teamList: TeamType[];
}

const props=defineProps<teamCardListProps>();

/**
 * 根据不同的路由展示不同的组件
 */

const route=useRoute();
const showButton=(targetPath:string)=>{
    const currentPath=route.path;
    return currentPath===targetPath;
}

/**
 * 动态弹出框，加入队伍时输入密码
 */
const showPasswordDialog=ref(false);
const password=ref('');
const joinTeamId=ref();

const preJoinTeam=(team)=>{
    joinTeamId.value=team.id;
    if(team.status===0){
        doJoinTeam();
    }
    else{
        showPasswordDialog.value=true;
    }
}

//清楚记录
const doJoinCancel=()=>{
    joinTeamId.value=0;
    password.value='';
}

/**
 * 加入队伍
 * @param id 队伍id
 */
const doJoinTeam=async ()=>{
    if(!joinTeamId.value){
        return;
    }
    const response=await myAxios.post('/team/join',{
        //todo：传入密码参数加入私密队伍
        'teamId':joinTeamId.value,
        'password':password.value
    })
    if(response.code===200){
        showSuccessToast('加入成功');
    }
    else{
        showFailToast(response.description);
    }
    doJoinCancel();
}

/**
 * 解散队伍
 * @param id 队伍id
 */
const doDeleteTeam=async (id:number)=>{
    const response=await myAxios.post('/team/delete',{
        teamId:id
    })
    if(response.code===200){
        showSuccessToast('解散成功');
    }
    else{
        showFailToast(response.description);
    }
}

/**
 * 更新队伍
 * @param id 队伍id
 */
const router=useRouter();
const doUpdateTeam=(id:number)=>{
    router.push({
        path:'/team/update',
        query:{
            id,
        }
    })
}

/**
 * 退出队伍
 * @param id 队伍id
 */
const doQuitTeam=async (id:number)=>{
    const response=await myAxios.post('/team/quit',{
        'teamId':id
    })
    if(response.code===200){
        showSuccessToast('退出成功');
    }
    else{
        showFailToast(response.description);
    }
}

/**
 * 进入群聊
 * @param type 类型
 * @param id 群id
 */
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