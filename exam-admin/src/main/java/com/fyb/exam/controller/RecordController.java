package com.fyb.exam.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyb.exam.common.CommonPage;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.dto.LoginLogPageParam;
import com.fyb.exam.entity.Record;
import com.fyb.exam.service.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-08-10
 */
@RestController
@RequestMapping("/exam/record")
public class RecordController {

    @Autowired
    IRecordService recordService;

    //分页查询
    @GetMapping("/records")
    public CommonResult<CommonPage<Record>> queryAllUsers(@Valid LoginLogPageParam pageParam){
        Page<Record> recordPage = new Page<>();
        recordPage.setSize(pageParam.getPageSize());
        recordPage.setCurrent(pageParam.getPageNum());
        IPage<Record> pageResult = recordService.page(recordPage);
        CommonPage<Record> userCommonPage = CommonPage.restPage(pageResult);
        CommonResult<CommonPage<Record>> success = CommonResult.success(userCommonPage);
        return success;
    }

}
