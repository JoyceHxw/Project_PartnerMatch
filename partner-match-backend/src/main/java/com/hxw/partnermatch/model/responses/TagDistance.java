package com.hxw.partnermatch.model.responses;

import com.hxw.partnermatch.model.Tag;
import com.hxw.partnermatch.model.User;
import lombok.Data;

@Data
public class TagDistance {
    private Long userId;
    private Integer editDistance;

    public TagDistance(Long userId,Integer editDistance){
        this.userId=userId;
        this.editDistance=editDistance;
    }
}
