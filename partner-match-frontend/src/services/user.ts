import myAxios from "../plugins/myAxios";

const getCurrentUser=async()=>{
    return await myAxios.get('/user/currentUser');
}

export default getCurrentUser;
