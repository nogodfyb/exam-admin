package com.fyb.exam.service.impl;

import com.fyb.exam.entity.Post;
import com.fyb.exam.mapper.PostMapper;
import com.fyb.exam.service.IPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fyb
 * @since 2020-09-22
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService {

}
