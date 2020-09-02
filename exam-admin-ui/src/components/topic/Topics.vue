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
      <el-col :span="4">
        <el-button type="success" @click="showUploadDialog">导入题目数据</el-button>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="showAddDialog">添加纯图片选项题</el-button>
      </el-col>
      <el-col :span="4">
        <el-link type="info" :href="BASE_REQUEST_PATH+'exam/topic/download'">下载模板</el-link>
      </el-col>
    </el-row>
    <!-- 题目列表区域 -->
    <el-table :data="userList" border stripe :height="height">
      <el-table-column type="index" fixed></el-table-column>
      <el-table-column label="题干" prop="topicDesc"></el-table-column>
      <el-table-column label="题型" prop="type" :formatter="formatter"></el-table-column>
      <el-table-column label="选项A"  width="125">
        <template slot-scope="scope">
          <el-image v-if="scope.row.isGraphic===true&&scope.row.answer1!==null" fit="cover" :src="BASE_REQUEST_IMG_PATH+scope.row.answer1" style="width: 100px;height: 100px">
          </el-image>
          <p v-if="scope.row.isGraphic===false">{{scope.row.answer1}}</p>
        </template>
      </el-table-column>
      <el-table-column label="选项B"  width="125">
        <template slot-scope="scope">
          <el-image v-if="scope.row.isGraphic===true&&scope.row.answer2!==null" fit="cover" :src="BASE_REQUEST_IMG_PATH+scope.row.answer2" style="width: 100px;height: 100px">
          </el-image>
          <p v-if="scope.row.isGraphic===false">{{scope.row.answer2}}</p>
        </template>
      </el-table-column>
      <el-table-column label="选项C"  width="125">
        <template slot-scope="scope">
          <el-image v-if="scope.row.isGraphic===true&&scope.row.answer3!==null" fit="cover" :src="BASE_REQUEST_IMG_PATH+scope.row.answer3" style="width: 100px;height: 100px">
          </el-image>
          <p v-if="scope.row.isGraphic===false">{{scope.row.answer3}}</p>
        </template>
      </el-table-column>
      <el-table-column label="选项D"  width="125">
        <template slot-scope="scope">
          <el-image v-if="scope.row.isGraphic===true&&scope.row.answer4!==null" fit="cover" :src="BASE_REQUEST_IMG_PATH+scope.row.answer4" style="width: 100px;height: 100px">
          </el-image>
          <p v-if="scope.row.isGraphic===false">{{scope.row.answer4}}</p>
        </template>
      </el-table-column>
      <el-table-column label="选项E"  width="125">
        <template slot-scope="scope">
          <el-image v-if="scope.row.isGraphic===true&&scope.row.answer5!==null" fit="cover" :src="BASE_REQUEST_IMG_PATH+scope.row.answer5" style="width: 100px;height: 100px">
          </el-image>
          <p v-if="scope.row.isGraphic===false">{{scope.row.answer5}}</p>
        </template>
      </el-table-column>
      <el-table-column label="选项F"  width="125">
        <template slot-scope="scope">
          <el-image v-if="scope.row.isGraphic===true&&scope.row.answer6!==null" fit="cover" :src="BASE_REQUEST_IMG_PATH+scope.row.answer6" style="width: 100px;height: 100px">
          </el-image>
          <p v-if="scope.row.isGraphic===false">{{scope.row.answer6}}</p>
        </template>
      </el-table-column>
      <el-table-column label="正确选项" prop="correctAnswer"></el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180"></el-table-column>
      <el-table-column label="更新时间" prop="updateTime" width="180"></el-table-column>
      <el-table-column label="操作" width="450px">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">编辑</el-button>
          <el-button type="info" icon="el-icon-upload" size="mini" @click="uploadImage(scope.row.id)">上传题干图片</el-button>
          <el-button type="success" icon="el-icon-thumb"  size="mini" @click="watchImage(scope.row.imageName)">查看题干图片</el-button>
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
      :on-success="afterUpload"
      :action="BASE_REQUEST_PATH+'exam/topic/upload'"
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
    title="查看该题干附件图片"
    :visible.sync="watchImgDialogVisible"
    width="50%" @close="watchImgDialogClosed"
  >
    <el-image
      v-if="src.indexOf('null')==-1"
      :src="src"
      fit="fill"></el-image>
  </el-dialog>
  <!-- 添加纯图片选项题对话框 -->
  <el-dialog
    title="添加纯图片选项题"
    :visible.sync="addTopicDialogVisible"
    width="50%" @close="addTopicDialogClosed"
  >
    <!--      内容主体区域-->
    <el-form :model="addForm"  ref="addFormRef" :rules="addFormRules" label-width="100px" >
      <el-form-item label="题干" prop="topicDesc" >
        <el-input type="textarea" v-model="addForm.topicDesc"></el-input>
      </el-form-item>
      <el-form-item label="题型" prop="type">
        <el-select v-model="addForm.type" placeholder="请选择题型">
          <el-option label="单选题" value="1"></el-option>
          <el-option label="判断题" value="2"></el-option>
          <el-option label="多选题" value="3"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <!--      底部区域-->
    <span slot="footer" class="dialog-footer">
    <el-button @click="addTopicDialogVisible = false">取 消</el-button>
    <el-button type="primary"  @click="addTopic">确 定</el-button>
      </span>
  </el-dialog>
  <!-- 编辑题目对话框 -->
  <el-dialog
    title="编辑题目"
    :visible.sync="editTopicDialogVisible"
    width="50%" @close="editTopicDialogClosed"
  >
    <!--      内容主体区域-->
    <el-form :model="editForm"  ref="editFormRef" :rules="addFormRules" label-width="100px" >
      <el-form-item label="题干" prop="topicDesc" >
        <el-input type="textarea" v-model="editForm.topicDesc"></el-input>
      </el-form-item>
      <el-form-item label="题型" prop="type">
        <el-select v-model="editForm.type" placeholder="请选择题型">
          <el-option label="单选题" :value="1"></el-option>
          <el-option label="判断题" :value="2"></el-option>
          <el-option label="多选题" :value="3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="正确选项" prop="correctAnswer" >
        <el-input  v-model="editForm.correctAnswer"></el-input>
      </el-form-item>
      <div v-if="editForm.isGraphic===false">
        <el-form-item label="选项A">
          <el-input type="textarea" v-model="editForm.answer1"></el-input>
        </el-form-item>
        <el-form-item label="选项B">
          <el-input type="textarea" v-model="editForm.answer2"></el-input>
        </el-form-item>
        <el-form-item label="选项C">
          <el-input type="textarea" v-model="editForm.answer3"></el-input>
        </el-form-item>
        <el-form-item label="选项D">
          <el-input type="textarea" v-model="editForm.answer4"></el-input>
        </el-form-item>
        <el-form-item label="选项E">
          <el-input type="textarea" v-model="editForm.answer5"></el-input>
        </el-form-item>
        <el-form-item label="选项F">
          <el-input type="textarea" v-model="editForm.answer6"></el-input>
        </el-form-item>
      </div>
      <div v-if="editForm.isGraphic===true">
        <el-form-item label="选项A">
          <el-upload  drag with-credentials :action="uploadImgPathA" multiple>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          </el-upload>
        </el-form-item>
        <el-form-item label="选项B">
          <el-upload  drag with-credentials :action="uploadImgPathB" multiple>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          </el-upload>
        </el-form-item>
        <el-form-item label="选项C">
          <el-upload  drag with-credentials :action="uploadImgPathC" multiple>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          </el-upload>
        </el-form-item>
        <el-form-item label="选项D">
          <el-upload  drag with-credentials :action="uploadImgPathD" multiple>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          </el-upload>
        </el-form-item>
        <el-form-item label="选项E">
          <el-upload  drag with-credentials :action="uploadImgPathE" multiple>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          </el-upload>
        </el-form-item>
        <el-form-item label="选项F">
          <el-upload  drag with-credentials :action="uploadImgPathF" multiple>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          </el-upload>
        </el-form-item>
      </div>
    </el-form>
    <!--      底部区域-->
    <span slot="footer" class="dialog-footer">
    <el-button @click="editTopicDialogVisible = false">取 消</el-button>
    <el-button type="primary"  @click="editTopic">确 定</el-button>
      </span>
  </el-dialog>
