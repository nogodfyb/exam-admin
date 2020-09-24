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
      <el-col :span="9">
        <el-input placeholder="请输入题干信息" v-model="queryInfo.topicDesc" clearable @clear="search">
          <el-button slot="append" icon="el-icon-search" @click="search"></el-button>
        </el-input>
      </el-col>
      <el-col :span="3">
        <el-button type="success" @click="showUploadDialog" size="medium">导入题目数据</el-button>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="showAddDialog" size="medium">添加纯图片选项题</el-button>
      </el-col>
      <el-col :span="2">
        <el-link type="info" :href="BASE_REQUEST_PATH+'exam/topic/download'">下载模板</el-link>
      </el-col>
    </el-row>
    <!-- 题目列表区域 -->
    <el-table :data="list" border  :height="height">
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
      <el-table-column label="选项E"  width="125" v-if="answer5Exist">
        <template slot-scope="scope">
          <el-image v-if="scope.row.isGraphic===true&&scope.row.answer5!==null" fit="cover" :src="BASE_REQUEST_IMG_PATH+scope.row.answer5" style="width: 100px;height: 100px">
          </el-image>
          <p v-if="scope.row.isGraphic===false">{{scope.row.answer5}}</p>
        </template>
      </el-table-column>
      <el-table-column label="选项F"  width="125" v-if="answer6Exist">
        <template slot-scope="scope">
          <el-image v-if="scope.row.isGraphic===true&&scope.row.answer6!==null" fit="cover" :src="BASE_REQUEST_IMG_PATH+scope.row.answer6" style="width: 100px;height: 100px">
          </el-image>
          <p v-if="scope.row.isGraphic===false">{{scope.row.answer6}}</p>
        </template>
      </el-table-column>
      <el-table-column label="正确选项" prop="correctAnswer" width="100"></el-table-column>
      <el-table-column label="是否必答" width="90">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.isMust" @change="topicMustChange(scope.row)">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250px">
        <template slot-scope="scope">
          <el-row>
            <el-button type="text" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" size="mini" @click="deleteTopic(scope.row.id)">删除</el-button>
            <el-button type="text" icon="el-icon-setting" @click="showSettingPostDialog(scope.row.id)" size="mini">分配岗位</el-button>
          </el-row>
          <el-row>
            <el-button type="text" icon="el-icon-upload" size="mini" @click="uploadImage(scope.row.id)">上传题干图片</el-button>
            <el-button type="text" icon="el-icon-thumb"  size="mini" @click="watchImage(scope.row.imageName)">查看题干图片</el-button>
          </el-row>
        </template>
      </el-table-column>
      <el-table-column label="最后更新人" prop="lastOperatorId" width="100"></el-table-column>
      <el-table-column label="更新时间" prop="updateTime" width="180"></el-table-column>
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
    width="40%" @close="uploadDialogClosed"
  >
    <el-form ref="uploadForm" :model="uploadForm"  label-width="100px">
      <el-form-item label="当前工段:">
        <el-input v-model="currentWorkSectionName" disabled style="width: 100px"></el-input>
      </el-form-item>
      <el-form-item label="产品线和岗位:">
        <el-cascader
          :options="options" v-model="selectKeys"
          :props="props"
          placeholder="请选择产品线和岗位" style="margin-bottom: 10px"
          clearable></el-cascader>
      </el-form-item>
    </el-form>
    <el-upload
      class="upload-demo"
      drag with-credentials :before-upload="beforeUpload"
      :on-success="afterUpload" :data="uploadForm"
      :action="BASE_REQUEST_PATH+'exam/topic/upload'"
      ref="uploadExcel">
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
    <span slot="footer">
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
          <el-row>
            <el-col :span="16">
              <el-upload  drag with-credentials :action="uploadImgPathA" :on-success="afterImgUpload">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              </el-upload>
            </el-col>
            <el-col :span="8">
              <el-image fit="cover" v-if="editForm.answer1!==null" :src="BASE_REQUEST_IMG_PATH+editForm.answer1" style="width: 200px;height: 200px">
              </el-image>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="选项B">
          <el-row>
            <el-col :span="16">
              <el-upload  drag with-credentials :action="uploadImgPathB" :on-success="afterImgUpload">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              </el-upload>
            </el-col>
            <el-col :span="8">
              <el-image fit="cover" v-if="editForm.answer2!==null" :src="BASE_REQUEST_IMG_PATH+editForm.answer2" style="width: 200px;height: 200px">
              </el-image>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="选项C">
          <el-row>
            <el-col :span="16">
              <el-upload  drag with-credentials :action="uploadImgPathC" :on-success="afterImgUpload">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              </el-upload>
            </el-col>
            <el-col :span="8">
              <el-image fit="cover" v-if="editForm.answer3!==null" :src="BASE_REQUEST_IMG_PATH+editForm.answer3" style="width: 200px;height: 200px">
              </el-image>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="选项D">
          <el-row>
            <el-col :span="16">
              <el-upload  drag with-credentials :action="uploadImgPathD" :on-success="afterImgUpload">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              </el-upload>
            </el-col>
            <el-col :span="8">
              <el-image fit="cover" v-if="editForm.answer4!==null" :src="BASE_REQUEST_IMG_PATH+editForm.answer4" style="width: 200px;height: 200px">
              </el-image>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="选项E">
          <el-row>
            <el-col :span="16">
              <el-upload  drag with-credentials :action="uploadImgPathE" :on-success="afterImgUpload">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              </el-upload>
            </el-col>
            <el-col :span="8">
              <el-image fit="cover" v-if="editForm.answer5!==null" :src="BASE_REQUEST_IMG_PATH+editForm.answer5" style="width: 200px;height: 200px">
              </el-image>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="选项F">
          <el-row>
            <el-col :span="16">
              <el-upload  drag with-credentials :action="uploadImgPathF" :on-success="afterImgUpload">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              </el-upload>
            </el-col>
            <el-col :span="8">
              <el-image fit="cover" v-if="editForm.answer6!==null" :src="BASE_REQUEST_IMG_PATH+editForm.answer6" style="width: 200px;height: 200px">
              </el-image>
            </el-col>
          </el-row>
        </el-form-item>
      </div>
    </el-form>
    <!--      底部区域-->
    <span slot="footer" class="dialog-footer">
    <el-button @click="editTopicDialogVisible = false">取 消</el-button>
    <el-button type="primary"  @click="editTopic">确 定</el-button>
      </span>
  </el-dialog>
  <!-- 分配岗位对话框 -->
  <el-dialog
    title="分配岗位"
    :visible.sync="settingPostDialogVisible"
    width="40%" @close="settingPostDialogClosed"
  >
    <el-form label-width="100px">
      <el-form-item label="当前工段:">
        <el-input v-model="currentWorkSectionName" disabled style="width: 100px"></el-input>
      </el-form-item>
      <el-form-item label="产品线和岗位:">
        <el-cascader
          :options="options" v-model="selectKeys"
          :props="props"
          placeholder="请选择产品线和岗位" style="margin-bottom: 10px"
          clearable></el-cascader>
      </el-form-item>
    </el-form>
    <!--      底部区域-->
    <span slot="footer">
    <el-button @click="settingPostDialogVisible= false">取 消</el-button>
    <el-button type="primary"  @click="setPosts">确 定</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
