<template>
  <div>
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>生产计划安排</el-breadcrumb-item>
      <el-breadcrumb-item>机台生产计划列表</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card>
      <el-date-picker
        v-model="queryInfo.productionDate"
        type="date"
        placeholder="选择日期"
        value-format="yyyy-MM-dd"
      @change="selectDate">
      </el-date-picker>
      <el-select v-model="queryInfo.planNo" placeholder="请选择计划批次号"
                 style="margin-left: 20px" @change="selectPlanNo" clearable>
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <!--        机台计划列表-->
      <el-table :data="machinePlanList" border stripe show-summary :summary-method="getSum">
        <el-table-column type="index"></el-table-column>
        <el-table-column label="机台编码" prop="machineCode"></el-table-column>
        <el-table-column label="计划批次号" prop="planNo"></el-table-column>
        <el-table-column label="生产日期" prop="productionDate"></el-table-column>
        <el-table-column label="生产班次" prop="shift"></el-table-column>
        <el-table-column label="产品编码" prop="productCode"></el-table-column>
        <el-table-column label="计划产量" prop="scheduledProduction"></el-table-column>
      </el-table>
      <!--    分页区域-->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryInfo.pageNum"
        :page-sizes="[5, 10, 15, 20, total]"
        :page-size="queryInfo.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
export default {
  created () {
    this.getMachinePlanList()
  },
  data () {
    return {
      machinePlanList: [],
      // 获取机台计划列表的参数对象
      queryInfo: {
        // 查询参数
        productionDate: '',
        planNo: '',
        // 当前的页数
        pageNum: 1,
        // 当前每页显示多少条数据
        pageSize: 10
      },
      total: 0,
      options: [
        { value: '202005', label: '202005' },
        { value: '202006', label: '202006' },
        { value: '202007', label: '202007' }
      ]
    }
  },
  methods: {
    // 获取机台列表
    async getMachinePlanList () {
      const { data: res } = await this.$http.get('machine-plan/list', { params: this.queryInfo })
      if (res.status !== 200) {
        return this.$message.error('获取机台计划列表失败！')
      }
      this.machinePlanList = res.data.list
      this.total = res.data.total
    },
    handleSizeChange (newSize) {
      this.queryInfo.pageSize = newSize
      this.getMachinePlanList()
    },
    handleCurrentChange (newPage) {
      this.queryInfo.pageNum = newPage
      this.getMachinePlanList()
    },
    selectDate () {
      this.getMachinePlanList()
    },
    selectPlanNo () {
      this.getMachinePlanList()
    },
    getSum (param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '合计'
          return
        }
        if (index === 6) {
          const values = data.map(item => Number(item[column.property]))
          if (!values.every(value => isNaN(value))) {
            sums[index] = values.reduce((prev, curr) => {
              const value = Number(curr)
              if (!isNaN(value)) {
                return prev + curr
              } else {
                return prev
              }
            }, 0)
            sums[index] += ' 条'
          } else {
            sums[index] = 'N/A'
          }
        }
      })
      return sums
    }
  }
}
</script>

<style scoped>

</style>
