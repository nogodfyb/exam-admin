package com.fyb.exam.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.entity.Post;
import com.fyb.exam.entity.WorkSection;
import com.fyb.exam.service.IPostService;
import com.fyb.exam.service.IWorkSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/exam/work-section")
public class WorkSectionController {

    @Autowired
    private IPostService postService;
    @Autowired
    private IWorkSectionService workSectionService;

    //在知当前产线id的情况下，查询当前岗位有多少工段
    @GetMapping("{productLineId}")
    public CommonResult<List<WorkSection>> list(@PathVariable Integer productLineId){
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.select("work_section_id").eq("product_line_id",productLineId).groupBy("work_section_id");
        List<Integer> list =(List<Integer>)(List<?>) postService.listObjs(postQueryWrapper);
        List<WorkSection> workSections = workSectionService.listByIds(list);
        return CommonResult.success(workSections);
    }
}
