package com.fyb.exam.service.impl;

import com.fyb.exam.entity.AdminUser;
import com.fyb.exam.mapper.AdminUserMapper;
import com.fyb.exam.service.IAdminUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fyb
 * @since 2020-09-15
 */
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements IAdminUserService {

}
