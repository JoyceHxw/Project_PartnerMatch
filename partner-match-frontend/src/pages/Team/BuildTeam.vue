<template>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field
                v-model="buildTeamData.name"
                name="name"
                label="队伍名称"
                placeholder="请输入队伍名称"
                :rules="[{ required: true, message: '请输入队伍名称' }]"
            />
            <van-field
                v-model="buildTeamData.description"
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
                :placeholder="buildTeamData.expireTime || '请选择失效时间'"
                @click="showPicker = true"
            />
            <van-popup v-model:show="showPicker" position="bottom">
                <van-picker-group
                    title="预约日期"
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
                    <van-stepper v-model="buildTeamData.maxNum" max="20" min="2"/>
                </template>
            </van-field>
            <van-field name="radio" label="队伍状态">
                <template #input>
                    <van-radio-group v-model="buildTeamData.status" direction="horizontal">
                    <van-radio name="0">公开</van-radio>
                    <van-radio name="1">私有</van-radio>
                    <van-radio name="2">加密</van-radio>
                    </van-radio-group>
                </template>
            </van-field>
            <van-field
                v-if="Number(buildTeamData.status)===2"
                v-model="buildTeamData.password"
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
import { ref } from 'vue';
import myAxios from '../../plugins/myAxios';
import { showFailToast, showSuccessToast, showToast } from 'vant';
import { useRouter } from 'vue-router';

const router=useRouter();
const initFormData={
    "name":"",
    "description":"",
    "expireTime":null,
    "maxNum":3,
    "status":0,
    "password":""
}

const minDate=new Date();
const columnsType = ['hour', 'minute', 'second'];

//用户填写的表单数据
const buildTeamData=ref({...initFormData});

let dateTime = ref('');
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
    showToast('cancel');
};

const onSubmit = async () => {
    const postData={
        ...buildTeamData.value,
        status: Number(buildTeamData.value.status),
        expireTime: dateTime.value,
    }
    console.log(postData);
    const response= await myAxios.post("/team/build",postData);
    if(response.code===200 && response.data){
        showSuccessToast('创建成功');
        router.push({
            path: '/team',
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