package com.fyb.exam.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.entity.Post;
import com.fyb.exam.entity.ProductLine;
import com.fyb.exam.service.IPostService;
import com.fyb.exam.service.IProductLineService;
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
@RequestMapping("/exam/product-line")
public class ProductLineController {


    @Autowired
    private IPostService postService;

    @Autowired
    private IProductLineService productLineService;

    //查询当前存在的岗位当中存在多少个产线
    @GetMapping("/list")
    public CommonResult<List<ProductLine>> list(){
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.select("product_line_id").groupBy("product_line_id");
        List<Integer> list = (List<Integer>)(List<?>) postService.listObjs(postQueryWrapper);
        List<ProductLine> productLines = productLineService.listByIds(list);
        return CommonResult.success(productLines);
    }


}
