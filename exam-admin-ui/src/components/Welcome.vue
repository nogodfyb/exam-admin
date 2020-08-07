<template>
    <div>
      <h3>欢迎来到我的小作品！请点击左边菜单栏开始浏览！</h3>
    </div>
</template>

<script>
export default {
  created () {
    const href = window.location.href
    const index = href.indexOf('?') + 1
    console.log(href.substring(index))
  }
}
</script>

<style scoped>

</style>
