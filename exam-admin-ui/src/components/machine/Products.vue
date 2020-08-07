<template>
  <div>
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>机台与产品</el-breadcrumb-item>
      <el-breadcrumb-item>产品列表</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card>
      <!--        产品列表-->
      <el-table :data="productList" border stripe height="700">
        <el-table-column type="index"></el-table-column>
        <el-table-column label="产品编号" prop="code"></el-table-column>
        <el-table-column label="产品名称" prop="name"></el-table-column>
        <el-table-column label="产品单价/元" prop="unitPrice"></el-table-column>
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
    // 获取产品列表
    async getProductList () {
      const { data: res } = await this.$http.get('product/list')
      if (res.status !== 200) {
        return this.$message.error('获取产品列表失败！')
      }
      this.productList = res.data
    }
  }
}
</script>

<style scoped>

</style>
