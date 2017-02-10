package com.abooc.airremoter;

import com.abooc.airremoter.model.Action;
import com.abooc.airremoter.model.GYRO;
import com.abooc.airremoter.model.Touch;
import com.abooc.airremoter.model.Xyz;
import com.google.gson.Gson;

import static com.abooc.airremoter.model.Action.GOS;
import static com.abooc.airremoter.model.Action.TOUCH;
import static com.abooc.airremoter.model.Action.TOUCH_XYZ;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/28.
 */
public class VRController {

    private Sender mSender;

    public VRController(Sender sender) {
        mSender = sender;
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
     * 向远端发送TOUCH事件
     *
     * @param touch
     */
    public void touch(Touch touch) {
        Action a = new Action(TOUCH);
        a.setInfo(touch);
        mSender.doSend(toMessage(a));
    }

    public void touchXYZ(float x, float y, float z) {
        Action action = new Action(TOUCH_XYZ);
        action.setInfo(new Xyz(x, y, z));
        mSender.doSend(toMessage(action));
    }

    /**
     * 发送陀螺仪数据
     *
     * @param array
     */
    public void gyroscope(float[] array) {
        Action action = new Action(GOS);
        action.setInfo(new GYRO(array));
        mSender.doSend(toMessage(action));
    }


    /**
     * 设置远端播放器属性
     *
     * @param actionCode 属性值见{@link com.abooc.airremoter.model.Action}
     *                   PLAYER_TOUCH,PLAYER_GOS Or FULL_VIEW_NORMAL,FULL_VIEW_SIGHT,FULL_VIEW_3D
     */
    public void doSettings(int actionCode) {
        Action action = new Action(actionCode);
        mSender.doSend(toMessage(action));
    }
}
