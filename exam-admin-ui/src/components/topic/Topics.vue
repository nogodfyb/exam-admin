<template>
<div>
    <!-- 面包屑导航 -->
  <el-breadcrumb separator-class="el-icon-arrow-right">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>题目管理</el-breadcrumb-item>
    <el-breadcrumb-item>题库列表</el-breadcrumb-item>
  </el-breadcrumb>
    <!-- 卡片视图 -->
  <el-card>
    <!-- 搜索与添加区域  -->
    <el-row :gutter="20">
      <el-col :span="3">
        <el-button type="success" @click="showUploadDialog">导入题目数据</el-button>
      </el-col>
      <el-col :span="2">
        <el-link type="info" href="http://localhost:8083/exam/topic/download">下载模板</el-link>
      </el-col>
    </el-row>
    <!-- 题目列表区域 -->
    <el-table :data="userList" border stripe height="500">
      <el-table-column type="index" fixed></el-table-column>
      <el-table-column label="题干" prop="topicDesc"></el-table-column>
      <el-table-column label="题型" prop="type" :formatter="formatter"></el-table-column>
      <el-table-column label="选项A" prop="answer1"></el-table-column>
      <el-table-column label="选项B" prop="answer2"></el-table-column>
      <el-table-column label="选项C" prop="answer4"></el-table-column>
      <el-table-column label="选项D" prop="answer4"></el-table-column>
      <el-table-column label="选项E" prop="answer5"></el-table-column>
      <el-table-column label="选项F" prop="answer6"></el-table-column>
      <el-table-column label="正确选项" prop="correctAnswer"></el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180"></el-table-column>
      <el-table-column label="更新时间" prop="updateTime" width="180"></el-table-column>
      <el-table-column label="操作" width="400px">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row.id)">编辑</el-button>
          <el-button type="info" icon="el-icon-upload" size="mini" @click="uploadImage(scope.row.id)">上传图片</el-button>
          <el-button type="success"  size="mini" @click="watchImage(scope.row.imageName)">查看图片</el-button>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteTopic(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
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
  <!-- 上传excel -->
  <el-dialog
    title="上传数据"
    :visible.sync="uploadDialogVisible"
    width="30%" @close="uploadDialogClosed"
  >
    <el-upload
      class="upload-demo"
      drag with-credentials
      action="http://localhost:8083/exam/topic/upload"
      multiple>
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传xlsx文件，且不超过500kb</div>
    </el-upload>
  </el-dialog>

  <!-- 上传img -->
  <el-dialog
    title="上传数据"
    :visible.sync="uploadImgDialogVisible"
    width="30%" @close="uploadImgDialogClosed"
  >
    <el-upload
      class="upload-demo"
      drag with-credentials
      :action="uploadImgPath"
      multiple>
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传图片文件，且不超过1MB</div>
    </el-upload>
  </el-dialog>

  <!-- 查看img -->
  <el-dialog
    title="查看该题目附件图片"
    :visible.sync="watchImgDialogVisible"
    width="50%" @close="watchImgDialogClosed"
  >
    <el-image
      v-if="src.indexOf('null')==-1"
      :src="src"
      fit="fill"></el-image>
  </el-dialog>
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
      uploadDialogVisible: false,
      uploadImgDialogVisible: false,
      watchImgDialogVisible: false,
      uploadImgPath: '',
      src: ''
    }
  },
  created () {
    this.getUserList()
  },
  methods: {
    async getUserList () {
      const { data: res } = await this.$http.get('topic/topics', { params: this.queryInfo })
      if (res.status !== 200) {
        return this.$message.error('获取题目列表失败！')
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
    async showUploadDialog () {
      const { data: res } = await this.$http.get('employee/isLogin')
      if (res.status !== 200) {
        return this.$message.error('您还未登录')
      }
      this.uploadDialogVisible = true
    },
    uploadDialogClosed () {
      this.getUserList()
    },
    uploadImgDialogClosed () {

    },
    watchImgDialogClosed () {

    },
    formatter (row, column) {
      if (row.type === 1) {
        return '单选'
      } else { return row.type === 2 ? '判断' : '多选' }
    },
    uploadImage (id) {
      this.uploadImgDialogVisible = true
      this.uploadImgPath = 'http://localhost:8083/exam/topic/uploadImage/' + id
    },
    watchImage (imageName) {
      this.src = 'http://localhost:8083/imgs/' + imageName
      console.log(this.src.indexOf('null'))
      this.watchImgDialogVisible = true
    }
  }
}
</script>

<style scoped>

</style>
