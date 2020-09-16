package com.fyb.exam.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class CommonPage<T> {

    //当前页码
    private Integer pageNum;
    //分页尺寸
    private Integer pageSize;
    //总共页数
    private Integer totalPage;
    //总共数量
    private Long total;
    //分页数据列表
    private List<T> list;

    //转化
    public static <T> CommonPage<T> resetPage(IPage<T> iPage) {
        CommonPage<T> result = new CommonPage<>();
        result.setPageNum((int) iPage.getCurrent());
        result.setPageSize((int) iPage.getSize());
        result.setTotalPage((int) iPage.getPages());
        result.setTotal(iPage.getTotal());
        result.setList(iPage.getRecords());
        return result;
    }
}
