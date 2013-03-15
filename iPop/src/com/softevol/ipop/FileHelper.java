package com.softevol.ipop;

import android.os.Environment;

import java.io.File;

public final class FileHelper {

    private static final File PROJECT_DIR = new File(Environment.getExternalStorageDirectory(), ".iPap");

    private static final String[] IMAGE_EXTENTIONS = new String[]{".png", ".jpg", ".jpeg"};
    private static final String[] VIEW_EXTENTIONS = new String[]{".mp4", ".3gp"};

    static {
        if (!PROJECT_DIR.exists()) {
            PROJECT_DIR.mkdirs();
        }
    }

    public static File getNextFile(FileType fileType) throws IllegalArgumentException {
        String ext = null;
        switch (fileType) {
            case IMAGE:
                ext = ".jpg";
                break;
            case VIDEO:
                ext = ".mp4";
                break;
            default:
                throw new IllegalArgumentException("fileType can be only IMAGE or VIDEO");
        }

        return new File(PROJECT_DIR, System.currentTimeMillis() + ext);
    }

    public static FileType getFileType(File file) {
        String fileName = file.getName().toLowerCase();
        for (String ext : IMAGE_EXTENTIONS) {
            if (fileName.endsWith(ext)) {
                return FileType.IMAGE;
            }
        }

        for (String ext : VIEW_EXTENTIONS) {
            if (fileName.endsWith(ext)) {
                return FileType.VIDEO;
            }
        }

        return FileType.UNKNOWN;
    }

    public static enum FileType {
        IMAGE, VIDEO, UNKNOWN
    }
}
