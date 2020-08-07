<template>
  <div>
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>机台与产品</el-breadcrumb-item>
      <el-breadcrumb-item>机台产能</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card>
      <!--        产能列表-->
      <el-table :data="productList" border stripe height="700">
        <el-table-column type="index"></el-table-column>
        <el-table-column label="机台编号" prop="machineCode"></el-table-column>
        <el-table-column label="日平均产量" prop="dailyProduction"></el-table-column>
        <el-table-column label="机台描述" prop="description"></el-table-column>
        <el-table-column label="产品名称" prop="name"></el-table-column>
        <el-table-column label="产品单价" prop="unitPrice"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  created () {
    this.getProductList()
  },
  data () {
    return {
      productList: []
    }
  },
  methods: {
    // 获取产能列表
    async getProductList () {
      const { data: res } = await this.$http.get('productivity/list')
      if (res.status !== 200) {
        return this.$message.error('获取产能列表失败！')
      }
      this.productList = res.data
    }
  }
}
</script>

<style scoped>

</style>
