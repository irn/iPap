package com.softevol.util;

public final class ThumbnailUtil {
    public static int getInSampleSize(int w, int h, int vw, int vh) {
        return Math.max(w / vw, h / vh);
    }
}
