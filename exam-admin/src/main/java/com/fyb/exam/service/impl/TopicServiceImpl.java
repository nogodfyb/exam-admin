package com.fyb.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fyb.exam.entity.Topic;
import com.fyb.exam.entity.TopicPostRelation;
import com.fyb.exam.mapper.TopicMapper;
import com.fyb.exam.service.ITopicPostRelationService;
import com.fyb.exam.service.ITopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fyb
 * @since 2020-08-10
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements ITopicService {

    @Autowired
    private ITopicPostRelationService topicPostRelationService;

    //保存题目与岗位之间的关系
    @Override
    public void saveRelations(Integer[] postIds,Integer topicId){
        for (Integer postId : postIds) {
            TopicPostRelation topicPostRelation = new TopicPostRelation();
            topicPostRelation.setPostId(postId);
            topicPostRelation.setTopicId(topicId);
            topicPostRelationService.save(topicPostRelation);
        }
    }


}