</div>
</template>

<script>
import config from '../../util/config'
export default {
  data () {
    return {
      height: 500,
      // 获取登录日志列表的参数对象
      queryInfo: {
        // 当前的页数
        pageNum: 1,
        // 当前每页显示多少条数据
        pageSize: 5
      },
      addForm: {
        topicDesc: '',
        type: undefined
      },
      editForm: {
      },
      userList: [],
      total: 0,
      uploadDialogVisible: false,
      uploadImgDialogVisible: false,
      watchImgDialogVisible: false,
      addTopicDialogVisible: false,
      editTopicDialogVisible: false,
      uploadImgPath: '',
      uploadImgPathA: '',
      uploadImgPathB: '',
      uploadImgPathC: '',
      uploadImgPathD: '',
      uploadImgPathE: '',
      uploadImgPathF: '',
      BASE_REQUEST_PATH: config.BASE_REQUEST_PATH,
      BASE_REQUEST_IMG_PATH: config.BASE_REQUEST_IMG_PATH,
      src: '',
      addFormRules: {
        topicDesc: [
          { required: true, message: '请输入题干信息', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择题型', trigger: 'blur' }
        ],
        correctAnswer: [
          { required: true, message: '正确选项必填', trigger: 'blur' }
        ]
      }
    }
  },
  created () {
    if (window.screen.width < 1400) {
      this.height -= 120
    }
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
    showAddDialog () {
      this.addTopicDialogVisible = true
    },
    showEditDialog (topic) {
      this.editTopicDialogVisible = true
      // 序列化及反序列化以免影响渲染视图
      this.editForm = JSON.parse(JSON.stringify(topic))
      // 如果该题目为纯图片选项题，更新每个选项的上传路径
      if (this.editForm.isGraphic === true) {
        this.uploadImgPathA = this.BASE_REQUEST_PATH + 'exam/topic/uploadImageItem/' + topic.id + '?select=A'
        this.uploadImgPathB = this.BASE_REQUEST_PATH + 'exam/topic/uploadImageItem/' + topic.id + '?select=B'
        this.uploadImgPathC = this.BASE_REQUEST_PATH + 'exam/topic/uploadImageItem/' + topic.id + '?select=C'
        this.uploadImgPathD = this.BASE_REQUEST_PATH + 'exam/topic/uploadImageItem/' + topic.id + '?select=D'
        this.uploadImgPathE = this.BASE_REQUEST_PATH + 'exam/topic/uploadImageItem/' + topic.id + '?select=E'
        this.uploadImgPathF = this.BASE_REQUEST_PATH + 'exam/topic/uploadImageItem/' + topic.id + '?select=F'
      }
    },
    uploadDialogClosed () {
      this.getUserList()
    },
    async afterUpload () {
      const { data: res } = await this.$http.get('topic/confirm')
      if (res.status === 200) {
        this.$message.warning({
          message: '本次上传有异常内容，请允许浏览器下载异常信息',
          duration: 0,
          showClose: true
        })
        window.location.href = this.BASE_REQUEST_PATH + 'exam/topic/download/exception'
      }
    },
    uploadImgDialogClosed () {
      this.getUserList()
    },
    addTopicDialogClosed () {
      this.$refs.addFormRef.resetFields()
    },
    editTopicDialogClosed () {
      this.getUserList()
      this.$refs.editFormRef.resetFields()
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
      this.uploadImgPath = this.BASE_REQUEST_PATH + 'exam/topic/uploadImage/' + id
    },
    watchImage (imageName) {
      this.src = this.BASE_REQUEST_IMG_PATH + imageName
      this.watchImgDialogVisible = true
    },
    addTopic () {
      this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) {
          return false
        }
        // 可以发起添加题目的网络请求
        const { data: res } = await this.$http.post('topic/add', this.addForm)
        if (res.status !== 200) {
          return this.$message.error('添加题目失败！')
        }
        this.$message.success('添加题目成功！')
        // 隐藏添加题目的对话框
        this.addTopicDialogVisible = false
        // 重新获取题目列表数据
        await this.getUserList()
      })
    },
    editTopic () {
      this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) {
          return false
        }
        const newCorrect = this.editForm.correctAnswer.toUpperCase().split('').sort().join('')
        this.editForm.correctAnswer = newCorrect
        // 可以发起修改题目的网络请求
        const url = 'topic/topics/' + this.editForm.id
        const { data: res } = await this.$http.put(url, this.editForm)
        if (res.status !== 200) {
          return this.$message.error('修改题目失败！')
        }
        this.$message.success('修改题目成功！')
        // 隐藏添加题目的对话框
        this.editTopicDialogVisible = false
        // 重新获取题目列表数据
        await this.getUserList()
      })
    },
    // 删除topic
    deleteTopic (id) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 暂不开启删除功能
        this.deleteTopicById(id)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    async deleteTopicById (id) {
      // 可以发起修改用户的网络请求
      const { data: res } = await this.$http.delete(`topic/topics/${id}`)
      if (res.status !== 200) {
        return this.$message.error('删除失败')
      }
      this.$message({
        type: 'success',
        message: '删除成功!'
      })
      this.getUserList()
    }
  }
}
</script>

<style scoped>

</style>
