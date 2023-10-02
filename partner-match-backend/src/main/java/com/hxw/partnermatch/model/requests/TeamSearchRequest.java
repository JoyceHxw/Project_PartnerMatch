package com.hxw.partnermatch.model.requests;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class TeamSearchRequest {
    private Long id;
    private List<Long> idList;
    private String searchText;
    private Integer num;
    private Integer maxNum;
    private Long userId;
    private Integer status;
    private Integer isRelative;
    private Integer pageNum;
    private Integer pageSize;
}
