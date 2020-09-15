<template>
<div>
    <!-- 面包屑导航 -->
  <el-breadcrumb separator-class="el-icon-arrow-right">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    <el-breadcrumb-item>管理员列表</el-breadcrumb-item>
  </el-breadcrumb>
    <!-- 卡片视图 -->
  <el-card>
<!-- 搜索与添加区域  -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-input placeholder="请输入内容" v-model="queryInfo.query" clearable @clear="getList">
        <el-button slot="append" icon="el-icon-search" @click="getList"></el-button>
      </el-input>
      </el-col>
      <el-col :span="3">
        <el-button type="primary" @click="dialogVisible=true">添加管理员</el-button>
      </el-col>
    </el-row>
    <!-- 用户列表区域 -->
    <el-table :data="list" border stripe :height="height">
      <el-table-column type="index"></el-table-column>
      <el-table-column label="工号" prop="userName"></el-table-column>
      <el-table-column label="创建时间" prop="createTime"></el-table-column>
      <el-table-column label="更新时间" prop="updateTime"></el-table-column>
      <el-table-column label="操作" width="200px">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row.id)">编辑</el-button>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteUser(scope.row.id)">删除</el-button>
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
  <!--    添加用户的对话框-->
  <el-dialog
    title="添加用户"
    :visible.sync="dialogVisible"
    width="50%" @close="addDialogClosed"
  >
    <!--      内容主体区域-->
    <el-form :model="addForm" :rules="addFormRules" ref="addFormRef" label-width="70px" >
      <el-form-item label="工号" prop="userName">
        <el-input v-model="addForm.userName"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="addForm.password"></el-input>
      </el-form-item>
      <el-form-item label="区域" prop="areaId">
        <el-select v-model="addForm.areaId" placeholder="请选择区域">
          <el-option v-for="item in areasInformation" :key="item.id" :label="item.areaName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <!--      底部区域-->
    <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary"  @click="addUser">确 定</el-button>
      </span>
  </el-dialog>
  <!--    修改用户的对话框-->
  <el-dialog
    title="修改用户"
    :visible.sync="editDialogVisible"
    width="50%" @close="editDialogClosed"
  >
    <!--      内容主体区域-->
    <el-form :model="editForm" :rules="addFormRules" ref="editFormRef" label-width="70px" >
      <el-form-item label="工号" >
        <el-input v-model="editForm.userName" disabled></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="editForm.password"></el-input>
      </el-form-item>
      <el-form-item label="区域" prop="areaId">
        <el-select v-model="editForm.areaId" placeholder="请选择区域">
          <el-option v-for="item in areasInformation" :key="item.id" :label="item.areaName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <!--      底部区域-->
    <span slot="footer" class="dialog-footer">
    <el-button @click="editDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="editUser" >确 定</el-button>
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
      BASE_REQUEST_PATH: config.BASE_REQUEST_PATH,
      // 获取用户列表的参数对象
      queryInfo: {
        query: '',
        // 当前的页数
        pageNum: 1,
        // 当前每页显示多少条数据
        pageSize: 5
      },
      list: [],
      areasInformation: [],
      total: 0,
      // 用户对话框的可见性
      dialogVisible: false,
      editDialogVisible: false,
      addForm: {
        userName: '',
        password: '',
        areaId: ''
      },
      editForm: {},
      addFormRules: {
        userName: [
          { required: true, message: '请输入工号', trigger: 'blur' },
          { min: 5, max: 6, message: '长度在 5 到 6 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入用户密码', trigger: 'blur' },
          { min: 5, max: 15, message: '长度在 5 到 15 个字符', trigger: 'blur' }
        ],
        areaId: [
          { required: true, message: '请选择区域', trigger: 'blur' }
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
  },
  methods: {
    async getAreasInformation () {
      const { data: res } = await this.$http.get('area/list')
      if (res.status !== 200) {
        return this.$message.error('获取区域信息失败!')
      }
      this.areasInformation = res.data
    },
    async getList () {
      const { data: res } = await this.$http.get('admin/users', { params: this.queryInfo })
      if (res.status !== 200) {
        return this.$message.error('获取用户列表失败！')
      }
      this.list = res.data.list
      this.total = res.data.total
    },
    handleSizeChange (newSize) {
      this.queryInfo.pageSize = newSize
      this.getList()
    },
    handleCurrentChange (newPage) {
      this.queryInfo.pageNum = newPage
      this.getList()
    },
    addDialogClosed () {
      // 不需要自己清空数据 以下不仅清空了addForm的数据并且清空了验证遗留信息
      this.$refs.addFormRef.resetFields()
    },
    addUser () {
      this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) {
          return false
        }
        // 可以发起添加用户的网络请求
        const { data: res } = await this.$http.post('admin/add', this.addForm)
        if (res.status !== 200) {
          return this.$message.error('添加用户失败！')
        }
        this.$message.success('添加用户成功！')
        // 隐藏添加用户的对话框
        this.dialogVisible = false
        // 重新获取用户列表数据
        this.getList()
      })
    },
    async showEditDialog (id) {
      this.editDialogVisible = true
      const { data: res } = await this.$http.get(`admin/${id}`)
      if (res.status !== 200) {
        this.$message.error('获取用户信息失败')
        return false
      }
      this.editForm = res.data
    },
    editDialogClosed () {
      this.$refs.editFormRef.resetFields()
    },
    editUser () {
      this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) {
          return false
        }
        // 可以发起修改用户的网络请求
        const { data: res } = await this.$http.put('admin/admins', this.editForm)
        if (res.status !== 201) {
          this.$message.error('修改用户失败！')
        }
        this.$message.success('修改用户成功！')
        // 隐藏修改用户的对话框
        this.editDialogVisible = false
        // 重新获取用户列表数据
        this.getList()
      })
    },
    deleteUser (id) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 暂不开启删除功能
        return this.$message.error('暂不开启删除功能')
        // this.deleteUserById(id)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    async deleteUserById (userId) {
      // 可以发起修改用户的网络请求
      const { data: res } = await this.$http.delete(`user/users/${userId}`)
      if (res.status !== 200) {
        return this.$message.error('删除失败')
      }
      this.$message({
        type: 'success',
        message: '删除成功!'
      })
      this.getList()
    }
  }
}
</script>

<style scoped>

</style>
