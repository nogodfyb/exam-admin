package com.fyb.exam.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.common.Const;
import com.fyb.exam.entity.AdminUser;
import com.fyb.exam.entity.Post;
import com.fyb.exam.entity.ProductLine;
import com.fyb.exam.service.IPostService;
import com.fyb.exam.service.IProductLineService;
import com.fyb.exam.vo.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

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

    private Integer getWorkSectionId(HttpSession session){
        AdminUser adminUser = (AdminUser)session.getAttribute(Const.CURRENT_USER);
        return adminUser.getWorkSectionId();
    }


    //查询当前登录的管理员所属的工段有多少条产线及职位二级分类
    @GetMapping("/list")
    public CommonResult<List<Options>> list(HttpSession session){
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.select("product_line_id").groupBy("product_line_id").eq("work_section_id",getWorkSectionId(session));
        List<Integer> list = (List<Integer>)(List<?>) postService.listObjs(postQueryWrapper);
        List<ProductLine> productLines = productLineService.listByIds(list);
        List<Options> collect = productLines.stream().map(productLine -> {
            Options option = new Options();
            option.setLabel(productLine.getProductLineName());
            option.setValue(productLine.getId());
            //根据产线id和工段id查询所有岗位信息
            QueryWrapper<Post> postQueryWrapper2 = new QueryWrapper<>();
            postQueryWrapper2.eq("product_line_id",productLine.getId()).eq("work_section_id",getWorkSectionId(session));
            List<Post> postList = postService.list(postQueryWrapper2);
            List<Options> collectChildRen = postList.stream().map(post -> {
                Options option2 = new Options();
                option2.setLabel(post.getPostName());
                option2.setValue(post.getId());
                return option2;
            }).collect(Collectors.toList());
            option.setChildren(collectChildRen);
            return option;
        }).collect(Collectors.toList());
        return CommonResult.success(collect);
    }


}
