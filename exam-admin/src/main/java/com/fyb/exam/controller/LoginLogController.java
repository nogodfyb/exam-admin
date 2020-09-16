package com.fyb.exam.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyb.exam.common.CommonPage;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.dto.LoginLogPageParam;
import com.fyb.exam.entity.LoginLog;
import com.fyb.exam.service.ILoginLogService;
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
@RequestMapping("/exam/login-log")
public class LoginLogController {

    @Autowired
    ILoginLogService loginLogService;

    //分页查询
    @GetMapping("/loginLogs")
    public CommonResult<CommonPage<LoginLog>> queryAllUsers(@Valid LoginLogPageParam pageParam){
        Page<LoginLog> loginLogPage = new Page<>();
        loginLogPage.setSize(pageParam.getPageSize());
        loginLogPage.setCurrent(pageParam.getPageNum());
        IPage<LoginLog> pageResult = loginLogService.page(loginLogPage);
        CommonPage<LoginLog> userCommonPage = CommonPage.resetPage(pageResult);
        CommonResult<CommonPage<LoginLog>> success = CommonResult.success(userCommonPage);
        return success;
    }

}
