<template>
    <van-sticky>
        <van-nav-bar 
            :title="name" 
            left-arrow 
            @click-left="onClickLeft"
            >
        </van-nav-bar>
    </van-sticky>
    <div class="chat-container">
        <!-- ref 属性用于在 Vue.js 中获取对 DOM 元素的引用，以便在组件中访问和操作该元素。 -->
        <!-- v-html 指令允许将动态生成的 HTML 内容插入到元素中 -->
        <div class="content" ref="chatRoom" v-html="content"></div>
        <van-cell-group inset style="position: fixed;bottom: 0;width: 100%">
            <van-field
                v-model="text"
                center
                clearable
                placeholder="聊点什么吧...."
            >
            <template #button>
                <van-button size="small" type="primary" @click="send" style="margin-right: 16px">发送</van-button>
            </template>
            </van-field>
        </van-cell-group>
    </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import myAxios from '../plugins/myAxios';
import { nextTick, onMounted, ref } from 'vue';
import { showFailToast } from 'vant';
import getCurrentUser from '../services/user';
import { formatDate } from '../plugins/timeFormat';

const router=useRouter();
const route=useRoute();

router.beforeEach((to, from, next) => {
  if (to.name === from.name) {
    // 当前路由与上一个路由相同，表示返回操作
    // 在这里执行刷新页面的操作，例如重新加载数据
    window.location.reload();
  }
  next();
});

const onClickLeft = () => {
    router.back();
}


//聊天对象id
const chatId=Number(route.query.id);
//聊天对象是用户还是队伍
const type=Number(route.query.type);
//聊天对象，可能是用户或队伍
const chatObject=ref();
//聊天对象名称
const name=ref('');
//当前用户自己
const currentUser=ref();
//当前用户的id
// let id;
//聊天内容
const content=ref('');

const chatRoom=ref(null);

//根据id查询队伍或用户
onMounted(async ()=>{
    const res=await getCurrentUser();
    if(res.code===200){
        currentUser.value=res.data;
        // id=currentUser.value.id;
    }
    else{
        showFailToast("获取用户信息失败");
    } 

    //获取聊天对象信息
    let response;
    //私聊
    if(type===0){
        response=await myAxios.get('/user/getById',{
            params:{
                id:chatId,
            }
        })
    }
    //群聊
    else{
        response=await myAxios.get('/team/getById',{
            params:{
                id:chatId,
            }
        })
    }
    if(response.code===200){
        chatObject.value=response.data;
        name.value= type===0 ? chatObject.value.username: chatObject.value.name+'（'+chatObject.value.num+'）';
    }
    else{
        showFailToast("获取用户信息失败");
    }

    //历史消息
    const response1=await myAxios.post("/chat/getChat",{
        id:chatId,
        type:type,
    })
    if(response1.code===200){
        const historyMessages=response1.data;
        if(type===0){
            historyMessages.forEach(chat=>{
                if(chat.fromId===currentUser.value.id && chat.toId===chatObject.value.id){
                    //自己发送的消息
                    // isMine, username,id,avatar, text, createTime
                    createContent(1,chat.fromUsername,chat.fromId,chat.fromAvatar,chat.content,new Date(chat.createTime));
                }
                else if(chat.fromId===chatObject.value.id && chat.toId===currentUser.value.id){
                    //别人发送的消息
                    createContent(0,chat.fromUsername,chat.fromId,chat.fromAvatar,chat.content,new Date(chat.createTime));
                }
                else{
                    showFailToast("获取历史消息失败");
                }
            })
        }
        else{
            historyMessages.forEach(async chat=>{
                if(chat.fromId===currentUser.value.id && chat.toId===chatObject.value.id){
                    //自己发送的消息
                    createContent(1,chat.fromUsername,chat.fromId,chat.fromAvatar,chat.content,new Date(chat.createTime));
                }
                else if(chat.toId===chatObject.value.id){
                    //异步获取用户信息导致消息队列混乱，改成后端封装传递用户参数
                    createContent(0,chat.fromUsername,chat.fromId,chat.fromAvatar,chat.content,new Date(chat.createTime));
                }
                else{
                    showFailToast("获取历史消息失败");
                }
            })
        }
    }
    
    //即时消息
    initWebSocket();
    // nextTick 是一个 Vue.js 的异步更新队列机制，用于在下一次 DOM 更新循环中执行特定的操作。它主要用于在更新之后操作 DOM 元素或获取更新后的数据。
    await nextTick();
    const lastElement=chatRoom.value.lastElementChild;
    if (lastElement) {
        lastElement.scrollIntoView();
    }
})

/**
 * WebSocket
 * （1）websocket在连接后，如果长时间服务端和客户端不发消息，服务端会把websocket给断开。
 * （2）存在网络忽然断开的情况，这时服务器端并没有触发onclose的事件。服务器会继续向客户端发送多余的信息，这些数据会丢失。
 * ⼼跳机制是客户端每隔⼀段时间会向服务端发送⼀个数据包，告诉服务端自己还活着，同时客户端会根据服务端是否会回传⼀个数据包来确定服务端是否还活着。
 * 如果客户端没有收到回复，表示websocket断开连接或者网络出现问题，就需要重连。
 */


/**
 * 作为客户端向后端服务端发送连接请求，接收后端消息
 * url携带的是自己的id，来让后端识别session
 */

let socket=null;
let heartbeatInterval=null;

