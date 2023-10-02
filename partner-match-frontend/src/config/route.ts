import Index from '../pages/Index.vue';
import Team from '../pages/Team.vue';
import User from '../pages/User.vue';
import Chat from '../pages/Chat.vue';
import UserInfo from '../pages/User/UserInfo.vue';
import UserEdit from '../pages/User/UserEdit.vue';
import UserMyBuildTeam from '../pages/User/UserMyBuildTeam.vue';
import UserMyJoinTeam from '../pages/User/UserMyJoinTeam.vue';
import UserMyBlog from '../pages/User/UserMyBlog.vue';
import Search from '../pages/Search.vue';
import SearchResult from '../pages/SearchResult.vue';
import Login from '../pages/Login.vue';
import Register from '../pages/Register.vue'
import BuildTeam from '../pages/Team/BuildTeam.vue';
import UpdateTeam from '../pages/Team/UpdateTeam.vue';
import ChatContent from '../pages/Chat/ChatContent.vue';
import Blog from '../pages/Blog.vue';
import PublishBlog from '../pages/Blog/PublishBlog.vue'
import EditBlog from '../pages/Blog/EditBlog.vue'
import BlogDetail from '../pages/Blog/BlogDetail.vue'
import TagsEdit from '../pages/TagsEdit.vue'


// 2. 定义一些路由
// 每个路由都需要映射到一个组件。
const routes = [
    { path: '/', title:'伙伴匹配', component: Index, meta: { layout: 'index'} },
    { path: '/team', title:'加入队伍',component: Team },
    { path: '/search', title:'搜索伙伴',component: Search },
    { path: '/user', title:'个人',component: User },
    { path: '/chat',title:'聊天', component: Chat },
    { path: '/user/info',title:'我的信息', component: UserInfo },
    { path: '/user/edit',title:'编辑信息', component: UserEdit },
    { path: '/user/myBuildTeam',title:'我创建的队伍', component: UserMyBuildTeam },
    { path: '/user/myJoinTeam', title:'我加入的队伍',component: UserMyJoinTeam },
    { path: '/user/myBlog', title:'我发布的博文',component: UserMyBlog },
    { path: '/search/result', title:'伙伴搜索结果',component: SearchResult },
    { path: '/login', title:'登录',component: Login, meta: { layout: 'login'} },
    { path: '/register', title:'注册',component: Register, meta: { layout: 'register'} },
    { path: '/team/build', title:'创建队伍',component: BuildTeam },
    { path: '/team/update',title:'更新队伍', component: UpdateTeam },
    { path: '/chat/content',title:'聊天', component: ChatContent, meta: { layout: 'chat'} },
    { path: '/blog', title:'广场', component: Blog },
    { path: '/blog/publish', title:'发布博文', component: PublishBlog },
    { path: '/blog/edit', title:'编辑博文', component: EditBlog },
    { path: '/blog/detail', title:'博文详情', component: BlogDetail },
    { path: '/tagsEdit', title:'选择标签', component: TagsEdit, meta: { layout: 'tags' } },
]

export default routes;