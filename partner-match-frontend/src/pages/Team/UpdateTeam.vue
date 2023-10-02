<template>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field
                v-model="addTeamData.name"
                name="name"
                label="队伍名称"
                placeholder="请输入队伍名称"
                :rules="[{ required: true, message: '请输入队伍名称' }]"
            />
            <van-field
                v-model="addTeamData.description"
                rows="1"
                autosize
                label="队伍描述"
                type="textarea"
                placeholder="请输入队伍描述"
            />
            <van-field
                v-model="dateTime"
                is-link
                readonly
                name="datePicker"
                label="失效时间"
                :placeholder="addTeamData.expireTime || '请选择失效时间'"
                @click="showPicker = true"
            />
            <van-popup v-model:show="showPicker" position="bottom">
                <van-picker-group
                    title="失效时间"
                    :tabs="['选择日期', '选择时间']"
                    @confirm="onConfirm"
                    @cancel="onCancel"
                >
                    <van-date-picker
                        v-model="currentDate"
                        :min-date="minDate"
                    />
                    <van-time-picker 
                        v-model="currentTime" 
                        :columns-type="columnsType"
                    />
                </van-picker-group>
            </van-popup>
            <van-field name="stepper" label="最大人数">
                <template #input>
                    <van-stepper v-model="addTeamData.maxNum" max="20" :min="addTeamData.num"/>
                </template>
            </van-field>
            <van-field name="radio" label="队伍状态">
                <template #input>
                    <van-radio-group v-model="addTeamData.status" direction="horizontal">
                        <van-radio name="0">公开</van-radio>
                        <van-radio name="1">私有</van-radio>
                        <van-radio name="2">加密</van-radio>
                    </van-radio-group>
                </template>
            </van-field>
            <van-field
                v-if="Number(addTeamData.status)===2"
                v-model="addTeamData.password"
                type="password"
                name="password"
                label="密码"
                placeholder="请输入队伍密码"
                :rules="[{ required: true, message: '请输入队伍密码' }]"
            />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
            提交
            </van-button>
        </div>
    </van-form>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import myAxios from '../../plugins/myAxios';
import { showFailToast, showSuccessToast, showToast } from 'vant';
import { useRoute, useRouter } from 'vue-router';
import { TeamType } from '../../models/team';

const router=useRouter();

const minDate=new Date();
const columnsType = ['hour', 'minute', 'second'];

//用户填写的表单数据
const addTeamData=ref<TeamType>({});


/**
* 加载队伍信息
*/

const route=useRoute();
onMounted(async ()=>{
    const res=await myAxios.get("/team/getById",{
        params:{
            'id':route.query.id
        }
    });
    if(res?.code===200){
        addTeamData.value=res.data;
    }
    else{
        showFailToast("加载队伍失败");
    }
})



let dateTime = addTeamData.expireTime;
const showPicker = ref(false);

const currentDate = ref([]);
const currentTime = ref(['12','00','00']);

const onConfirm = () => {
    const selectedDate = currentDate.value.join('/');
    const selectedTime = currentTime.value.join(':');
    // 构建日期时间参数
    const dateTimeString = `${selectedDate} ${selectedTime}Z`;
    // 创建日期时间对象
    const dateTimeJSON= JSON.stringify(new Date(dateTimeString));
    dateTime = JSON.parse(dateTimeJSON);
    showPicker.value = false;
};

const onCancel = () => {
    showPicker.value=false;
};

const onSubmit = async () => {
    const postData={
        ...addTeamData.value,
        status: Number(addTeamData.value.status),
        expireTime: dateTime
    }
    const response= await myAxios.post("/team/update",postData);
    if(response.code===200 && response.data){
        showSuccessToast('更新成功');
        router.push({
            path: '/user/myBuildTeam',
            replace: true,
        })
    }
    else{
        showFailToast(response.description);
    }
};

</script>

<style scoped>
</style>