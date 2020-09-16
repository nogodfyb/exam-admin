package com.fyb.exam.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyb.exam.common.CommonPage;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.common.Const;
import com.fyb.exam.dto.EmployeePageParam;
import com.fyb.exam.entity.AdminUser;
import com.fyb.exam.service.IAdminUserService;
import com.fyb.exam.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-09-15
 */
@RestController
@RequestMapping("/exam/admin")
public class AdminUserController {

    @Autowired
    private IAdminUserService adminUserService;

    private String getCurrentUserName(HttpSession session){
        AdminUser currentUser = (AdminUser)session.getAttribute(Const.CURRENT_USER);
        return currentUser.getUserName();
    }

    //分页查询
    @GetMapping("/users")
    public CommonResult<CommonPage<AdminUser>> queryAllUsers(@Valid EmployeePageParam employeePageParam){
        Page<AdminUser> adminUserPage = new Page<>();
        adminUserPage.setSize(employeePageParam.getPageSize());
        adminUserPage.setCurrent(employeePageParam.getPageNum());
        QueryWrapper<AdminUser> employeeQueryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(employeePageParam.getQuery())){
            employeeQueryWrapper.like("user_name",employeePageParam.getQuery());
        }
        IPage<AdminUser> pageResult = adminUserService.page(adminUserPage,employeeQueryWrapper);
        CommonPage<AdminUser> adminUserCommonPage = CommonPage.resetPage(pageResult);
        CommonResult<CommonPage<AdminUser>> success = CommonResult.success(adminUserCommonPage);
        return success;
    }

    //添加管理员
    @PostMapping("add")
    public CommonResult<Object> addEmployee(@RequestBody AdminUser adminUser,HttpSession session){
        String currentUserName = getCurrentUserName(session);
        if(currentUserName.equals("K8078")){
            adminUser.setPassword(MD5Utils.encode(adminUser.getPassword()));
            adminUser.setCreateTime(LocalDateTime.now());
            adminUser.setUpdateTime(LocalDateTime.now());
            adminUser.setAreaId(adminUser.getAreaId());
            boolean save = adminUserService.save(adminUser);
            if (save) {
                return CommonResult.success(null);
            }else return CommonResult.failed();
        }else return CommonResult.forbidden();
    }

    //根据id获取管理员信息
    @GetMapping("{adminId}")
    public  CommonResult<AdminUser> queryUserById(@PathVariable Integer adminId){
        AdminUser user = adminUserService.getById(adminId);
        if(null!=user){
            user.setPassword(null);
            return CommonResult.success(user);
        }
        return CommonResult.failed();
    }

    //根据id修改管理员信息
    @PutMapping("/admins")
    public  CommonResult<AdminUser> updateUserById(@RequestBody AdminUser adminUser,HttpSession session){
        String currentUserName = getCurrentUserName(session);
        if(currentUserName.equals("K8078")){
            adminUser.setPassword(MD5Utils.encode(adminUser.getPassword()));
            adminUser.setUpdateTime(LocalDateTime.now());
            boolean update = adminUserService.updateById(adminUser);
            if(update){
                return CommonResult.success(null);
            }
            return CommonResult.failed();
        } else return CommonResult.forbidden();
    }

    //根据id删除管理员
    @DeleteMapping("/admins/{id}")
    public CommonResult<Object> deleteAdminUserById(@PathVariable Integer id,HttpSession session){
        String currentUserName = getCurrentUserName(session);
        if(currentUserName.equals("K8078")){
            boolean remove = adminUserService.removeById(id);
            return remove?CommonResult.success(null):CommonResult.failed();
        }else return CommonResult.forbidden();
    }

}
