<template>
    <div class="login_container">
      <div class="login_box">
        <!--头像区域-->
          <div class="avatar_box">
            <img src="../assets/logo.png" alt="">
          </div>
        <!--登录表单-->
        <el-form  ref="loginFormRef" label-width="0px" class="login_form" :model="loginForm" :rules="loginFormRules">
          <el-form-item prop="username">
            <el-input prefix-icon="iconfont icon-user" v-model="loginForm.username"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input prefix-icon="iconfont icon-3702mima" v-model="loginForm.password" type="password"></el-input>
          </el-form-item>
          <el-form-item class="btns">
            <el-button type="primary" @click="onSubmit">登录</el-button>
            <el-button type="info" @click="resetForm('loginFormRef')">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
</template>

<script>
export default {
  data () {
    return {
      loginForm: {
        username: 'A0001',
        password: '123456'
      },
      loginFormRules: {
        username: [
          { required: true, message: '请输入用户名称', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入用户密码', trigger: 'blur' },
          { min: 5, max: 15, message: '长度在 5 到 15 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    onSubmit () {
      this.$refs.loginFormRef.validate((valid) => {
        if (!valid) {
          return false
        }
        this.$http.post('employee/adminLogin', this.loginForm).then((res) => {
          if (res.data.status !== 200) {
            return this.$message.error('登录失败')
          }
          this.$message.success('登录成功')
          // this.$router.push(`home?${res.data.data.id}`)
          this.$router.push('home')
        })
      })
    },
    resetForm (loginFormRef) {
      this.$refs[loginFormRef].resetFields()
    }
  }
}
</script>

<style lang="less" scoped>
  .login_container {
    background: #2b4b6b;
    height: 100%;
  }
  .login_box{
    width: 450px;
    height: 300px;
    background: #fff;
    border-radius: 3px;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%,-50%);
    .avatar_box{
      height:130px ;
      width: 130px;
      border: 1px solid #eee;
      border-radius: 50%;
      padding: 10px ;
      box-shadow:0 0 10px #ddd ;
      position: absolute;
      left: 50%;
      transform: translate(-50%,-50%);
      background: #fff;
      img{
        height: 130px;
        width: 130px;
        border-radius: 50%;
        background: #eee;
      }
     }
  }
  .login_form{
    position: absolute;
    bottom: 0;
    width: 100%;
    padding: 0 20px;
    box-sizing: border-box;
  }
  .btns{
    display: flex;
    justify-content: flex-end;
  }
</style>
