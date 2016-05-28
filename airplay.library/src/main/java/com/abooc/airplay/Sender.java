package com.abooc.airplay;

import android.support.annotation.NonNull;

/**
 * Description: <pre>
 * <br>
 *     发送者
 * <br>
 * </pre>
 * <br>
 * Creator: 大宇
 * E-mail: allnet@live.cn
 * Date: 16/4/20
 */
public interface Sender {
    void doSend(@NonNull String message);
}
