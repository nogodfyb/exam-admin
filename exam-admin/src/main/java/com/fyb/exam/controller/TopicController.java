package com.fyb.exam.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyb.exam.common.CommonPage;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.dto.LoginLogPageParam;
import com.fyb.exam.entity.Topic;
import com.fyb.exam.listener.UploadTopicListener;
import com.fyb.exam.service.ITopicService;
import com.fyb.exam.vo.TopicExcelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-08-10
 */
@RestController
@RequestMapping("/exam/topic")
public class TopicController {
    @Autowired
    ITopicService topicService;

    //分页查询
    @GetMapping("/topics")
    public CommonResult<CommonPage<Topic>> queryAllUsers(@Valid LoginLogPageParam pageParam){
        Page<Topic> topicPage = new Page<>();
        topicPage.setSize(pageParam.getPageSize());
        topicPage.setCurrent(pageParam.getPageNum());
        IPage<Topic> pageResult = topicService.page(topicPage);
        CommonPage<Topic> userCommonPage = CommonPage.restPage(pageResult);
        CommonResult<CommonPage<Topic>> success = CommonResult.success(userCommonPage);
        return success;
    }
    @GetMapping("download")
    public void download(HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("导入题库的模板", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), TopicExcelVo.class).sheet("模板").doWrite(new ArrayList());
    }
    @PostMapping("upload")
    @ResponseBody
    public String upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), TopicExcelVo.class, new UploadTopicListener(topicService)).sheet().doRead();
        return "success";
    }
}
