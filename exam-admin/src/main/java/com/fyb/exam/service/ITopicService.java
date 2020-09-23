package com.fyb.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fyb.exam.entity.Topic;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fyb
 * @since 2020-08-10
 */
public interface ITopicService extends IService<Topic> {

    //保存题目与岗位之间的关系
    void saveRelations(Integer[] postIds, Integer topicId);
}
