<template>
    <div>
      <!-- 面包屑导航 -->
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>生产计划安排</el-breadcrumb-item>
        <el-breadcrumb-item>已分配的计划批次</el-breadcrumb-item>
      </el-breadcrumb>
      <el-card>
        <!--        已分配计划列表-->
        <el-table :data="planItemList" border stripe height="600" >
          <el-table-column type="index"></el-table-column>
          <el-table-column label="计划批次号" prop="planNo"></el-table-column>
          <el-table-column label="产品名称" prop="name"></el-table-column>
          <el-table-column label="计划生产数量" prop="productQuantity"></el-table-column>
          <el-table-column label="开始日期" prop="startTime"></el-table-column>
        </el-table>
      </el-card>
    </div>
</template>

<script>
export default {
  created () {
    this.getPlanItemList()
  },
  data () {
    return {
      planItemList: []
    }
  },
  methods: {
    // 获取已分配计划列表
    async getPlanItemList () {
      const { data: res } = await this.$http.get('plan-item/list/assign')
      if (res.status !== 200) {
        return this.$message.error('获取已分配计划列表失败！')
      }
      this.planItemList = res.data
    }
  }
}
</script>

<style scoped>

</style>
