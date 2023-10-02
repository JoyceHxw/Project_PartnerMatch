package com.hxw.partnermatch.utils;

import com.hxw.partnermatch.model.responses.TagDistance;

import java.util.Comparator;

/**
 * 大根堆
 */
public class TagDistanceComparator implements Comparator<TagDistance> {
    @Override
    public int compare(TagDistance o1, TagDistance o2) {
        return Integer.compare(o2.getEditDistance(),o1.getEditDistance());
    }
}
