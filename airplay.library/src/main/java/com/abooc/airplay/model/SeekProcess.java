package com.abooc.airplay.model;

import android.support.annotation.Keep;

/**
 * Description: <pre>
 * <br>
 *     视频进度,用于解析远程端Seek进度条json数据
 * <br>
 * </pre>
 * <br>
 * Creator: 大宇
 * E-mail: allnet@live.cn
 * Date: 16/4/22
 */
@Keep
public class SeekProcess {
    @Keep
    public int position;
    @Keep
    public int duration;
}
