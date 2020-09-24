package com.fyb.exam.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyb.exam.common.CommonPage;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.common.Const;
import com.fyb.exam.dto.TopicPageParam;
import com.fyb.exam.entity.AdminUser;
import com.fyb.exam.entity.Image;
import com.fyb.exam.entity.Topic;
import com.fyb.exam.listener.UploadTopicListener;
import com.fyb.exam.service.IImageService;
import com.fyb.exam.service.ITopicService;
import com.fyb.exam.vo.TopicExcelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
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
    @Autowired
    IImageService imageService;
    private Integer getCurrentWorkSectionId(HttpSession session){
        AdminUser currentUser = (AdminUser)session.getAttribute(Const.CURRENT_USER);
        return currentUser.getWorkSectionId();
    }

    private String getCurrentUserName(HttpSession session){
        AdminUser currentUser = (AdminUser)session.getAttribute(Const.CURRENT_USER);
        return currentUser.getUserName();
    }

    //分页查询,登录的后台管理员属于哪个工段，查询的题目属于那个工段
    @GetMapping("/topics")
    public CommonResult<CommonPage<Topic>> queryAllTopics(@Valid TopicPageParam pageParam, HttpSession session) {
        Page<Topic> topicPage = new Page<>();
        topicPage.setSize(pageParam.getPageSize());
        topicPage.setCurrent(pageParam.getPageNum());
        //构造条件
        QueryWrapper<Topic> topicQueryWrapper = new QueryWrapper<>();
        topicQueryWrapper.eq("is_deleted", false).eq("work_section_id",getCurrentWorkSectionId(session));
        if(!StringUtils.isEmpty(pageParam.getTopicDesc())){
            topicQueryWrapper.like("topic_desc",pageParam.getTopicDesc());
        }
        IPage<Topic> pageResult = topicService.page(topicPage, topicQueryWrapper);
        CommonPage<Topic> userCommonPage = CommonPage.resetPage(pageResult);
        CommonResult<CommonPage<Topic>> success = CommonResult.success(userCommonPage);
        return success;
    }

    //更新题目
    @PutMapping("/topics")
    public CommonResult<Object> updateTopic(@RequestBody Topic topic,HttpSession session) {
        topic.setUpdateTime(LocalDateTime.now());
        topic.setLastOperatorId(getCurrentUserName(session));
        boolean update = topicService.updateById(topic);
        return update ? CommonResult.success(null) : CommonResult.failed();
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

    //确认当前是否有异常内容
    @GetMapping("confirm")
    public CommonResult<Object> confirmException(HttpSession session){
        Object attribute = session.getAttribute(Const.CURRENT_FAIL_LIST);
        return attribute!=null?CommonResult.success(null):CommonResult.failed();
    }
    //下载异常信息
    @GetMapping("download/exception")
    public void downloadException(HttpServletResponse response,HttpSession session) throws IOException {
        List<TopicExcelVo> list= (List<TopicExcelVo>)session.getAttribute(Const.CURRENT_FAIL_LIST);
        if (list!=null) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(now.format(formatter)+"异常上传内容", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), TopicExcelVo.class).sheet("异常内容").doWrite(list);
        }
        session.setAttribute(Const.CURRENT_FAIL_LIST,null);
    }

    //批量上传题目
    @PostMapping("upload")
    @ResponseBody
    public CommonResult<Object> upload(MultipartFile file, HttpSession session,Integer[] postIds) throws IOException {
        ArrayList<TopicExcelVo> topicExcelVos = new ArrayList<>();
        UploadTopicListener uploadTopicListener = new UploadTopicListener(topicService, topicExcelVos, getCurrentUserName(session), getCurrentWorkSectionId(session),postIds);
        EasyExcel.read(file.getInputStream(), TopicExcelVo.class,
                uploadTopicListener).sheet().doRead();
        if (topicExcelVos.size()!=0) {
            session.setAttribute(Const.CURRENT_FAIL_LIST,topicExcelVos);
        }
        return CommonResult.success("上传结果：成功"+uploadTopicListener.getSuccessCount()+"条;失败"+uploadTopicListener.getFailCount()+"条");
    }

    //上传题干附件图片
    @PostMapping("uploadImage/{id}")
    public String uploadImageForWebApp(MultipartFile file, @PathVariable Integer id,HttpSession session) {
        if (file.isEmpty()) {
            return "file is empty";
        }
        try {
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            //根据id来命名图片
            String fileName = id + originalFilename.substring(originalFilename.indexOf("."));
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
            topic.setLastOperatorId(getCurrentUserName(session));
            topicService.updateById(topic);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "file is upload";
    }

    //上传题目选项附件图片
    @PostMapping("uploadImageItem/{id}")
    public String uploadImage(MultipartFile file, @PathVariable Integer id, String select,HttpSession session) {
        if (file.isEmpty()) {
            return "file is empty";
        }
        try {
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            //根据id来命名图片
            //版本号随机
            int version = (int) (Math.random() * 10000);
            String fileName = id + "_" + select + version + originalFilename.substring(originalFilename.indexOf("."));
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
            topic.setLastOperatorId(getCurrentUserName(session));
            topicService.updateById(topic);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "file is upload";
    }

    //添加选项仅为图片的题目
    @PostMapping("/add")
    public CommonResult<Object> add(@RequestBody Topic topic,HttpSession session) {
        topic.setIsGraphic(true);
        topic.setCreateTime(LocalDateTime.now());
        topic.setUpdateTime(LocalDateTime.now());
        topic.setCreatorId(getCurrentUserName(session));
        topic.setLastOperatorId(getCurrentUserName(session));
        topic.setWorkSectionId(getCurrentWorkSectionId(session));
        boolean save = topicService.save(topic);
        return save ? CommonResult.success(null) : CommonResult.failed();
    }

    //删除题目
    @DeleteMapping("/topics/{id}")
    public CommonResult<Object> delete(@PathVariable Integer id) {
        Topic topic = new Topic();
        topic.setId(id);
        topic.setIsDeleted(true);
        boolean update = topicService.updateById(topic);
        return update ? CommonResult.success(null) : CommonResult.failed();
    }
    //根据id查询题目
    @GetMapping("/topics/{id}")
    public CommonResult<Topic> selectTopicById(@PathVariable Integer id){
        Topic topic = topicService.getById(id);
        return topic==null?CommonResult.failed():CommonResult.success(topic);
    }

}
