package com.android.liuzhuang.threadwatchdog.utils;

/**
 * convert stack
 * Created by liuzhuang on 16/5/6.
 */
public class StackUtil {
    public static final String SEPARATOR = "\r\n";
    public static String getStackTrace(Thread thread) {
        if (thread != null) {
            StringBuilder builder = new StringBuilder();
            StackTraceElement[] elements = thread.getStackTrace();
            for (StackTraceElement element :
                    elements) {
                builder.append(element.toString()).append(SEPARATOR);
            }
            return builder.toString();
        }
        return "";
    }
}
