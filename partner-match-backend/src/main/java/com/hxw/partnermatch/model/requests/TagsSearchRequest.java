package com.hxw.partnermatch.model.requests;

import lombok.Data;

import java.util.List;

@Data
public class TagsSearchRequest {
    private String tags;
    private Integer pageNum;
    private Integer pageSize;
}
