<template>
    <van-cell>
        <template #title>
            <span class="cell-span">{{ `${blog.username}` }}</span>
            <span style="font-size: 12px; color: darkgray; float: right;">{{formatDate(new Date(blog.createTime), 'yyyy-MM-dd hh:mm')}}</span>
        </template>
        <template #icon>
            <van-image :src="blog.avatar" round width="50" height="50" style="margin-left: 10px"/>       
        </template>
    </van-cell>
    <div>
        <div style="margin: 10px;">
            <span style="font-weight: bold;">{{ `${blog.title}` }}</span>
            <br>
            <span class="content">{{ `${blog.content}` }}</span>
        </div>
    </div>
    <div class="custom-button-container">
        <van-button size="mini" is-link @click="show = true">评论x{{ blog.commentsCount }}</van-button>
        <van-button size="mini" :class="{ 'liked-button': blog.isLikedByCurrentUser }" @click="toggleLike(blog)">点赞x{{ blog.likesCount }}</van-button>
        <van-button v-if="isTrue" size="mini" @click="doEdit(blog.id)">编辑</van-button>
        <van-button v-if="isTrue" size="mini" @click="doDeleteBlog(blog.id)">删除</van-button>
        <van-popup
            v-model:show="show"
            position="bottom"
            :style="{ height: '30%' }"
            closeable
            @click-overlay="onClickOverlay"
            @click-close-icon="onClickCloseIcon"    
        >
        <van-form @submit="onSubmit">
            <van-cell-group inset>
                <van-field
                v-model="comment"
                name="评论"
                label="评论"
                placeholder="请友善评论"
                :rules="[{ required: true, message: '评论不能为空' }]"
                />
            </van-cell-group>
            <div style="margin: 16px;">
                <van-button round block type="primary" native-type="submit">
                    发表
                </van-button>
            </div>
        </van-form>
        </van-popup>
    </div>
    <van-divider/>
    <van-cell-group v-for="comment in commentList">
        <van-cell>
            <template #title>
                <span class="cell-span">{{ `${comment.commenterUsername}` }}</span>
                <span style="font-size: 12px; color: darkgray; float: right;">{{formatDate(new Date(comment.createTime), 'yyyy-MM-dd hh:mm')}}</span>
            </template>
            <template #label>
                <div style="width: 270px; margin-left: 10px;">
                    <span style="word-wrap: break-word; text-align: left;">{{ `${comment.commentContent}` }}</span>
                </div>
                <van-button v-if="showButtonComment(comment.commenterId)" size="mini" @click="doDeleteComment(comment.id)" style="float: right;">删除</van-button>
            </template>
            <template #icon>
                <van-image :src="comment.commenterAvatar" round width="50" height="50" style="margin-left: 10px"/>       
            </template>
        </van-cell>
    </van-cell-group>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import myAxios from '../../plugins/myAxios';
import { useRoute, useRouter } from 'vue-router';
import { showFailToast, showSuccessToast } from 'vant';
import { formatDate } from '../../plugins/timeFormat';
import getCurrentUser from '../../services/user';
// import { UserType } from '../../models/user';

const route=useRoute();
const router=useRouter();

const blog=ref({});
const commentList = ref();
const currentUser=ref();
const isTrue=ref(false);

onMounted(async ()=>{
    const response=await myAxios.get('/blog/getById',{
        params:{
            id:route.query.id
        }
    })
    if(response.code===200){
        blog.value=response.data;
        console.log(blog);
    }
    else{
        showFailToast("获取信息失败");
    }

    const res=await getCurrentUser();
    if(res.code===200){
        currentUser.value=res.data;
        if(currentUser.value.id===blog.value.authorId){
            isTrue.value=true;
        }
    }
    else{
        showFailToast("获取用户信息失败");
    } 

    const res2=await myAxios.get('/comment/search',{
        params:{
            blogId:route.query.id
        }
    })
    if(res2.code===200){
        commentList.value=res2.data;
    }
    else{
        showFailToast("获取评论信息失败");
    } 

})

const show = ref(false);
const onClickOverlay = () => {
    show.value=false;
};
const onClickCloseIcon = () => {
    show.value=false;
};

const comment = ref('');
const onSubmit = async () => {
    const response=await myAxios.post('/comment/publish',{
        "blogId":route.query.id,
        "commenterId":currentUser.value.id,
        "commentContent":comment.value
    })
    if(response.code===200 && response.data){
        showSuccessToast('发表成功');
        window.location.reload();
    }
    else{
        showFailToast(response.description);
    }
};

const showButtonComment=(commenterId)=>{
    return currentUser.value.id===Number(commenterId);
}


const doDeleteComment=async (id:Number)=>{
    const response=await myAxios.post('/comment/delete',{
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

const doEdit=(id:Number)=>{
    router.push({
        path:'/blog/edit',
        query:{
            id,
        }
    });
}

const doDeleteBlog=async (id:Number)=>{
    const response=await myAxios.post('/blog/delete',{
        id,
    });
    if(response.code===200){
        //刷新页面
        showSuccessToast('删除成功');
        window.location.reload();
    }
    else{
        showFailToast(response.description);
    }
}

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


.content{
    /* margin-top: 5px; */
    font-size: 13px;
}


.custom-button-container {
  text-align: center;
  justify-content: space-between;
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
