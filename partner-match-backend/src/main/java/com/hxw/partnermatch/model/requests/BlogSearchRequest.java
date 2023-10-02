package com.hxw.partnermatch.model.requests;

import lombok.Getter;

@Getter
public class BlogSearchRequest {
    private String searchText;
    private Long authorId;
}
