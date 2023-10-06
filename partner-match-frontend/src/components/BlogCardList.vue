<template>
    <van-divider/>
    <van-cell-group v-for="blog in props.blogList" class="custom-cell-group">
        <van-cell @click="goToBlog(blog.id)" class="custom-cell">
            <template #title>
                <span class="cell-span">{{ `${blog.username}` }}</span>
                <span style="font-size: 12px; color: darkgray; float: right;">{{formatDate(new Date(blog.createTime), 'yyyy-MM-dd hh:mm')}}</span>
            </template>
            <template #label>
                <span class="cell-span" style="font-weight: bold;">{{ `${blog.title}` }}</span>
                <van-text-ellipsis
                    class="cell-span"
                    rows="3"
                    :content="blog.content"
                />
            </template>
            <template #icon>
                <van-image :src="blog.avatar" round width="50" height="50" style="margin-left: 10px"/>       
            </template>
        </van-cell>
        <div class="custom-button-container">
            <van-button size="mini" @click="goToBlog(blog.id)">评论x{{ blog.commentsCount }}</van-button>
            <van-button size="mini" :class="{ 'liked-button': blog.isLikedByCurrentUser }" @click="toggleLike(blog)">点赞x{{ blog.likesCount }}</van-button>
            <van-button v-if="showButton('/user/myBlog')" size="mini" @click="doEdit(blog.id)">编辑</van-button>
            <van-button v-if="showButton('/user/myBlog')" size="mini" @click="doDelete(blog.id)">删除</van-button>
        </div>
    </van-cell-group>
    <van-empty v-if="!blogList || blogList.length<1" description="没有相关博文" />
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { BlogType } from '../models/blog';
import myAxios from '../plugins/myAxios';
import { showFailToast, showSuccessToast } from 'vant';
import { formatDate } from '../plugins/timeFormat';
import { onMounted, ref } from 'vue';
import getCurrentUser from '../services/user';

const router=useRouter();
const route=useRoute();

const props=defineProps<UserCardListProps>();

const showButton=(targetPath:string)=>{
    const currentPath=route.path;
    return currentPath===targetPath;
}

const goToBlog=(id:Number)=>{
    router.push({
        path:'/blog/detail',
        query:{
            id,
        }
    });
}

interface UserCardListProps{
    blogList: BlogType[];
}


const doEdit=(id:Number)=>{
    router.push({
        path:'/blog/edit',
        query:{
            id,
        }
    });
}

const doDelete=async (id:Number)=>{
    const response=await myAxios.post('/blog/delete',{
        id,
    });
    if(response.code===200){
        //刷新页面
        showSuccessToast('解散成功');
        window.location.reload();
    }
    else{
        showFailToast(response.description);
    }
}

const currentUser=ref();
onMounted(async()=>{
    const response=await getCurrentUser();
    if(response.code===200 && response.data){
        currentUser.value=response.data;
    }
    else{
        showFailToast("获取用户信息失败");
    }
})

/**
 * 点击切换点赞状态
 * @param blog 传递博文对象，直接修改属性
 */
const toggleLike=async (blog)=>{
    const isLiked=blog.isLikedByCurrentUser;
    //点赞
    if(!isLiked){
        const response=await myAxios.post('/like/add',{
            'blogId':blog.id,
            'userId':currentUser.value.id,
        })
        if(response.code===200){
            blog.likesCount+=1;
            showSuccessToast("点赞成功");
        }
        else{
            showFailToast("点赞失败");
        }
    }
    //取消点赞
    else{
        const response=await myAxios.post('/like/cancel',{
            'blogId':blog.id,
            'userId':currentUser.value.id,
        })
        if(response.code===200){
            //后台也会更新，但是这样可以不用刷新
            blog.likesCount-=1;
            showSuccessToast("取消点赞成功");
        }
        else{
            showFailToast("取消点赞失败");
        }
    }
    blog.isLikedByCurrentUser=!isLiked;
}


</script>

<style scoped>
.custom-cell-group {
  margin-bottom: 10px; /* 调整每个 <van-cell> 之间的间隔 */
}

.custom-cell {
  background-color: #f7f8fa; /* 添加灰色背景 */
  /* 其他自定义样式 */
}

.custom-button-container {
  text-align: center;
  justify-content: space-between;
  background-color: #f7f8fa;
  /* 按钮容器的样式 */
}


.custom-button-container .liked-button {
  background-color: #1989fa; /* 改变背景颜色 */
  color: white; /* 改变文字颜色 */
  /* 添加其他样式以改变按钮样式 */
}

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