const initWebSocket=()=>{
    if(typeof(WebSocket)=='undefined'){
        showFailToast("您的浏览器不支持WebSocket");
    }
    else{
        let id= type===0? currentUser.value.id:chatId;
        let wsUrl=`ws://localhost:8080/api/chat/${id}/${type}`
        //创建WebSocket连接
        socket=new WebSocket(wsUrl);
        //心跳包内容
        const heartBeat = {
            content: "ping",
        }
        socket.onopen=function(){
            //定时发送心跳包
            heartbeatInterval=setInterval(function() {
                if(socket.readyState===WebSocket.OPEN){
                    //客户端向后端服务端发送消息
                    socket.send(JSON.stringify(heartBeat));
                    console.log("心脏跳动");
                }
            }, 30000);
            console.log("建立连接");
        }
        // 获得从服务器发送过来的文本消息（别人发过来的消息）
        socket.onmessage=function(msg){
            let message=JSON.parse(msg.data);
            if(type===0){
                //检验消息发送用户是否一致
                if(Number(message.fromId)!==chatId || Number(message.toId)!==currentUser.value.id){
                    console.log("不是当前私信对象");
                    return;
                }
                // isMine, username,id,avatar, text, createTime
                createContent(0,message.fromUsername,message.fromId,message.fromAvatar,message.content,new Date(message.createTime));
            }
            else{
                console.log(message);
                //检验群聊是否一致
                if(Number(message.toId)!==chatId){
                    console.log("不是当前群聊对象")
                    console.log("???");
                    return;
                }
                //自己发出的消息
                if(Number(message.fromId)===currentUser.value.id){
                    console.log("****");
                    return;
                }
                //异步获取用户信息可能其他客户端不能响应，收不到消息
                createContent(0,message.fromUsername,message.fromId,message.fromAvatar,message.content,new Date(message.createTime));
            }
            
            nextTick(() => {
                const lastElement = chatRoom.value.lastElementChild
                if (lastElement) {
                    lastElement.scrollIntoView();
                }
            })
            console.log("接收消息")
        }
        //监听WebSocket连接关闭事件，重新连接
        socket.onclose=function(){
            if(heartbeatInterval){
                clearInterval(heartbeatInterval);
                heartbeatInterval=null;
                console.log("关闭连接");
            }
            setTimeout(initWebSocket,3000); //重新连接
        }
        socket.onerror=function(){
            showFailToast("WebSocket错误");
        }

    }
}


const text=ref('');
// const messages=[];


const send = () => {
    if (!text.value.trim()) {
        showFailToast("不能发送空白信息")
    } else {
        if (typeof (WebSocket) == "undefined") {
            showFailToast("您的浏览器不支持WebSocket");
        } else {
            //消息体
            let message = {
                fromId: currentUser.value.id, //当前用户自己
                toId: chatId, //发送对象
                content: text.value,
                type: type,
                createTime: new Date(),
            }
            //消息发送失败时，提示发送失败
            if(socket.readyState===WebSocket.OPEN){
                socket.send(JSON.stringify(message));
                //封装自己发出的消息
                createContent(1,currentUser.value.username,currentUser.value.id,currentUser.value.avatar,text.value,new Date());
                text.value = '';
                nextTick(() => {
                    const lastElement = chatRoom.value.lastElementChild
                    if (lastElement) {
                        lastElement.scrollIntoView();
                    }
                })
            }
            else{
                // showFailToast("消息发送失败");
                alert("消息发送失败");
            }
            // messages.push({user: currentUser.id, text: text})
        }
    }
}

//封装消息框
const createContent = (isMine, username,id,avatar, text, createTime) => {
    // 当前用户消息
    let html;
    if (isMine) {
        // 当前用户发送的聊天消息，绿色气泡
        html = `
        <div class="message self">
            <div class="myInfo info">
                <img :alt=${username} class="avatar" onclick="showUser(${id})" src=${avatar}>
            </div>
            <div class="myMessage">
                <span class="username">${formatDate(createTime, 'yyyy-MM-dd hh:mm')}&nbsp;&nbsp;&nbsp;我</span>
                <p class="text">${text}</p>
            </div>
        </div>`
    } 
    else {
        // 聊天对象用户聊天消息，灰色的气泡
        html = `
        <div class="message other">
            <img :alt=${username} class="avatar" onclick="showUser(${id})" src=${avatar}>
            <div class="info">
                <span class="username">${username}&nbsp;&nbsp;&nbsp;${formatDate(createTime, 'yyyy-MM-dd hh:mm')}</span>
                <p class="text" >${text}</p>
            </div>
        </div>`
    }
    content.value += html;
}



</script>

<style>
.chat-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  overflow-y: auto;
}

.message {
  display: flex;
  align-items: center;
  margin: 10px 10px;
}

.content {
  padding-top: 22px;
  padding-bottom: 57px;
  display: flex;
  flex-direction: column
}

.self {
  align-self: flex-end;
}

.avatar {
  align-self: flex-start;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  margin-right: 10px;
  margin-left: 10px;
}

.username {
  align-self: flex-start;
  text-align: center;
  max-width: 200px;
  font-size: 12px;
  color: #999;
  padding-bottom: 4px;
  white-space: nowrap;
  overflow: visible;
  background-color: #fff;
}

.info {
  display: flex;
  flex-direction: column;
  order: 2;
}

.myMessage {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.myMessage .username{
    text-align: right;
    width: 100%;
}

.myInfo {
  align-self: flex-start;
}

.text {
  padding: 10px;
  border-radius: 10px;
  background-color: #eee;
  word-wrap: break-word;
  word-break: break-all;
  max-width: 200px; 
}

.other .text {
  align-self: flex-start;
}

.self .text {
  background-color: #0084ff;
  color: #fff;
}
</style>
