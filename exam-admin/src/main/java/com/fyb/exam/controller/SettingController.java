package com.fyb.exam.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.common.Const;
import com.fyb.exam.entity.Setting;
import com.fyb.exam.entity.Topic;
import com.fyb.exam.service.ISettingService;
import com.fyb.exam.service.ITopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-08-21
 */
@RestController
@RequestMapping("/exam/setting")
public class SettingController {

    @Autowired
    private ISettingService settingService;

    @Autowired
    private ITopicService topicService;

    //获取当前配置信息
    @GetMapping("current")
    public CommonResult<Setting> getCurrentSetting(){
        Setting setting = settingService.getById(1);
        if (setting!=null) {
            return CommonResult.success(setting);
        }else return CommonResult.failed();
    }

    //修改当前配置
    @PutMapping("current")
    public CommonResult<Object> updateCurrentSetting(@RequestBody Setting setting){
        // 单选题数量配置是否合理
        QueryWrapper<Topic> topicQueryWrapper = new QueryWrapper<>();
        topicQueryWrapper.eq("type",Const.SINGLE_TYPE);
        int singleCount = topicService.count(topicQueryWrapper);
        if(setting.getSingleCount()<=0||setting.getSingleCount()>singleCount){
            return CommonResult.failed("单选题数量不合法,最大允许数量:"+singleCount);
        }
        // 判断题数量配置是否合理
        QueryWrapper<Topic> topicQueryWrapper2 = new QueryWrapper<>();
        topicQueryWrapper2.eq("type",Const.JUDGE_TYPE);
        int judgeCount = topicService.count(topicQueryWrapper2);
        if(setting.getJudgeCount()<=0||setting.getJudgeCount()>judgeCount){
            return CommonResult.failed("判断题数量不合法,最大允许数量:"+judgeCount);
        }
        // 多选题数量配置是否合理
        QueryWrapper<Topic> topicQueryWrapper3 = new QueryWrapper<>();
        topicQueryWrapper3.eq("type",Const.MULTIPLE_TYPE);
        int multipleCount = topicService.count(topicQueryWrapper3);
        if(setting.getMultipleCount()<=0||setting.getMultipleCount()>multipleCount){
            return CommonResult.failed("多选题数量不合法,最大允许数量:"+multipleCount);
        }
        boolean update = settingService.updateById(setting);
        return  update?CommonResult.success(null):CommonResult.failed();
    }



}
