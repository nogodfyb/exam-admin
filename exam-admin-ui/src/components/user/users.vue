<template>
<div>
    <!-- 面包屑导航 -->
  <el-breadcrumb separator-class="el-icon-arrow-right">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    <el-breadcrumb-item>用户列表</el-breadcrumb-item>
  </el-breadcrumb>
    <!-- 卡片视图 -->
  <el-card>
<!-- 搜索与添加区域  -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-input placeholder="请输入内容" v-model="queryInfo.query" clearable @clear="getUserList">
        <el-button slot="append" icon="el-icon-search" @click="getUserList"></el-button>
      </el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="dialogVisible=true">添加用户</el-button>
      </el-col>
    </el-row>
    <!-- 用户列表区域 -->
    <el-table :data="userList" border stripe height="500">
      <el-table-column type="index"></el-table-column>
      <el-table-column label="姓名" prop="username"></el-table-column>
      <el-table-column label="邮箱" prop="email"></el-table-column>
      <el-table-column label="电话" prop="mobile"></el-table-column>
      <el-table-column label="角色" prop="roleName"></el-table-column>
      <el-table-column label="状态(是否开启)" >
        <template slot-scope="scope">
          <el-switch v-model="scope.row.isAlive" @change="userStateChanged(scope.row)">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300px">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row.id)">编辑</el-button>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteUser(scope.row.id)">删除</el-button>
          <el-tooltip  effect="dark" content="分配角色" placement="top-start" :enterable="false">
            <el-button type="warning" icon="el-icon-setting" size="mini" @click="setRole(scope.row)">分配角色</el-button>
          </el-tooltip>
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
      <el-form-item label="用户名" prop="username">
        <el-input v-model="addForm.username"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="addForm.password"></el-input>
      </el-form-item>
      <el-form-item label="手机" prop="mobile">
        <el-input v-model="addForm.mobile"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="addForm.email"></el-input>
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
    <el-form :model="editForm" :rules="editFormRules" ref="editFormRef" label-width="70px" >
      <el-form-item label="用户名" >
        <el-input v-model="editForm.username" disabled></el-input>
      </el-form-item>
      <el-form-item label="手机" prop="mobile">
        <el-input v-model="editForm.mobile"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="editForm.email"></el-input>
      </el-form-item>
    </el-form>
    <!--      底部区域-->
    <span slot="footer" class="dialog-footer">
    <el-button @click="editDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="editUser" >确 定</el-button>
      </span>
  </el-dialog>
<!--  分配角色的对话框-->
  <el-dialog
    title="提示"
    :visible.sync="setRoleDialogVisible"
    width="50%"
    @close="setRoleDialogClosed">
    <div>
      <p>当前的用户：{{userInfo.username}}</p>
      <p>当前的角色：{{userInfo.roleName}}</p>
      <p>分配新角色:
        <el-select v-model="selectedId" placeholder="请选择" >
          <el-option
            v-for="item in rolesList"
            :key="item.roleId"
            :label="item.roleName"
            :value="item.roleId">
          </el-option>
        </el-select></p>
    </div>
    <span slot="footer" class="dialog-footer">
    <el-button @click="setRoleDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="saveRoleInfo">确 定</el-button>
  </span>
  </el-dialog>
</div>
</template>

<script>
export default {
  data () {
    // 验证邮箱的规则
    const checkEmail = (rule, value, cb) => {
      // 验证邮箱的正则表达式
      const regEmail = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/

      if (regEmail.test(value)) {
        // 合法的邮箱
        return cb()
      }

      cb(new Error('请输入合法的邮箱'))
    }
    // 验证手机号的规则
    const checkMobile = (rule, value, cb) => {
      // 验证手机号的正则表达式
      const regMobile = /^1[3456789]\d{9}$/

      if (regMobile.test(value)) {
        return cb()
      }

      cb(new Error('请输入合法的手机号'))
    }
    return {
      // 获取用户列表的参数对象
      queryInfo: {
        query: '',
        // 当前的页数
        pageNum: 1,
        // 当前每页显示多少条数据
        pageSize: 5
      },
      userList: [],
      total: 0,
      // 用户对话框的可见性
      dialogVisible: false,
      editDialogVisible: false,
      // 控制分配角色对话框的显示与关闭
      setRoleDialogVisible: false,
      addForm: {
        username: '',
        password: '',
        mobile: '',
        email: ''
      },
      editForm: {},
      // 需要被分配角色的用户信息
      userInfo: {},
      // 所有角色列表
      rolesList: [],
      // 已选中的roleId
      selectedId: '',
      addFormRules: {
        username: [
          { required: true, message: '请输入用户名称', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入用户密码', trigger: 'blur' },
          { min: 5, max: 15, message: '长度在 5 到 15 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { validator: checkEmail, trigger: 'blur', message: '请输入合法的邮箱' }
        ],
        mobile: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { validator: checkMobile, trigger: 'blur', message: '请输入而合法的手机号' }
        ]
      },
      editFormRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { validator: checkEmail, trigger: 'blur', message: '请输入合法的邮箱' }
        ],
        mobile: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { validator: checkMobile, trigger: 'blur', message: '请输入而合法的手机号' }
        ]
      }
    }
  },
  created () {
    this.getUserList()
  },
  methods: {
    async getUserList () {
      const { data: res } = await this.$http.get('user/users', { params: this.queryInfo })
      if (res.status !== 200) {
        return this.$message.error('获取用户列表失败！')
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
    // 监听 switch 开关状态的改变
    async userStateChanged (userInfo) {
      const { data: res } = await this.$http.put(
        `user/${userInfo.id}/state/${userInfo.isAlive}`
      )
      if (res.status !== 200) {
        userInfo.isAlive = !userInfo.isAlive
        return this.$message.error('更新用户状态失败！')
      }
      this.$message.success('更新用户状态成功！')
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
        const { data: res } = await this.$http.post('user/add', this.addForm)
        if (res.status !== 200) {
          return this.$message.error('添加用户失败！')
        }
        this.$message.success('添加用户成功！')
        // 隐藏添加用户的对话框
        this.dialogVisible = false
        // 重新获取用户列表数据
        this.getUserList()
      })
    },
    async showEditDialog (id) {
      this.editDialogVisible = true
      const { data: res } = await this.$http.get(`user/users/${id}`)
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
        const { data: res } = await this.$http.put('user/users', this.editForm)
        if (res.status !== 201) {
          this.$message.error('修改用户失败！')
        }
        this.$message.success('修改用户成功！')
        // 隐藏修改用户的对话框
        this.editDialogVisible = false
        // 重新获取用户列表数据
        this.getUserList()
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
      this.getUserList()
    },
    // 展示分配用户角色的对话框
    setRole (user) {
      this.userInfo = user
      // 获取所有角色的列表,这里没有写后端接口，模拟数据学习前端而已
      this.rolesList = [{ roleId: 101, roleName: '超级管理员' },
        { roleId: 102, roleName: '普通管理员' },
        { roleId: 103, roleName: '普通用户' }]
      this.setRoleDialogVisible = true
    },
    // 点击按钮，分配角色
    saveRoleInfo () {
      if (!this.selectedId) {
        return this.$message.error('没有选择新角色')
      }
      console.log('当前用户id:' + this.userInfo.id)
      console.log('当前选中角色id:' + this.selectedId)
      return this.$message.error('当前不开放分配角色权限！')
    },
    setRoleDialogClosed () {
      this.selectedId = ''
      this.userInfo = {}
    }
  }
}
</script>

<style scoped>

</style>
