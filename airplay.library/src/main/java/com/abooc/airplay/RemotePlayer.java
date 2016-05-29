package com.abooc.airplay;

import com.abooc.airplay.model.Action;
import com.abooc.airplay.model.GYRO;
import com.abooc.airplay.model.Position;
import com.abooc.airplay.model.Touch;
import com.abooc.airplay.model.V;
import com.abooc.airplay.model.Volume;
import com.abooc.airplay.model.Xyz;
import com.google.gson.Gson;

import static com.abooc.airplay.model.Action.GOS;
import static com.abooc.airplay.model.Action.PAUSE;
import static com.abooc.airplay.model.Action.RESUME;
import static com.abooc.airplay.model.Action.SCALE;
import static com.abooc.airplay.model.Action.SEEK;
import static com.abooc.airplay.model.Action.START;
import static com.abooc.airplay.model.Action.STOP;
import static com.abooc.airplay.model.Action.TOUCH;
import static com.abooc.airplay.model.Action.TOUCH_XYZ;
import static com.abooc.airplay.model.Action.VIDEO_INFO;
import static com.abooc.airplay.model.Action.VOLUME;

/**
 * 手机端向远程端发送消息
 * <br>
 * Created by author:大宇
 * email:allnet@live.cn
 */
public class RemotePlayer {

    private Sender mSender;

    private Player mPlayerStatus;

    public enum Player {
        /**
         * 在播放状态
         */
        PLAYING,
        /**
         * 暂停状态
         */
        PASUE,
        /**
         * 视频已停止
         */
        STOP
    }

    RemotePlayer(Sender sender) {
        mSender = sender;
    }

    public void setStatus(Player player) {
        mPlayerStatus = player;
    }

    /**
     * 远程端是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return mPlayerStatus == Player.PLAYING;
    }

    public boolean isPause() {
        return mPlayerStatus == Player.PASUE;
    }

    public boolean isStop() {
        return mPlayerStatus == Player.STOP;
    }

    /**
     * 是否正在远程投屏
     *
     * @return
     */
    public boolean isRemoting() {
        return mPlayerStatus != null;
    }

    private String toString(Object object) {
        String json = null;
        try {
            json = new Gson().toJson(object);
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        }
        return json;
    }

    /**
     * 播放视频
     *
     * @param type
     * @param url      视频真实源地址
     * @param name
     * @param position
     */
    public V start(V.Type type, String url, String name, long position) {
        V v = new V();
        v.type = type.value();
        v.name = name;
        v.url = url;
        v.position = position;
        Action action = new Action(START);
        action.setInfo(v);
        mSender.doSend(toString(action));
        return v;
    }

    /**
     * 暂停转播放
     */
    public void resume() {
        Action action = new Action(RESUME);
        mSender.doSend(toString(action));
    }

    /**
     * 播放转暂停
     */
    public void pause() {
        Action action = new Action(PAUSE);
        mSender.doSend(toString(action));
    }


    public void seek(int ms) {
        Action action = new Action(SEEK);
        action.setInfo(new Position(ms));
        mSender.doSend(toString(action));
    }

    /**
     * 停止视频
     */
    public void stop() {
        Action action = new Action(STOP);
        mSender.doSend(toString(action));
    }

    /**
     * 控制声音
     *
     * @param i 固定值，+1 Or -1
     */
    public void volume(int i) {
        Action action = new Action(VOLUME);
        action.setInfo(new Volume(i));
        mSender.doSend(toString(action));
    }

    public void getVideoInfo() {
        Action action = new Action(VIDEO_INFO);
        mSender.doSend(toString(action));
    }

    public void screenSize(int index) {
        Action action = new Action(SCALE);
        action.setInfo(index + "");
        mSender.doSend(toString(action));
    }

    /**
     * 向远端发送TOUCH事件
     *
     * @param touch
     */
    public void touch(Touch touch) {
        Action a = new Action(TOUCH);
        a.setInfo(touch);
        mSender.doSend(toString(a));
    }

    public void touchXYZ(float x, float y, float z) {
        Action action = new Action(TOUCH_XYZ);
        action.setInfo(new Xyz(x, y, z));
        mSender.doSend(toString(action));
    }

    /**
     * 发送陀螺仪数据
     *
     * @param array
     */
    public void gyroscope(float[] array) {
        Action action = new Action(GOS);
        action.setInfo(new GYRO(array));
        mSender.doSend(toString(action));
    }


    /**
     * 设置远端播放器属性
     *
     * @param actionCode 属性值见{@link com.abooc.airplay.model.Action}
     *                   PLAYER_TOUCH,PLAYER_GOS Or FULL_VIEW_NORMAL,FULL_VIEW_SIGHT,FULL_VIEW_3D
     */
    public void doSettings(int actionCode) {
        Action action = new Action(actionCode);
        mSender.doSend(toString(action));
    }

    public void destroy() {
        mPlayerStatus = null;
    }

}
