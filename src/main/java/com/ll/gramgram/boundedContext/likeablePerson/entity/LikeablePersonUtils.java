package com.ll.gramgram.boundedContext.likeablePerson.entity;

public class LikeablePersonUtils {
    public static String getAttractiveTypeDisplayName(int attractiveTypeCode) {
        return switch (attractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            default -> "능력";
        };
    }
}
