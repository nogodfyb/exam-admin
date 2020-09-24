package com.fyb.exam.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyb.exam.common.CommonResult;
import com.fyb.exam.entity.Post;
import com.fyb.exam.entity.TopicPostRelation;
import com.fyb.exam.service.IPostService;
import com.fyb.exam.service.ITopicPostRelationService;
import com.fyb.exam.service.ITopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fyb
 * @since 2020-09-23
 */
@RestController
@RequestMapping("/exam/topic-post-relation")
public class TopicPostRelationController {

    @Autowired
    private ITopicPostRelationService topicPostRelationService;

    @Autowired
    private IPostService postService;

    @Autowired
    private ITopicService topicService;

    //查询当前题目的岗位所属关系
    @GetMapping("{topicId}")
    public CommonResult<List<List<Integer>>> selectRelations(@PathVariable Integer topicId){
        QueryWrapper<TopicPostRelation> relationQueryWrapper = new QueryWrapper<>();
        relationQueryWrapper.eq("topic_id",topicId);
        List<TopicPostRelation> list = topicPostRelationService.list(relationQueryWrapper);
        if (list.size()==0) {
            return CommonResult.success(new ArrayList<>());
        }
        List<Integer> postIds = list.stream().map(topicPostRelation -> topicPostRelation.getPostId()).collect(Collectors.toList());
        //根据postIds查出所有岗位信息
        List<Post> postList = postService.listByIds(postIds);
        ArrayList<List<Integer>> selectKeys = new ArrayList<>();
        for (Post post : postList) {
            ArrayList<Integer> selectKey = new ArrayList<>();
            selectKey.add(post.getProductLineId());
            selectKey.add(post.getId());
            selectKeys.add(selectKey);
        }
        return CommonResult.success(selectKeys);
    }

    //更新当前题目与所属岗位的关系
    @PutMapping("update")
    public CommonResult<Object> updateRelations(Integer[] postIds, Integer topicId){
        topicService.updateRelations(postIds,topicId);
        return  CommonResult.success(null);
    }

}
