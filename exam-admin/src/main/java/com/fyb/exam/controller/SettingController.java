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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Long singleCount = getMinSize(Const.SINGLE_TYPE);
        if(setting.getSingleCount()<=0||setting.getSingleCount()>singleCount){
            return CommonResult.failed("单选题数量不合法,最大允许数量:"+singleCount);
        }
        // 判断题数量配置是否合理
        Long judgeCount = getMinSize(Const.JUDGE_TYPE);
        if(setting.getJudgeCount()<=0||setting.getJudgeCount()>judgeCount){
            return CommonResult.failed("判断题数量不合法,最大允许数量:"+judgeCount);
        }
        // 多选题数量配置是否合理
        Long multipleCount = getMinSize(Const.MULTIPLE_TYPE);
        if(setting.getMultipleCount()<=0||setting.getMultipleCount()>multipleCount){
            return CommonResult.failed("多选题数量不合法,最大允许数量:"+multipleCount);
        }
        boolean update = settingService.updateById(setting);
        return  update?CommonResult.success(null):CommonResult.failed();
    }

    public Long getMinSize(Integer type){
        QueryWrapper<Topic> topicQueryWrapper = new QueryWrapper<>();
        topicQueryWrapper.select("area_id","count(*)").groupBy("area_id").eq("type",type).eq("is_deleted",0);
        List<Map<String, Object>> maps = topicService.listMaps(topicQueryWrapper);
        List<Long> collect = maps.stream().map(map -> (Long)map.get("count(*)")).collect(Collectors.toList());
        Collections.sort(collect);
        return collect.get(0);
    }


}
