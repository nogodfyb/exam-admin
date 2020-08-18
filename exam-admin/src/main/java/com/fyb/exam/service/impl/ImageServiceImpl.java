package com.fyb.exam.service.impl;

import com.fyb.exam.entity.Image;
import com.fyb.exam.mapper.ImageMapper;
import com.fyb.exam.service.IImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fyb
 * @since 2020-08-18
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements IImageService {

}
