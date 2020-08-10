package com.fyb.exam.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyb.exam.common.CommonPage;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.common.Const;
import com.fyb.exam.dto.AdminParam;
import com.fyb.exam.dto.EmployeePageParam;
import com.fyb.exam.entity.Employee;
import com.fyb.exam.listener.UploadEmployeeListener;
import com.fyb.exam.service.IEmployeeService;
import com.fyb.exam.util.MD5Utils;
import com.fyb.exam.vo.EmployExcelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

    @Autowired
    private IEmployeeService employeeService;


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
    //检验用户是否已登录
    @GetMapping("isLogin")
    public CommonResult<Object> isLogin(HttpSession session){
        Object user = session.getAttribute(Const.CURRENT_USER);
        if (user==null) {
            return  CommonResult.failed();
        }else return CommonResult.success(null);
    }

    //分页查询
    @GetMapping("/employees")
    public CommonResult<CommonPage<Employee>> queryAllUsers(@Valid EmployeePageParam employeePageParam){
        Page<Employee> userPage = new Page<>();
        userPage.setSize(employeePageParam.getPageSize());
        userPage.setCurrent(employeePageParam.getPageNum());
        QueryWrapper<Employee> employeeQueryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(employeePageParam.getQuery())){
            employeeQueryWrapper.like("employee_code",employeePageParam.getQuery());
        }
        IPage<Employee> pageResult = employeeService.page(userPage,employeeQueryWrapper);
        CommonPage<Employee> userCommonPage = CommonPage.restPage(pageResult);
        CommonResult<CommonPage<Employee>> success = CommonResult.success(userCommonPage);
        return success;
    }

    //添加职员
    @PostMapping("add")
    public CommonResult<Object> addEmployee(@RequestBody Employee employee){
        employee.setPassword(MD5Utils.encode(employee.getPassword()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        boolean save = employeeService.save(employee);
        if (save) {
            return CommonResult.success(null);
        }else return CommonResult.failed();
    }
    //根据id获取职员信息
    @GetMapping("{employeeId}")
    public  CommonResult<Employee> queryUserById(@PathVariable Integer employeeId){
        Employee user = employeeService.getById(employeeId);
        if(null!=user){
            user.setPassword(null);
            return CommonResult.success(user);
        }
        return CommonResult.failed();
    }

    //根据id修改职员信息
    @PutMapping("/employees")
    public  CommonResult<Employee> updateUserById(@RequestBody Employee user){
        user.setPassword(MD5Utils.encode(user.getPassword()));
        user.setUpdateTime(LocalDateTime.now());
        boolean update = employeeService.updateById(user);
        if(update){
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    /**
     * 文件下载（失败了会返回一个有部分数据的Excel）
     * <p>1. 创建excel对应的实体对象 参照{@link EmployExcelVo}
     * <p>2. 设置返回的 参数
     * <p>3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
     */
    @GetMapping("download")
    public void download(HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("测试", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), EmployExcelVo.class).sheet("模板").doWrite(new ArrayList());
    }

    /**
     * 文件上传
     * <p>1. 创建excel对应的实体对象 参照{@link EmployExcelVo}
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link UploadEmployeeListener}
     * <p>3. 直接读即可
     */
    @PostMapping("upload")
    @ResponseBody
    public String upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), EmployExcelVo.class, new UploadEmployeeListener(employeeService)).sheet().doRead();
        return "success";
    }




}
