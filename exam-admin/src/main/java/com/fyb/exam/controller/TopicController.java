package com.fyb.exam.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyb.exam.common.CommonPage;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.dto.LoginLogPageParam;
import com.fyb.exam.entity.Image;
import com.fyb.exam.entity.Topic;
import com.fyb.exam.listener.UploadTopicListener;
import com.fyb.exam.service.IImageService;
import com.fyb.exam.service.ITopicService;
import com.fyb.exam.vo.TopicExcelVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TopicController.class);
    @Autowired
    ITopicService topicService;
    @Autowired
    IImageService imageService;

    //分页查询
    @GetMapping("/topics")
    public CommonResult<CommonPage<Topic>> queryAllUsers(@Valid LoginLogPageParam pageParam){
        Page<Topic> topicPage = new Page<>();
        topicPage.setSize(pageParam.getPageSize());
        topicPage.setCurrent(pageParam.getPageNum());
        //构造条件
        QueryWrapper<Topic> topicQueryWrapper = new QueryWrapper<>();
        topicQueryWrapper.eq("is_deleted",false);
        IPage<Topic> pageResult = topicService.page(topicPage,topicQueryWrapper);
        CommonPage<Topic> userCommonPage = CommonPage.restPage(pageResult);
        CommonResult<CommonPage<Topic>> success = CommonResult.success(userCommonPage);
        return success;
    }
    //更新题目
    @PutMapping("/topics/{id}")
    public CommonResult<Object> updateTopic(@RequestBody Topic topic,@PathVariable Integer id){
        topic.setUpdateTime(LocalDateTime.now());
        boolean update = topicService.updateById(topic);
        return  update?CommonResult.success(null):CommonResult.failed();
    }
    //下载模板
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
    //批量上传题目
    @PostMapping("upload")
    @ResponseBody
    public String upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), TopicExcelVo.class, new UploadTopicListener(topicService)).sheet().doRead();
        return "success";
    }

    //上传题干附件图片
    @PostMapping("uploadImage/{id}")
    public String uploadImageForWebApp(MultipartFile file, @PathVariable Integer id) {
        if (file.isEmpty()) {
            return "file is empty";
        }
        try {
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            //根据id来命名图片
            String fileName=id+originalFilename.substring(originalFilename.indexOf("."));
            Path path = Paths.get("D:/imgs/" + fileName);
            Files.write(path, bytes);
            //上传成功之后
            Image image = new Image();
            image.setTopicId(id);
            image.setFileName(fileName);
            image.setCreateTime(LocalDateTime.now());
            image.setUpdateTime(LocalDateTime.now());
            imageService.save(image);
            //更新topic
            Topic topic = new Topic();
            topic.setId(id);
            topic.setImageName(fileName);
            topic.setUpdateTime(LocalDateTime.now());
            topicService.updateById(topic);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "file is upload";
    }

    //上传题目选项附件图片
    @PostMapping("uploadImageItem/{id}")
    public String uploadImage(MultipartFile file, @PathVariable Integer id,String select) {
        if (file.isEmpty()) {
            return "file is empty";
        }
        try {
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            //根据id来命名图片
            //版本号随机
            int version=(int )(Math.random() * 10000);
            String fileName=id+"_"+select+version+originalFilename.substring(originalFilename.indexOf("."));
            Path path = Paths.get("D:/imgs/" + fileName);
            Files.write(path, bytes);
            //上传成功之后
            Image image = new Image();
            Topic topic = new Topic();
            if ("A".equals(select)) {
                topic.setAnswer1(fileName);
                image.setAnswerId(1);
            }
            if ("B".equals(select)) {
                topic.setAnswer2(fileName);
                image.setAnswerId(2);
            }
            if ("C".equals(select)) {
                topic.setAnswer3(fileName);
                image.setAnswerId(3);
            }
            if ("D".equals(select)) {
                topic.setAnswer4(fileName);
                image.setAnswerId(4);
            }
            if ("E".equals(select)) {
                topic.setAnswer5(fileName);
                image.setAnswerId(5);
            }
            if ("F".equals(select)) {
                topic.setAnswer6(fileName);
                image.setAnswerId(6);
            }
            image.setTopicId(id);
            image.setFileName(fileName);
            image.setCreateTime(LocalDateTime.now());
            image.setUpdateTime(LocalDateTime.now());
            imageService.save(image);
            //更新topic
            topic.setId(id);
            topic.setUpdateTime(LocalDateTime.now());
            topicService.updateById(topic);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "file is upload";
    }

    //添加选项仅为图片的题目
    @PostMapping("/add")
    public CommonResult<Object> add (@RequestBody Topic topic){
        topic.setIsGraphic(true);
        topic.setCreateTime(LocalDateTime.now());
        topic.setUpdateTime(LocalDateTime.now());
        boolean save = topicService.save(topic);
        return save?CommonResult.success(null):CommonResult.failed();
    }

    //删除题目
    @DeleteMapping("/topics/{id}")
    public CommonResult<Object> delete(@PathVariable Integer id){
        Topic topic = new Topic();
        topic.setId(id);
        topic.setIsDeleted(true);
        boolean update = topicService.updateById(topic);
        return update?CommonResult.success(null):CommonResult.failed();
    }
}
