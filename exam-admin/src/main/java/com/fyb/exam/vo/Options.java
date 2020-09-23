package com.fyb.exam.vo;

import lombok.Data;

import java.util.List;

@Data
public class Options {

    private Integer value;

    private String label;

    private List<Options> children;

}
