<template>
    <form action="/">
        <van-search
            v-model="searchText"
            show-action
            placeholder="请输入搜索关键词过滤标签"
            @search="onSearch"
            @cancel="onCancel"
        />
    </form>
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
    <van-button square type="primary" @click="doSearchUser">搜索</van-button>
</template>

<script setup lang="ts">
import { ref } from 'vue';

/**
 * 搜索框
 */
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

const searchText = ref('');
const onSearch = () => {
    tagList.value=originTagList.map(parentTag => {
        if (parentTag.children && typeof parentTag.children[Symbol.iterator] === 'function') {
            // console.log(parentTag.children);
            //... 是 JavaScript 中的扩展运算符.它用于创建一个已有数组或对象的浅拷贝，也可以用于将数组、对象、或其他可迭代的数据结构展开成一个新的数组或对象
            // const tempChildren=[...parentTag.children!];
            const tempChildren = Array.from(parentTag.children);
            const tempParentTag={...parentTag};
            // console.log(tempParentTag);
            tempParentTag.children=tempChildren.filter(item=>item.text.includes(searchText.value));
            // console.log(tempParentTag);
            return tempParentTag;
        } else {
            // 处理 parentTag.children 不存在或不是可迭代对象的情况
            // 可以根据需要进行处理
            return parentTag; // 或者返回一个默认值
        }
    });
};
const onCancel = () => {
    searchText.value="";
    tagList.value=originTagList;
};


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
 * 跳转搜索页，根据标签搜索用户
 */
import { useRouter } from 'vue-router';
const router=useRouter();
const doSearchUser=()=>{
    router.push({
        path:'/search/result',
        query:{
            tags:activeIds.value,
        }
    })
}


const activeIds = ref([]);
const activeIndex = ref(0);




</script>