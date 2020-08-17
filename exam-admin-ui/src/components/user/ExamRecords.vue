<template>
<div>
    <!-- 面包屑导航 -->
  <el-breadcrumb separator-class="el-icon-arrow-right">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    <el-breadcrumb-item>答题记录</el-breadcrumb-item>
  </el-breadcrumb>
    <!-- 卡片视图 -->
  <el-card>
    <!-- 用户列表区域 -->
    <el-table :data="userList" border stripe height="550" @expand-change="expand">
      <!--          展开列-->
      <el-table-column type="expand" >
        <template slot-scope="scope">
          <el-row>
            <el-col :span="4"><div >题目Id</div></el-col>
            <el-col :span="4"><div >提交选项</div></el-col>
          </el-row>
          <el-row>
            <el-col :span="4"><div >{{ JSON.parse(scope.row.submitContent).answerList[0].topicId}}</div></el-col>
            <el-col :span="4"><div >{{ JSON.parse(scope.row.submitContent).answerList[0].radio}}</div></el-col>
          </el-row>
          <el-row v-for="n in 9" :key="n">
            <el-col :span="4"><div >{{ JSON.parse(scope.row.submitContent).answerList[n].topicId}}</div></el-col>
            <el-col :span="4"><div >{{ JSON.parse(scope.row.submitContent).answerList[n].radio}}</div></el-col>
          </el-row>
        </template>
      </el-table-column>
      <el-table-column type="index"></el-table-column>
      <el-table-column label="工号" prop="employeeCode"></el-table-column>
      <el-table-column label="答题得分" prop="score"></el-table-column>
      <el-table-column label="答题时间" prop="createTime"></el-table-column>
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
      // 获取登录日志列表的参数对象
      queryInfo: {
        // 当前的页数
        pageNum: 1,
        // 当前每页显示多少条数据
        pageSize: 5
      },
      userList: [],
      total: 0,
      currentTopics: []
    }
  },
  created () {
    this.getUserList()
  },
  methods: {
    async getUserList () {
      const { data: res } = await this.$http.get('record/records', { params: this.queryInfo })
      if (res.status !== 200) {
        return this.$message.error('获取答题记录列表失败！')
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
    },
    // 展开之后绑定答题记录到currentTopics
    expand (row) {
      console.log(JSON.parse(row.submitContent))
      this.currentTopics = JSON.parse(row.submitContent)
    }
  }
}
</script>

<style scoped>

</style>
