<template>
    <van-card
        v-for="user in props.userList"
        :desc="user.profile"
        :title="user.username"
        :thumb="user.avatar"
    >
        <template #tags>
            <van-space wrap>
                <van-tag v-for="tag in user.tags" type="primary">
                    {{ tag }}
                </van-tag>
            </van-space>
        </template>
        <template #footer>
            <van-button size="mini" @click="doChat(0,user.id)">发消息</van-button>
        </template>
    </van-card>
    <van-empty v-if="!userList || userList.length<1" description="没有相关用户" />
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { UserType } from '../models/user';

interface UserCardListProps{
    userList: UserType[];
}


const router=useRouter();
const doChat=(type:Number,id:Number)=>{
    router.push({
        path:'/chat/content',
        query:{
            type,
            id,
        }
    });
}

const props=defineProps<UserCardListProps>();
</script>