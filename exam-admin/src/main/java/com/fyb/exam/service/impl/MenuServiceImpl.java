package com.fyb.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fyb.exam.entity.Menu;
import com.fyb.exam.mapper.MenuMapper;
import com.fyb.exam.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fyb
 * @since 2020-08-07
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    @Autowired
    private MenuMapper menuMapper;



    @Override
    public ArrayList<Menu> getAllMenuTree(List<Integer> idList){
        List<Menu> rootList=null;
        if(idList==null){
            //原始数据
            rootList = menuMapper.selectList(null);
        }else {
            //原始数据
            rootList = menuMapper.selectBatchIds(idList);
        }
        //最后的结果
        ArrayList<Menu> categoryArrayList = new ArrayList<>();
        //先找到所有的一级菜单，parentid为0
        for (int i = 0; i <rootList .size() ; i++) {
            if(rootList .get(i).getParentId()==0){
                categoryArrayList.add(rootList.get(i));
            }
        }
        //为一级菜单设置子菜单
        for (Menu category: categoryArrayList
        ) {
            category.setChildren(getChildren(category.getId(),rootList));
        }
        return categoryArrayList;
    }

    /**
     * 递归查找子菜单
     * @param id 当前菜单id
     * @param rootList 要查找的列表
     * @return
     */
    private List<Menu> getChildren(Integer id, List<Menu> rootList) {
        //子菜单
        ArrayList<Menu> childList = new ArrayList<>();
        for (Menu category: rootList
        ) {
            //遍历所有节点，将父菜单id与传过来的的id比较
            if(category.getParentId()!=0){
                if(category.getParentId().equals(id)){
                    childList.add(category);
                }
            }
        }
        //把子菜单的子菜单再循环一遍
        for (Menu category:childList
        ) {
            //递归
            category.setChildren(getChildren(category.getId(),rootList));
        }
        //递归退出条件
        if(childList.size()==0){
            return null;
        }
        return childList;
    }

}
