package com.abooc.airremoter;

import android.support.annotation.NonNull;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/28.
 */
public class VRPlayer extends RemotePlayer {

    VRController iVRController;

    public VRPlayer(@NonNull Sender sender) {
        super(sender);
        iVRController = new VRController(sender);
    }

    public VRController getVRController() {
        return iVRController;
    }

}
