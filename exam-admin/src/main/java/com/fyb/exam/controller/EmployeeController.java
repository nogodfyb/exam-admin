package com.fyb.exam.controller;


import com.fyb.exam.common.CommonResult;
import com.fyb.exam.common.Const;
import com.fyb.exam.dto.AdminParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-08-07
 */
@RestController
@RequestMapping("/exam/employee")
public class EmployeeController {

    @PostMapping("adminLogin")
    public CommonResult<Object> adminLogin(@RequestBody AdminParam adminParam, HttpSession session){
        boolean status1 = "admin".equals(adminParam.getUsername());
        boolean status2 = "admin".equals(adminParam.getPassword());
        if(status1&&status2){
            // 登录成功存储在session中
            session.setAttribute(Const.CURRENT_USER,adminParam);
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

}
