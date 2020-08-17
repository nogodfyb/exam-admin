package com.fyb.exam.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TopicExcelVo {

    @ExcelProperty("题干")
    private String topicDesc;
    @ExcelProperty("题型(1代表单选，2代表判断，3代表多选)")
    private Integer type;
    @ExcelProperty("选项A")
    private String answer1;
    @ExcelProperty("选项B")
    private String answer2;
    @ExcelProperty("选项C")
    private String answer3;
    @ExcelProperty("选项D")
    private String answer4;
    @ExcelProperty("选项E")
    private String answer5;
    @ExcelProperty("选项F")
    private String answer6;
    @ExcelProperty("正确选项")
    private String correctAnswer;

}
