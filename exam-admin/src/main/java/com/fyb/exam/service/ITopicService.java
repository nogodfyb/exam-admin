package com.fyb.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fyb.exam.entity.Topic;
import com.fyb.exam.vo.TopicExcelVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fyb
 * @since 2020-08-10
 */
public interface ITopicService extends IService<Topic> {

    List<TopicExcelVo> getFailList();
}
