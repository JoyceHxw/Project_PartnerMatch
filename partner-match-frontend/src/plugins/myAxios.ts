import axios from "axios";

const myAxios= axios.create({
    baseURL: 'http://localhost:8080/api/'
});

myAxios.defaults.withCredentials=true; //请求携带cookie

//将token放入请求头中
myAxios.interceptors.request.use(
  (config) => {
    let token=sessionStorage.getItem("token");
    if(token){
      config.headers['Authorization']=token;
    }
    return config;
  },
  error=>{
    console.log(error);
    return Promise.reject(error);
  }
);

myAxios.interceptors.response.use(function (response) {
  console.log(response?.data?.code);
  if(response?.data?.code===503){
    window.location.href='/login';
  }
  return response.data;
}, function (error) {
    return Promise.reject(error);
});

export default myAxios;