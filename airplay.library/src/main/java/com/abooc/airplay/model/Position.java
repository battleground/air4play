package com.abooc.airplay.model;

import android.support.annotation.Keep;

/**
 * Description: <pre>
 * <br>
 *     视频进度,用于手机端Seek进度条发送数据给远程端;
 * <br>
 *     注意::::由于远程端接收数据不允许带冗余数据(如duration),所以不能和SeekProcess.java共用;
 * <br>
 * </pre>
 * <br>
 * Creator: 大宇
 * E-mail: allnet@live.cn
 * Date: 16/4/22
 */
@Keep
public class Position {
    @Keep
    public int position;

    public Position(int position) {
        this.position = position;
    }
}
