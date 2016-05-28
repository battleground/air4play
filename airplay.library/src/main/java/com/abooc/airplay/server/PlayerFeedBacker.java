package com.abooc.airplay.server;


import com.abooc.airplay.Sender;
import com.abooc.airplay.model.Action;
import com.abooc.airplay.model.Position;
import com.abooc.airplay.model.V;

import static com.abooc.airplay.model.Action.PAUSE;
import static com.abooc.airplay.model.Action.REMOTE_CLIENT_CODE;
import static com.abooc.airplay.model.Action.RESUME;
import static com.abooc.airplay.model.Action.SEEK;
import static com.abooc.airplay.model.Action.STOP;
import static com.abooc.airplay.model.Action.TICKER;
import static com.abooc.airplay.model.Action.VIDEO_INFO;
import static com.abooc.airplay.model.Action.VOLUME;

/**
 * Description: <pre>
 *
 *     远程播放器回执消息.目前不会用到.
 *
 * </pre>
 * <br>
 * Creator: 大宇
 * E-mail: allnet@live.cn
 * Date: 16/4/23
 */
public class PlayerFeedBacker {


    private Sender mSender;

    public PlayerFeedBacker(Sender sender) {
        mSender = sender;
    }

    /**
     * 暂停转播放
     */
    public void resume() {
        Action action = new Action(RESUME + REMOTE_CLIENT_CODE);
        mSender.doSend(action.toString());
    }

    /**
     * 播放转暂停
     */
    public void pause() {
        Action action = new Action(PAUSE + REMOTE_CLIENT_CODE);
        mSender.doSend(action.toString());
    }


    public void seek(int position) {
        Action action = new Action(SEEK + REMOTE_CLIENT_CODE);
        action.setInfo(new Position(position));
        mSender.doSend(action.toString());
    }

    /**
     * 停止视频
     */
    public void stop() {
        Action action = new Action(STOP + REMOTE_CLIENT_CODE);
        mSender.doSend(action.toString());
    }

    /**
     * 控制声音
     *
     * @param i 固定值，+1 Or -1
     */
    public void volume(int i) {
        if (i > 0) {
            Action action = new Action(VOLUME + REMOTE_CLIENT_CODE);
            mSender.doSend(action.toString());
        } else {
            Action action = new Action(VOLUME + REMOTE_CLIENT_CODE);
            mSender.doSend(action.toString());
        }
    }

    public void sendVideoInfo(V.Type type, String name, long position, long duration) {
        Action action = new Action(VIDEO_INFO + REMOTE_CLIENT_CODE);
        V video = new V();
        video.type = type.value();
        video.name = name;
        video.position = position;
        video.duration = duration;
        action.setInfo(video);
        mSender.doSend(action.toString());
    }


    public void ticker(int position) {
        Action action = new Action(TICKER);
        action.setInfo(new Position(position));
        mSender.doSend(action.toString());
    }

    public void destroy() {
        mSender = null;
    }

}
