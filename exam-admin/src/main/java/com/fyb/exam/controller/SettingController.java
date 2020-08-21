package com.fyb.exam.controller;


import com.fyb.exam.common.CommonResult;
import com.fyb.exam.entity.Setting;
import com.fyb.exam.service.ISettingService;
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
        boolean update = settingService.updateById(setting);
        return  update?CommonResult.success(null):CommonResult.failed();
    }
}
