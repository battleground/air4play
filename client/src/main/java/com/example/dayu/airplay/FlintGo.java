//package com.example.dayu.airplay;
//
//import android.content.Context;
//import android.os.Bundle;
//
//import com.abooc.util.Debug;
//
//import tv.matchstick.flint.ApplicationMetadata;
//import tv.matchstick.flint.ConnectionResult;
//import tv.matchstick.flint.Flint;
//import tv.matchstick.flint.FlintDevice;
//import tv.matchstick.flint.FlintManager;
//import tv.matchstick.flint.ResultCallback;
//import tv.matchstick.flint.Status;
//
///**
// * Description: <pre></pre>
// * <p/>
// * Creator: 大宇
// * E-mail: allnet@live.cn
// * Date: 16/4/20
// */
//public class FlintGo {
//
//    private Context mContext;
//    Flint.Listener mFlintListener;
//    FlintManager mApiClient;
//    ConnectionCallbacks mConnectionCallbacks = new ConnectionCallbacks();
//    boolean mWaitingForReconnect;
//
//    public FlintGo(Context context) {
//        mContext = context;
//    }
//
//    public void connect(FlintDevice device) {
//        Flint.FlintApi.setApplicationId("~flintplayer");
//        mFlintListener = new Flint.Listener() {
//            @Override
//            public void onApplicationStatusChanged() {
//                Debug.anchor("onApplicationStatusChanged: " + Flint.FlintApi.getApplicationStatus(mApiClient));
//            }
//
//            @Override
//            public void onVolumeChanged() {
//                Debug.anchor("onVolumeChanged: " + Flint.FlintApi.getVolume(mApiClient));
//            }
//
//            @Override
//            public void onApplicationDisconnected(int errorCode) {
//                teardown();
//            }
//        };
//
//
//        Flint.FlintOptions.Builder apiOptionsBuilder = Flint.FlintOptions.builder(device, mFlintListener);
//
//        mApiClient = new FlintManager.Builder(mContext)
//                .addApi(Flint.API, apiOptionsBuilder.build())
//                .addConnectionCallbacks(mConnectionCallbacks)
//                .build();
//
//        mApiClient.connect();
//    }
//
//    private class ConnectionCallbacks implements FlintManager.ConnectionCallbacks {
//        @Override
//        public void onConnected(Bundle connectionHint) {
//            Debug.anchor("isSuccess, connectionHint: " + connectionHint);
//            if (mWaitingForReconnect) {
//                mWaitingForReconnect = false;
////                reconnectChannels();
//            } else {
//                try {
//                    Flint.FlintApi.launchApplication(mApiClient, "~flintplayer", false).setResultCallback(new ResultCallback<Flint.ApplicationConnectionResult>() {
//                        @Override
//                        public void onResult(Flint.ApplicationConnectionResult result) {
//                            Debug.anchor("result:" + result);
//                            Status status = result.getStatus();
//                            if (status.isSuccess()) {
//                                ApplicationMetadata applicationMetadata = result.getApplicationMetadata();
//                                String applicationStatus = result.getApplicationStatus();
//                                boolean wasLaunched = result.getWasLaunched();
//                            } else {
//                                teardown();
//                            }
//                        }
//                    });
//                } catch (Exception e) {
//                    Debug.anchor("Failed to launch application: " + e);
//                }
//            }
//        }
//
//        @Override
//        public void onConnectionSuspended(int cause) {
//            mWaitingForReconnect = true;
//            Debug.anchor("cause: " + cause);
//        }
//
//        @Override
//        public void onConnectionFailed(ConnectionResult connectionResult) {
//            Debug.anchor("connectionResult: " + connectionResult);
//            teardown();
//
//        }
//
//    }
//
//    private void teardown() {
//        Debug.anchor("teardown");
////        if (mApiClient != null) {
////            if (mApplicationStarted) {
////                if (mApiClient.isConnected() || mApiClient.isConnecting()) {
////                    try {
////                        Flint.FlintApi.stopApplication(mApiClient);
////                        if (mHelloWorldChannel != null) {
////                            Flint.FlintApi.removeMessageReceivedCallbacks(mApiClient, mHelloWorldChannel.getNamespace());
////                            mHelloWorldChannel = null;
////                        }
////                    } catch (IOException e) {
////                        Log.anchor("Exception while removing channel:" + e);
////                    }
////                    mApiClient.disconnect();
////                }
////                mApplicationStarted = false;
////            }
////            mApiClient = null;
////        }
////        mSelectedDevice = null;
////        mWaitingForReconnect = false;
//    }
//
//
//}
