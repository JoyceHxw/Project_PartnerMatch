# 伙伴匹配项目
## 项目功能
- 注册登录验证功能
- 基于编辑距离算法根据用户标签实现伙伴推荐功能
- 根据标签搜索用户功能
- 队伍创建、加入、更新、解散等相关功能
- 博文发布、编辑、删除、评论、点赞等相关功能
- 基于websocket实现私信和群聊功能
## 技术栈
### 前端
- vue+vite+vant实现前端页面
- axios向后端发送请求
- websocket实现与服务端的消息发送、接收、处理功能
### 后端
- SpringBoot 3.1.3搭建后端框架
- MySQL数据库存储用户、队伍、聊天、博文、评论、点赞等信息
- MyBatis-Plus进行数据库操作
- Redis缓存对推荐用户列表进行保存和预热
- Redisson分布式锁
- postman对接口进行测试
## 页面展示
![](https://github.com/JoyceHxw/Project_PartnerMatch/blob/main/login.png)
![](https://github.com/JoyceHxw/Project_PartnerMatch/blob/main/index.png)
![](https://github.com/JoyceHxw/Project_PartnerMatch/blob/main/search.png)
![](https://github.com/JoyceHxw/Project_PartnerMatch/blob/main/team.png)
![](https://github.com/JoyceHxw/Project_PartnerMatch/blob/main/blog.png)
![](https://github.com/JoyceHxw/Project_PartnerMatch/blob/main/chat1.png) ![](https://github.com/JoyceHxw/Project_PartnerMatch/blob/main/chat2.png) ![](https://github.com/JoyceHxw/Project_PartnerMatch/blob/main/chat3.png)
![](https://github.com/JoyceHxw/Project_PartnerMatch/blob/main/user.png)
