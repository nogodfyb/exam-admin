package com.fyb.exam.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.entity.Post;
import com.fyb.exam.service.IPostService;
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
 * @since 2020-09-22
 */
@RestController
@RequestMapping("/exam/post")
public class PostController {
    @Autowired
    private IPostService postService;

    //在已知当前产线id和和工段id的情况下，查询有多少岗位
    @GetMapping("list")
    public CommonResult<List<Post>> list(Integer productLineId,Integer workSectionId){
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.eq("product_line_id",productLineId).eq("work_section_id",workSectionId);
        List<Post> list = postService.list(postQueryWrapper);
        return CommonResult.success(list);
    }

}
