package com.fyb.exam.controller;


import com.fyb.exam.common.CommonResult;
import com.fyb.exam.entity.Menu;
import com.fyb.exam.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-08-07
 */
@RestController
@RequestMapping("/exam/menu")
public class MenuController {

    @Autowired
    private IMenuService menuService;

    @GetMapping("/list")
    public CommonResult<List<Menu>> listAll(){
        ArrayList<Menu> allMenuTree = menuService.getAllMenuTree(null);
        if (0!=allMenuTree.size()){
            return CommonResult.success(allMenuTree);
        }
        else {
            return CommonResult.failed();
        }
    }

}
