import axios from "axios";

const myAxios= axios.create({
    baseURL: 'http://localhost:8080/api/'
});

myAxios.defaults.withCredentials=true; //请求携带cookie

myAxios.interceptors.response.use(function (response) {
  if(response?.data?.code===503){
    window.location.href='/login';
  }
  return response.data;
}, function (error) {
    return Promise.reject(error);
});

export default myAxios;