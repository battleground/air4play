package com.abooc.airplay;

import android.support.annotation.NonNull;

import com.abooc.airplay.model.Action;
import com.abooc.airplay.model.Position;
import com.abooc.airplay.model.V;
import com.abooc.airplay.model.Volume;
import com.google.gson.Gson;

import static com.abooc.airplay.model.Action.PAUSE;
import static com.abooc.airplay.model.Action.RESUME;
import static com.abooc.airplay.model.Action.SCALE;
import static com.abooc.airplay.model.Action.SEEK;
import static com.abooc.airplay.model.Action.START;
import static com.abooc.airplay.model.Action.STOP;
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

    public RemotePlayer(@NonNull Sender sender) {
        mSender = sender;
    }

    /**
     * 设置播放状态
     *
     * @param player 播放状态
     */
    public void setStatus(RemotePlayer.Player player) {
        mPlayerStatus = player;
    }

    /**
     * 远程端是否正在播放
     *
     * @return true 正在播放中
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
     * 投屏播控中
     *
     * @return true 投屏中
     */
    public boolean isRemoting() {
        return mPlayerStatus != null;
    }

    public String toMessage(Object object) {
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
        mSender.doSend(toMessage(action));
        return v;
    }

    /**
     * 暂停转播放
     */
    public void resume() {
        Action action = new Action(RESUME);
        mSender.doSend(toMessage(action));
    }

    /**
     * 播放转暂停
     */
    public void pause() {
        Action action = new Action(PAUSE);
        mSender.doSend(toMessage(action));
    }


    public void seek(int ms) {
        Action action = new Action(SEEK);
        action.setInfo(new Position(ms));
        mSender.doSend(toMessage(action));
    }

    /**
     * 停止视频
     */
    public void stop() {
        Action action = new Action(STOP);
        mSender.doSend(toMessage(action));
    }

    /**
     * 控制声音
     *
     * @param i 固定值，+1 Or -1
     */
    public void volume(int i) {
        Action action = new Action(VOLUME);
        action.setInfo(new Volume(i));
        mSender.doSend(toMessage(action));
    }

    public void getVideoInfo() {
        Action action = new Action(VIDEO_INFO);
        mSender.doSend(toMessage(action));
    }

    public void screenSize(int index) {
        Action action = new Action(SCALE);
        action.setInfo(index + "");
        mSender.doSend(toMessage(action));
    }


    public void destroy() {
        mPlayerStatus = null;
    }

}
