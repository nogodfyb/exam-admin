<template>
    <div>
      <!-- 面包屑导航 -->
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>机台与产品</el-breadcrumb-item>
        <el-breadcrumb-item>机台列表</el-breadcrumb-item>
      </el-breadcrumb>
      <el-card>
        <!--        机台列表-->
        <el-table :data="machineList" border stripe height="700">
          <el-table-column type="index"></el-table-column>
          <el-table-column label="机台ID" prop="id"></el-table-column>
          <el-table-column label="机台编号" prop="code"></el-table-column>
          <el-table-column label="机台描述" prop="description"></el-table-column>
        </el-table>
      </el-card>
    </div>
</template>

<script>
export default {
  created () {
    this.getMachineList()
  },
  data () {
    return {
      machineList: []
    }
  },
  methods: {
    // 获取机台列表
    async getMachineList () {
      const { data: res } = await this.$http.get('machine/list')
      if (res.status !== 200) {
        return this.$message.error('获取机台列表失败！')
      }
      this.machineList = res.data
    }
  }
}
</script>

<style scoped>

</style>
