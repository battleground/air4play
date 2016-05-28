package com.abooc.airplay.model;

import android.support.annotation.Keep;

import com.google.gson.Gson;

/**
 * Description: <pre></pre>
 * <br>
 * Creator: 大宇
 * E-mail: allnet@live.cn
 * Date: 16/4/20
 */
@Keep
public class V {

    /**
     * -1 其他
     * 0 普通视频
     * 1 暴风云普通视频
     * 2 暴风云全景(VR)视频
     */
    public enum Type {

        TYPE_OTHER(-1),
        TYPE_NORMAL(0),
        TYPE_BAOFENG(1),
        TYPE_BAOFENG_VR(2);

        int value;

        Type(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        @Override
        public String toString() {
            return "" + value;
//            return String.valueOf(value);
        }
    }

    @Keep
    public int type;
    @Keep
    public String name;
    @Keep
    public String url;
    @Keep
    public long position;
    @Keep
    public long duration;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
