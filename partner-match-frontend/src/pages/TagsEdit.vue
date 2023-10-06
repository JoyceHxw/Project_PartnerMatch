<template>
    <van-divider content-position="left">已选标签</van-divider>
    <van-space wrap>
        <van-tag v-for="tag in activeIds" :show="show" closeable size="medium" type="primary" @close="close(tag)">
            {{ tag }}
        </van-tag>
    </van-space>
    <van-divider content-position="left">可选标签</van-divider>
    <van-tree-select
        v-model:active-id="activeIds"
        v-model:main-active-index="activeIndex"
        :items="tagList"
    />
    <van-button square type="primary" @click="doInitialTags">完成</van-button>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const route=useRoute();
const editUser = ref<{
    editKey: string;
    editName: string;
    currentValue: string;
}>({
    editKey: route.query.editKey as string,
    editName: route.query.editName as string,
    currentValue: route.query.currentValue as string,
});


const originTagList=[
    {
    text: '学历',
    children: [
        { text: '专科', id: '专科' },
        { text: '本科', id: '本科' },
        { text: '研究生', id: '研究生'},
    ],
    },
    {
    text: '编程技术',
    children: [
        { text: 'C++', id: 'C++' },
        { text: 'Java', id: 'Java' },
        { text: 'Python', id: 'Python'},
        { text: 'Linux', id: 'Linux'},
    ],
    },
    {
    text: '状态',
    children: [
        { text: '学习', id: '学习' },
        { text: '考研', id: '考研' },
        { text: '秋招', id: '秋招' },
        { text: '考公', id: '考公' },
    ],
    },
    {
    text: '兴趣',
    children: [
        { text: '玩游戏', id: '玩游戏' },
        { text: '看书', id: '看书' },
        { text: '看电影', id: '看电影' },
        { text: '乒乓球', id: '乒乓球' },
        { text: '篮球', id: '篮球' },
        { text: '桌游', id: '桌游' },
    ],
    },
];

let tagList=ref(originTagList);

const activeIds = ref(JSON.parse(editUser.value.currentValue) || []);
const activeIndex = ref(0);


/**
 * 搜索标签
 */
const show = ref(true);
const close = (tag:number) => {
    activeIds.value =activeIds.value.filter(item => {
        return item!==tag;
    })
};


/**
 * 更新标签
 */
import { useRoute, useRouter } from 'vue-router';
import getCurrentUser from '../services/user';
import myAxios from '../plugins/myAxios';
import { showFailToast, showSuccessToast } from 'vant';

const router=useRouter();
const doInitialTags=async () => {
    const userData=await getCurrentUser();
    const response=await myAxios.post('/user/update',{
        'id':userData.data.id,
        [editUser.value.editKey]: JSON.stringify(activeIds.value),
    })
    console.log(response);
    if(response.code===200 && response.data>0){
        showSuccessToast('修改成功');
        router.replace('/user/info');
    }
    else{
        showFailToast('修改出错');
    }
};


</script>