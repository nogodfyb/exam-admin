package com.fyb.exam.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TopicExcelVo {

    @ExcelProperty("题干")
    private String topicDesc;
    @ExcelProperty("选项1")
    private String answer1;
    @ExcelProperty("选项2")
    private String answer2;
    @ExcelProperty("选项3")
    private String answer3;
    @ExcelProperty("选项4")
    private String answer4;
    @ExcelProperty("正确选项")
    private Integer correctAnswer;

}
