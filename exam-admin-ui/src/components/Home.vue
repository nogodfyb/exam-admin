<template>
  <el-container class="home-container" >
    <el-header >
      <div>
        <img src="../assets/logo.png" alt="" style="height: 50px ;width: 50px">
        <span>答题后台管理</span>
      </div>
      <el-button type="info" @click="loginOut">退出</el-button>
    </el-header>
    <el-container style="height: 850px">
<!--      侧边栏-->
      <el-aside :width="isCollapse ? '64px' : '200px'" >
        <div class="toggle-button" @click="toggleCollapse">|||</div>
        <el-menu
          background-color="#333744" text-color="#fff" active-text-color="#409eef"
          unique-opened :collapse="isCollapse" :collapse-transition="false" router
          :default-active="activePath">
          <!--一级菜单-->
          <el-submenu :index="item.id+''" v-for=" item in menuList" :key="item.id">
            <template slot="title">
              <i :class="iconsObj[item.id]"></i>
              <span>{{item.authName}}</span>
            </template>
            <!--二级菜单-->
              <el-menu-item :index="subItem.path+''" v-for=" subItem in item.children " :key="subItem.id"
                            @click="saveNavState(subItem.path)">
                <template slot="title">
                  <i class="el-icon-menu"></i>
                  <span>{{subItem.authName}}</span>
                </template>
              </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  data () {
    return {
      // 左侧菜单列表
      menuList: [],
      iconsObj: {
        1: 'iconfont icon-user',
        2: 'iconfont icon-tijikongjian',
        3: 'iconfont icon-shangpin',
        4: 'iconfont icon-danju',
        5: 'iconfont icon-baobiao'
      },
      // 是否折叠
      isCollapse: false,
      activePath: ''
    }
  },
  created () {
    this.getMenuList()
    this.activePath = window.sessionStorage.getItem('activePath')
  },
  methods: {
    loginOut () {
      document.cookie = 'JSESSIONID='
      window.sessionStorage.removeItem('activePath')
      this.$router.push('/login')
    },
    async getMenuList () {
      const { data: res } = await this.$http.get('menu/list')
      if (res.status !== 200) {
        return this.$message.error(res.msg)
      }
      this.menuList = res.data
    },
    // 点击按钮，切换菜单的折叠与展开
    toggleCollapse () {
      this.isCollapse = !this.isCollapse
    },
    // 保存链接的激活状态
    saveNavState (activePath) {
      window.sessionStorage.setItem('activePath', activePath)
      this.activePath = activePath
    }
  }
}
</script>

<style lang="less" scoped>
  .home-container{
    height: 100%;
  }
  .el-header{
    background-color: #373d41;
    display: flex;
    justify-content:space-between ;
    padding-left: 0;
    align-items: center;
    color: #ffffff;
    font-size: 18px;
    >div{
      display: flex;
      align-items: center;
      span {
        margin-left: 15px;
      }
    }
  }
  .el-aside{
    background: #333744;
    .el-menu{
      border-right: none;
    }
  }
  .el-main{
    background: #eaedf1;
  }
  .iconfont{
    margin-right:10px ;
  }
  .toggle-button {
    background-color: #4a5064;
    font-size: 10px;
    line-height: 24px;
    color: #fff;
    text-align: center;
    letter-spacing: 0.2em;
    cursor: pointer;
  }
</style>
