<template>
  <div>
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>题目管理</el-breadcrumb-item>
      <el-breadcrumb-item>题型数量配置</el-breadcrumb-item>
    </el-breadcrumb>
    <el-form ref="form" :model="currentConfig" label-width="100px" style="width: 300px;height: 300px;margin-left: 100px;margin-top: 100px">
      <el-form-item label="单选题数量" >
        <el-input v-model="currentConfig.singleCount"></el-input>
      </el-form-item>
      <el-form-item label="判断题数量" >
        <el-input v-model="currentConfig.judgeCount"></el-input>
      </el-form-item>
      <el-form-item label="多选题数量" >
        <el-input v-model="currentConfig.multipleCount"></el-input>
      </el-form-item>
      <el-button type="primary"  @click="saveSetting" style="margin-left: 100px">保存配置</el-button>
    </el-form>
  </div>
</template>

<script>
export default {
  data () {
    return {
      currentConfig: {}
    }
  },
  created () {
    this.getConfig()
  },
  methods: {
    async getConfig () {
      const { data: res } = await this.$http.get('setting/current')
      if (res.status !== 200) {
        return this.$message.error('获取配置信息失败！')
      }
      this.currentConfig = res.data
    },
    async saveSetting () {
      const { data: res } = await this.$http.put('setting/current', this.currentConfig)
      if (res.status !== 200) {
        return this.$message.error(res.msg)
      }
      this.$message.success('修改配置信息成功!')
      await this.getConfig()
    }
  }
}
</script>

<style scoped>

</style>
