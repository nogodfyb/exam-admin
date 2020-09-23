package com.fyb.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author fyb
 * @since 2020-08-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Topic implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    /**
     * 题目描述，题干信息
     */
    private String topicDesc;

    /**
     * 题型(1代表单选，2代表判断题，3代表多选题)
     */
    private Integer type;

    /**
     * 答案1
     */
    private String answer1;

    /**
     * 答案2
     */
    private String answer2;

    /**
     * 答案3
     */
    private String answer3;

    /**
     * 答案4
     */
    private String answer4;

    /**
     * 答案5
     */
    private String answer5;

    /**
     * 答案6
     */
    private String answer6;


    /**
     * 是否图片形式的题型：0，否，1是
     */
    private Boolean isGraphic;

    /**
     * 是否删除(0未删除，1删除)
     */
    private Boolean isDeleted;

    /**
     * 正确答案的序号
     */
    private String correctAnswer;

    /**
     * 图片名称
     */
    private String imageName;

    /**
     * 创建者id
     */
    private String creatorId;
    /**
     * 最后更新人id
     */
    private String lastOperatorId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
