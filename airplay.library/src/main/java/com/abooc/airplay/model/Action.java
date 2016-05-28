package com.abooc.airplay.model;

import com.google.gson.Gson;

/**
 * Description: <pre>远程事件（Action）</pre>
 * <br>
 * Creator: 大宇
 * E-mail: allnet@live.cn
 * Date: 16/4/21
 */
public class Action<JSON_TYPE> {

    public static final int REMOTE_CLIENT_CODE = 100;


    /**
     * 普通
     */
    public static final int FULL_VIEW_NORMAL = 110;


    /**
     * 开启全景
     */
    public static final int FULL_VIEW_SIGHT = 111;

    /**
     * 开启 3D 双屏
     */
    public static final int FULL_VIEW_3D = 112;

    /**
     * 开启TOUCH
     */
    public static final int PLAYER_TOUCH = 120;
    /**
     * 开启GOS
     */
    public static final int PLAYER_GOS = 121;


    /**
     * 播放
     */
    public static final int START = 200;
    /**
     * 继续播放
     */
    public static final int RESUME = 201;
    /**
     * 暂停
     */
    public static final int PAUSE = 202;
    /**
     * 进度
     */
    public static final int SEEK = 203;
    /**
     * 退出播放
     */
    public static final int STOP = 204;
    /**
     * 视频信息
     */
    public static final int VIDEO_INFO = 206;
    /**
     * 声音
     */
    public static final int VOLUME = 208;
    /**
     * 屏幕比例
     */
    public static final int SCALE = 205;
    /**
     * 远程端每一秒返回视频进度
     */
    public static final int TICKER = 310;
    /**
     * 触控
     */
    public static final int TOUCH = 211;
    /**
     * 触控XYZ
     */
    public static final int TOUCH_XYZ = 213;
    /**
     * 陀螺仪
     */
    public static final int GOS = 212;


    /**
     * 结果码:成功
     */
    public static final int RESULT_CODE_SUCCESS = 1;
    /**
     * 结果码:失败
     */
    public static final int RESULT_CODE_ERROR = -1;

    public int code;

//    public int result;

    JSON_TYPE info;

    public JSON_TYPE getInfo() {
        return info;
    }

    public Action(int code) {
        this.code = code;
    }

    public void setInfo(JSON_TYPE jsonObject) {
//        Gson gson = new Gson();
//        String json = gson.toJson(jsonObject);
//        info = gson.toJsonTree(json).getAsJsonObject();

        info = jsonObject;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public int getCode() {
        return code;
    }
}


