package com.airplay.okhttp;

import android.support.annotation.NonNull;

import com.abooc.airremoter.Sender;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;


/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/27.
 */
public class OkSender implements Sender {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final int PORT_REMOTER = 21367;
    @Deprecated
    public static final int PORT_REMOTER_VR = 21368;

    //    String json = "{ \"code\":230, \"info\":\"\" }";
    private String mDeviceIp;
    private String mServer;

    private static OkSender mOur = new OkSender();
    private OkHttpClient httpClient = new OkHttpClient();

    private OkSender() {
    }

    public static OkSender create(String server, int port) {
        mOur.buildServer(server, port);
        return mOur;
    }

    private void buildServer(String server, int port) {
        mDeviceIp = server;
        mServer = "http://" + mDeviceIp + ":" + port;
    }

    @Override
    public void doSend(@NonNull String message) {
        out(mServer + message);

        RequestBody body = RequestBody.create(JSON, message);
        Request request = new Request.Builder()
                .url(mServer)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

                out(e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                out(response.body().string());

            }
        });


    }

    static void out(String str) {
        System.out.println(str);
//                Debug.anchor(str);
    }
}
