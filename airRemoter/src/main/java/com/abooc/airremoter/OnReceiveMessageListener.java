package com.abooc.airremoter;


import com.abooc.airremoter.model.Action;

public interface OnReceiveMessageListener {

    void onReceiveMessage(Action action, String message);

}