package com.abooc.airplay;

import com.abooc.airplay.model.Action;

public interface OnReceiveMessageListener {

    void onReceiveMessage(Action action, String message);

}