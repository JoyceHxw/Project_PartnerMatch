<template>
    <van-form @submit="onSubmit">
        <van-field
            v-model="value"
            :name="editUser.editKey"
            :label="editUser.editName"
            :placeholder="`请输入${editUser.editName}`"
        />
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
            提交
            </van-button>
        </div>
    </van-form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRoute,useRouter } from 'vue-router';
import myAxios from '../../plugins/myAxios';
import { showFailToast, showSuccessToast } from 'vant';
import getCurrentUser from '../../services/user';

//传递的参数
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

let value=editUser.value.editKey==='gender'? Number(editUser.value.currentValue)===0?'女':'男': editUser.value.currentValue;

// console.log(editUser.value);

const router=useRouter();
const onSubmit = async () => {
    const userData=await getCurrentUser();
    const response=await myAxios.post('/user/update',{
        'id':userData.data.id,
        [editUser.value.editKey]: editUser.value.editKey==='gender'? value==='男'? 1:0 :value,
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