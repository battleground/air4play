package com.abooc.airremoter;

/**
 * Description: <pre></pre>
 * <br>
 * Creator: 大宇
 * E-mail: allnet@live.cn
 * Date: 16/4/26
 */
public class Utils {


    /**
     * 格式化时间
     *
     * @param milliSecond 毫秒
     * @return 时:分:秒
     */
    public static String formatMilliSecondTime(long milliSecond) {
        return formatSecondTime(milliSecond / 1000);
    }

    /**
     * 格式化时间
     *
     * @param totalSecond 秒
     * @return 时:分:秒
     */
    public static String formatSecondTime(long totalSecond) {
        long minute = totalSecond / 60;
        long hour = minute / 60;
        minute %= 60;
        totalSecond %= 60;
        return String.format("%02d:%02d:%02d", hour, minute, totalSecond);
    }
}
