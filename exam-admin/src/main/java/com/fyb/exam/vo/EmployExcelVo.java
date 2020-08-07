package com.fyb.exam.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 基础数据类
 *
 * @author Jiaju Zhuang
 **/
@Data
public class EmployExcelVo {
    @ExcelProperty("职员工号")
    private String employeeCode;
    @ExcelProperty("用户密码")
    private String password;
}
