package com.fyb.exam.dto;

import lombok.Data;

@Data
public class TopicPageParam {
    private Integer pageNum;
    private Integer pageSize;
    private String topicDesc;
}
