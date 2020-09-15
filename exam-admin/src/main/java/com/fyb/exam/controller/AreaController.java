package com.fyb.exam.controller;

import com.fyb.exam.common.CommonResult;
import com.fyb.exam.entity.Area;
import com.fyb.exam.service.IAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-09-15
 */
@RestController
@RequestMapping("/exam/area")
public class AreaController {


    @Autowired
    private IAreaService areaService;

    //返回所有area信息列表
    @GetMapping("/list")
    public CommonResult<List<Area>> list(){
        List<Area> list = areaService.list();
        return CommonResult.success(list);
    }
}
