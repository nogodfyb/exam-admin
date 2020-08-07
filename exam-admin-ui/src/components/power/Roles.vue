<template>
    <div>
      <!-- 面包屑导航 -->
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>权限管理</el-breadcrumb-item>
        <el-breadcrumb-item>角色列表</el-breadcrumb-item>
      </el-breadcrumb>
      <!-- 卡片视图 -->
      <el-card>
        <!-- 添加角色按钮区域 -->
        <el-row>
          <el-col>
            <el-button type="primary" @click="addRole">添加角色</el-button>
          </el-col>
        </el-row>
        <!--        角色列表-->
        <el-table :data="roleList" border stripe>
<!--          展开列-->
          <el-table-column type="expand">
            <template slot-scope="scope">
              <el-row :class="['bdbottom',index1 === 0 ? 'bdtop' : '','vcenter']" v-for="(item1,index1) in scope.row.children" :key="item1.id">
<!--                渲染一级权限-->
                <el-col :span="5">
                  <el-tag>{{item1.authName}}</el-tag>
                  <i class="el-icon-caret-right"></i>
                </el-col>
<!--                渲染二级和三级权限-->
                <el-col :span="19">
<!--                 循环嵌套二级权限 -->
                  <el-row :class="[index2 === 0 ? '' : 'bdtop','vcenter']" v-for="(item2,index2) in item1.children" :key="item2.id" >
                    <el-col :span="6" >
                      <el-tag type="success">{{item2.authName}}</el-tag>
                      <i class="el-icon-caret-right"></i>
                    </el-col>
                    <el-col :span="18">
<!--                      循环嵌套三级权限-->
                        <el-tag type="warning" v-for="item3 in item2.children" :key="item3.id " closable
                        @close="removeRightById">
                          {{item3.authName}}
                        </el-tag>
                    </el-col>
                  </el-row>
                </el-col>
              </el-row>
            </template>
          </el-table-column>
<!--          索引列-->
          <el-table-column type="index"></el-table-column>
          <el-table-column label="角色名称" prop="roleName"></el-table-column>
          <el-table-column label="角色描述" prop="roleDesc"></el-table-column>
          <el-table-column label="操作" >
            <template slot-scope="scope">
              <el-button type="primary" icon="el-icon-edit" size="mini" @click="editRole" >编辑</el-button>
              <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteRole" >删除</el-button>
              <el-tooltip  effect="dark" content="分配权限" placement="top-start" :enterable="false">
                <el-button type="warning" icon="el-icon-setting" size="mini" @click="showSetRightDialog(scope.row)">
                  分配权限
                </el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <el-dialog
        title="分配权限"
        :visible.sync="setRightDialogVisible"
        width="30%"
        @close="setRightDialogClosed"
        >
<!--        树形控件-->
        <el-tree :data="rightsList" :props="treeProps" show-checkbox node-key="id" default-expand-all
        :default-checked-keys="defKeys" ref="treeRef">
        </el-tree>
        <span slot="footer" class="dialog-footer">
    <el-button @click="setRightDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="allotRights" >确 定</el-button>
  </span>
      </el-dialog>
    </div>
</template>

<script>
export default {
  data () {
    return {
      // 所有角色列表数据
      roleList: [],
      // 控制分配权限对话框的显示与隐藏
      setRightDialogVisible: false,
      // 所有权限的数据
      rightsList: [],
      // 树形控件的属性绑定对象
      treeProps: {
        label: 'authName',
        children: 'children'
      },
      // 默认选中的节点Id值数组
      defKeys: [],
      // 当前即将分配权限的角色id
      roleId: ''
    }
  },
  created () {
    this.getRolesList()
  },
  methods: {
  // 获取所有角色的列表
    async getRolesList () {
      const { data: res } = await this.$http.get('role/roles')

      if (res.status !== 200) {
        return this.$message.error('获取角色列表失败！')
      }
      this.roleList = res.data
      console.log(this.roleList)
    },
    removeRightById () {
      this.$confirm('此操作将永久删除该权限, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log(123)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    async showSetRightDialog (role) {
      this.roleId = role.roleId
      const { data: res } = await this.$http.get('rights/tree')
      if (res.status !== 200) {
        return this.$message.error('获取权限数据失败！')
      }
      // 把获取到的权限数据保存到 data 中
      this.rightsList = res.data
      this.getLeafKeys(role, this.defKeys)
      this.setRightDialogVisible = true
    },
    // 通过递归的形式，获取角色下所有三级权限的id，并保存到 defKeys 数组中
    getLeafKeys (node, arr) {
      // 如果当前 node 节点不包含 children 属性，则是三级节点
      if (!node.children) {
        return arr.push(node.id)
      }
      node.children.forEach(item => this.getLeafKeys(item, arr))
    },
    // 关闭后清空被选中的三级权限id
    setRightDialogClosed () {
      this.defKeys = []
      console.log(this.defKeys)
    },
    // 点击为角色分配权限
    allotRights () {
      const keys = [
        ...this.$refs.treeRef.getCheckedKeys(),
        ...this.$refs.treeRef.getHalfCheckedKeys()
      ]
      console.log('当前需要分配权限的角色id:' + this.roleId)
      console.log(keys)
      this.$message.success('收到您点中的所有权限id:[' + keys + ']但目前不做更新')
    },
    addRole () {
      return this.$message.error('暂不开放添加！')
    },
    editRole () {
      return this.$message.error('暂不开放编辑！')
    },
    deleteRole () {
      return this.$message.error('暂不开放删除！')
    }

  }
}
</script>

<style lang="less" scoped>
  .el-tag{
    margin: 7px;
  }
  .bdtop {
    border-top: 1px solid #eee;
  }
  .bdbottom {
    border-bottom: 1px solid #eee;
  }
  .vcenter{
    display: flex;
    align-items: center;
  }
</style>