import config from '../../util/config'
export default {
  data () {
    return {
      currentWorkSectionName: '',
      currentSetRelationsTopicId: 0,
      selectKeys: [],
      props: { multiple: true, expandTrigger: 'hover' },
      options: [],
      height: 500,
      // 获取登录日志列表的参数对象
      queryInfo: {
        // 当前的页数
        pageNum: 1,
        // 当前每页显示多少条数据
        pageSize: 5,
        // 题干信息
        topicDesc: ''
      },
      addForm: {
        topicDesc: '',
        type: undefined
      },
      editForm: {
      },
      uploadForm: {
        postIds: []
      },
      list: [],
      areasInformation: [],
      total: 0,
      uploadDialogVisible: false,
      uploadImgDialogVisible: false,
      watchImgDialogVisible: false,
      addTopicDialogVisible: false,
      editTopicDialogVisible: false,
      settingPostDialogVisible: false,
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
    this.getList()
    this.getAreasInformation()
    this.getProductLines()
    this.getCurrentWorkSection()
  },
  computed: {
    answer5Exist () {
      for (let i = 0; i < this.list.length; i++) {
        if (this.list[i].answer5 !== null) {
          return true
        }
      }
      return false
    },
    answer6Exist () {
      for (let i = 0; i < this.list.length; i++) {
        if (this.list[i].answer6 !== null) {
          return true
        }
      }
      return false
    },
    postIds () {
      return this.selectKeys.map(childArr => childArr[1])
    }
  },
  methods: {
    async getAreasInformation () {
      const { data: res } = await this.$http.get('area/list')
      if (res.status !== 200) {
        return this.$message.error('获取区域信息失败!')
      }
      this.areasInformation = res.data
    },
    async getProductLines () {
      const { data: res } = await this.$http.get('product-line/list')
      if (res.status !== 200) {
        return this.$message.error('获取所有产品线及岗位的二级分类失败!')
      }
      this.options = res.data
    },
    async getCurrentWorkSection () {
      const { data: workSectionRes } = await this.$http.get('admin/workSectionName')
      if (workSectionRes.status !== 200) {
        return this.$message.error('未查询到当前管理员所属工段')
      }
      this.currentWorkSectionName = workSectionRes.data.workSectionName
    },
    async getList () {
      const { data: res } = await this.$http.get('topic/topics', { params: this.queryInfo })
      if (res.status !== 200) {
        return this.$message.error('获取题目列表失败！')
      }
      this.list = res.data.list
      this.total = res.data.total
      console.log(res)
    },
    handleSizeChange (newSize) {
      this.queryInfo.pageSize = newSize
      this.getList()
    },
    handleCurrentChange (newPage) {
      this.queryInfo.pageNum = newPage
      this.getList()
    },
    async showUploadDialog () {
      const { data: res } = await this.$http.get('employee/isLogin')
      if (res.status !== 200) {
        return this.$message.error('您还未登录')
      }
      this.uploadDialogVisible = true
    },
    async showSettingPostDialog (topicId) {
      this.currentSetRelationsTopicId = topicId
      const { data: res } = await this.$http.get(`topic-post-relation/${topicId}`)
      if (res.status !== 200) {
        return this.$message.error('获取该题的岗位分配信息失败!')
      }
      this.selectKeys = res.data
      this.settingPostDialogVisible = true
    },
    async setPosts () {
      const params = new URLSearchParams()
      params.append('postIds', this.postIds)
      params.append('topicId', this.currentSetRelationsTopicId)
      const { data: res } = await this.$http.put('topic-post-relation/update', params)
      if (res.status !== 200) {
        return this.$message.error('更新题目与岗位之间的关系失败!')
      }
      this.$message.success('更新题目与岗位之间的关系成功!')
      this.settingPostDialogVisible = false
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
      // 清空上传文件列表
      this.$refs.uploadExcel.clearFiles()
      this.getList()
      // 清空上传excel导入题目的uploadForm
      this.selectKeys = []
    },
    settingPostDialogClosed () {
      this.selectKeys = []
    },
    beforeUpload () {
      this.uploadForm.postIds = this.postIds
      if (this.uploadForm.postIds.length === 0) {
        this.$message.error('你没有选择任何岗位!')
        return false
      }
    },
    async afterUpload (response) {
      this.$message.success({ message: response.msg, duration: 10000 })
      const { data: res } = await this.$http.get('topic/confirm')
      if (res.status === 200) {
        this.$message.warning({
          message: '本次上传有异常内容，请允许浏览器下载异常信息',
          duration: 0,
          showClose: true
        })
        window.location.href = this.BASE_REQUEST_PATH + 'exam/topic/download/exception'
      }
      this.uploadDialogVisible = false
    },
    async afterImgUpload () {
      const { data: res } = await this.$http.get(`topic/topics/${this.editForm.id}`)
      if (res.status !== 200) {
        return this.$message.error('更新当前题目图片路径失败!')
      }
      this.editForm = res.data
    },
    uploadImgDialogClosed () {
      this.getList()
    },
    addTopicDialogClosed () {
      this.$refs.addFormRef.resetFields()
    },
    editTopicDialogClosed () {
      this.getList()
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
        await this.getList()
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
        const { data: res } = await this.$http.put('topic/topics', this.editForm)
        if (res.status !== 200) {
          return this.$message.error('修改题目失败！')
        }
        this.$message.success('修改题目成功！')
        // 隐藏添加题目的对话框
        this.editTopicDialogVisible = false
        // 重新获取题目列表数据
        await this.getList()
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
      this.getList()
    },
    async topicMustChange (topic) {
      const { data: res } = await this.$http.put('topic/topics', topic)
      if (res.status !== 200) {
        topic.isMust = !topic.isMust
        return this.$message.error('更改题目必答状态失败!')
      }
      this.$message.success('更改题目必答状态成功!')
      await this.getList()
    },
    search () {
      this.queryInfo.pageNum = 1
      this.getList()
    }
  }
}
</script>

<style>
  .el-table__body tr.hover-row>td { background-color: #ffff99 !important; }
</style>
