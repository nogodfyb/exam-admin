<template>
<div>
    <!-- 面包屑导航 -->
  <el-breadcrumb separator-class="el-icon-arrow-right">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    <el-breadcrumb-item>登录日志</el-breadcrumb-item>
  </el-breadcrumb>
    <!-- 卡片视图 -->
  <el-card>
    <!-- 用户列表区域 -->
    <el-table :data="userList" border stripe :height="height">
      <el-table-column type="index"></el-table-column>
      <el-table-column label="工号" prop="employeeCode"></el-table-column>
      <el-table-column label="ip地址" prop="ip"></el-table-column>
      <el-table-column label="操作系统" prop="os"></el-table-column>
      <el-table-column label="浏览器" prop="browser"></el-table-column>
      <el-table-column label="登录时间" prop="createTime"></el-table-column>
    </el-table>
<!--    分页区域-->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="queryInfo.pageNum"
      :page-sizes="[3, 5, 10, 15]"
      :page-size="queryInfo.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
  </el-card>
</div>
</template>

<script>
export default {
  data () {
    return {
      height: 550,
      // 获取登录日志列表的参数对象
      queryInfo: {
        // 当前的页数
        pageNum: 1,
        // 当前每页显示多少条数据
        pageSize: 5
      },
      userList: [],
      total: 0
    }
  },
  created () {
    if (window.screen.width < 1400) {
      this.height -= 130
    }
    this.getUserList()
  },
  methods: {
    async getUserList () {
      const { data: res } = await this.$http.get('login-log/loginLogs', { params: this.queryInfo })
      if (res.status !== 200) {
        return this.$message.error('获取登录日志列表失败！')
      }
      this.userList = res.data.list
      this.total = res.data.total
      console.log(res)
    },
    handleSizeChange (newSize) {
      this.queryInfo.pageSize = newSize
      this.getUserList()
    },
    handleCurrentChange (newPage) {
      this.queryInfo.pageNum = newPage
      this.getUserList()
    }
  }
}
</script>

<style scoped>

</style>
