<template>
  <div>
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>生产计划安排</el-breadcrumb-item>
      <el-breadcrumb-item>计划批次列表</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card>
      <el-button type="primary" @click="addDialogVisible=true">添加计划</el-button>
      <p>注意：计划批次的计划产量不得小于当天产能!</p>
      <!--        计划列表-->
      <el-table :data="planItemList" border stripe height="600" @selection-change="changeFun">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column type="index"></el-table-column>
        <el-table-column label="计划批次号" prop="planNo"></el-table-column>
        <el-table-column label="产品名称" prop="name"></el-table-column>
        <el-table-column label="计划生产数量" prop="productQuantity"></el-table-column>
        <el-table-column label="开始日期" prop="startTime"></el-table-column>
      </el-table>
      <el-button type="success" @click="generateProductionPlans" style="margin-top: 15px">分配各机台生产计划</el-button>
    </el-card>
<!--    添加计划对话框-->
    <el-dialog
      :visible.sync="addDialogVisible"
    width="50%"
    title="添加计划"
    @close="addPlanDialogClosed"
    :close-on-click-modal="false"
    :close-on-press-escape="false">
<!--      主体区域-->
      <el-form label-width="100px">
        <el-form-item label="计划批次号" >
          <el-select v-model="planForm.planNo" placeholder="请选择">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="产品编号" >
          <el-select v-model="planForm.productCode" placeholder="请选择">
            <el-option
              v-for="item in productionOptions"
              :key="item.productId"
              :label="item.label"
              :value="item.productId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="计划生产数量" >
          <el-input v-model.number="planForm.productQuantity" style="width: 217px"></el-input>
        </el-form-item>
        <el-form-item label="起始时间" >
          <el-date-picker
            v-model="planForm.startTime"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <!--      底部区域-->
      <span slot="footer" class="dialog-footer">
    <el-button @click="addDialogVisible = false">取 消</el-button>
    <el-button type="primary"  @click="addPlan">确 定</el-button>
      </span>
    </el-dialog>
<!--    生成计划对话框-->
    <el-dialog :visible.sync="generateDialogVisible" width="12%" :show-close="false"
               :close-on-click-modal="false" :close-on-press-escape="false" @close="generateDialogClosed">
      <el-progress type="circle" :percentage="currentPercentage" ></el-progress>
    </el-dialog>
  </div>
</template>

<script>
export default {
  created () {
    this.getPlanItemList()
  },
  data () {
    return {
      planItemList: [],
      addDialogVisible: false,
      generateDialogVisible: false,
      currentPercentage: 20,
      options: [
        { value: '202005', label: '202005' },
        { value: '202006', label: '202006' },
        { value: '202007', label: '202007' }
      ],
      planForm: {
        planNo: undefined,
        productCode: undefined,
        productQuantity: undefined,
        startTime: undefined
      },
      productionOptions: [
        { productId: 1001, label: '天府RD-109' },
        { productId: 1002, label: '天府RD-110A' },
        { productId: 1003, label: '天府RD-123' }
      ],
      selectedPlanItemIds: []
    }
  },
  methods: {
    // 获取计划列表
    async getPlanItemList () {
      const { data: res } = await this.$http.get('plan-item/list')
      if (res.status !== 200) {
        return this.$message.error('获取计划列表失败！')
      }
      this.planItemList = res.data
    },
    addPlanDialogClosed () {
      this.planForm = {}
    },
    // 添加计划
    async addPlan () {
      const { data: res } = await this.$http.post('plan-item/add', this.planForm)
      if (res.status !== 200) {
        return this.$message.error('添加计划失败！选择的日期不能小于当天！')
      }
      this.addDialogVisible = false
      this.getPlanItemList()
    },
    // 选项变化重置数据
    changeFun (array) {
      this.selectedPlanItemIds = array.map((item) => {
        return item.id
      })
      console.log(this.selectedPlanItemIds)
    },
    // 生成机台生成计划
    async generateProductionPlans () {
      if (this.selectedPlanItemIds.length === 0) {
        return this.$message.error('您没有选中任何计划！')
      }
      this.generateDialogVisible = true
      const { data: res } = await this.$http.post('plan-item/generateProductionPlans', this.selectedPlanItemIds)
      if (res.status !== 200) {
        this.generateDialogVisible = false
        return this.$message.error('操作失败，计划批次的时间顺序不能为逆序！')
      }
      this.currentPercentage = 100
      this.generateDialogVisible = false
      this.$message.success('已经分配好生产计划！请到机台生产计划列表查看')
      this.getPlanItemList()
    },
    generateDialogClosed () {
      this.currentPercentage = 20
    }
  }
}
</script>

<style scoped>

</style>
