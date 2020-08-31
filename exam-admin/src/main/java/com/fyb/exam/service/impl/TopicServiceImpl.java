package com.fyb.exam.service.impl;

import com.fyb.exam.entity.Topic;
import com.fyb.exam.mapper.TopicMapper;
import com.fyb.exam.service.ITopicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fyb.exam.vo.TopicExcelVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    private final List<TopicExcelVo> failList=new ArrayList<>();

    @Override
    public List<TopicExcelVo> getFailList() {
        return failList;
    }
}
