package com.zengdw.mybatis.util;

import com.intellij.openapi.util.SystemInfoRt;

import java.io.File;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/20 14:16
 */
public class FileUtil {
    public static final String SEPARATOR = "/";

    public static String separatorConversion(String path) {
        if (SystemInfoRt.isWindows) {
            return path.replace(File.separator, SEPARATOR);
        }
        return path;
    }

}
