package com.fyb.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fyb.exam.entity.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fyb
 * @since 2020-08-07
 */
public interface IMenuService extends IService<Menu> {
    ArrayList<Menu> getAllMenuTree(List<Integer> idList);
}
