package com.fyb.exam.service.impl;

import com.fyb.exam.entity.LoginLog;
import com.fyb.exam.mapper.LoginLogMapper;
import com.fyb.exam.service.ILoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements ILoginLogService {

}
