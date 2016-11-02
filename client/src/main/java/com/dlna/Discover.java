package com.dlna;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;

import com.abooc.util.Debug;

import java.util.ArrayList;

import tv.matchstick.flint.Flint;
import tv.matchstick.flint.FlintDevice;
import tv.matchstick.flint.FlintMediaControlIntent;

/**
 * 发现者,去探索新世界,发现新地标
 * Created by author:大宇
 * email:allnet@live.cn
 */
public class Discover {

    public interface OnDiscoverListener {
        void onDiscoveryChanged(ArrayList<Device> list);
    }

    private static Discover ourInstance;

    private OnDiscoverListener mOnDiscoverListener;
    private MediaRouter mMediaRouter;
    private MediaRouterCallback mMediaRouterCallback;
    private MediaRouteSelector mMediaRouteSelector;
    private ArrayList<Device> deviceList = new ArrayList<>();
    private static final String APPLICATION_ID = "~flintplayer";

    private Context mContext;
    private WiFi mWiFi;

    private Discover(Context context) {
        mWiFi = new WiFi();
        mContext = context;
        context.registerReceiver(mWiFi, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        mMediaRouter = MediaRouter.getInstance(mContext);

        mMediaRouteSelector = new MediaRouteSelector.Builder()
//                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                .addControlCategory(FlintMediaControlIntent.categoryForFlint(APPLICATION_ID))
                .build();
        mMediaRouterCallback = new MediaRouterCallback();
        Flint.setVerboseLoggingEnabled(false);
    }

    public static Discover creator(Context context) {
        if (ourInstance == null) {
            ourInstance = new Discover(context.getApplicationContext());
        }
        return ourInstance;
    }

    public static Discover getInstance() {
        return ourInstance;
    }

    public void registerDiscoverListener(OnDiscoverListener listener) {
        this.mOnDiscoverListener = listener;
    }

    /**
     * 获取当前检索到的设备
     *
     * @return
     */
    public ArrayList<Device> getDevicesList() {
        return deviceList;
    }

    private boolean running;

    public void start() {
        if (!running) {
            running = true;
            int flags = MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY | MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN;
            mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback, flags);
            Debug.anchor("扫描开启...");
        }
    }

    public void restart() {
        stop();
        restart.cancel();
        restart.start();
    }

    public void stop() {
        running = false;
        mMediaRouter.removeCallback(mMediaRouterCallback);
        Debug.anchor("扫描关闭");
    }

    public void destroy() {
        mContext.unregisterReceiver(mWiFi);
        stop();
    }

    private class MediaRouterCallback extends MediaRouter.Callback {
        @Override
        public void onRouteAdded(MediaRouter router, RouteInfo route) {
            FlintDevice flintDevice = FlintDevice.getFromBundle(route.getExtras());
            Debug.anchor(flintDevice.getIpAddress());
            Device device = new Device();
            device.setFlintDevice(flintDevice);
            if (!deviceList.contains(device)) {
                if (deviceList.isEmpty()) {
                    deviceList.add(device);
                } else if (getIpLastSegment() < getIpLastSegment(device.getIp())) {
                    deviceList.add(device);
                } else {
                    int newIpInt = getIpLastSegment(device.getIp());
                    for (int i = 0; i < deviceList.size(); i++) {
                        int ipInt = getIpLastSegment(deviceList.get(i).getIp());
                        if (newIpInt > ipInt) {
                            continue;
                        }
                        deviceList.add(i, device);
                        break;
                    }
                }

            }
            if (mOnDiscoverListener != null) {
                mOnDiscoverListener.onDiscoveryChanged(deviceList);
            }
        }

        @Override
        public void onRouteRemoved(MediaRouter router, RouteInfo route) {
            FlintDevice flintDevice = FlintDevice.getFromBundle(route.getExtras());
            String ip = flintDevice.getIpAddress().toString();
            Debug.anchor(ip);

            Device device = new Device();
            device.setFlintDevice(flintDevice);
            if (deviceList.contains(device)) {
                deviceList.remove(device);
            }
            if (mOnDiscoverListener != null) {
                mOnDiscoverListener.onDiscoveryChanged(deviceList);
            }
        }
    }

    private int getIpLastSegment() {
        String ip = deviceList.get(deviceList.size() - 1).getIp();
        return getIpLastSegment(ip);
    }

    private int getIpLastSegment(String ip) {
        return Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1));
    }

    private boolean closeYes = true;
    private boolean openYes = true;

    private class WiFi extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Debug.anchor(intent.toString());
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLING:
                        Debug.anchor("WiFi 关闭中...");
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        Debug.anchor("WiFi 不可用");
                        if (closeYes) {
                            closeYes = false;
                            openYes = true;

                            deviceList.clear();
                            if (mOnDiscoverListener != null) {
                                mOnDiscoverListener.onDiscoveryChanged(null);
                            }
                            Debug.anchor("停止扫描");
                            timer.cancel();
                            stop();
                        }
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        Debug.anchor("WiFi 开启...");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Debug.anchor("WiFi 可用");
                        if (openYes) {
                            openYes = false;
                            closeYes = true;

                            if (!running) {
                                Debug.anchor("扫描程序即将启动...");
                                timer.start();
                            }
                        }
                        break;
                }
            }
        }
    }

    private CountDownTimer timer = new CountDownTimer(4 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Debug.anchor("扫描程序即将启动..." + (millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            Debug.anchor("开始检索设备...");
            Discover.getInstance().start();
        }
    };

    private CountDownTimer restart = new CountDownTimer(3 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (!running) {
                Debug.anchor("开始检索设备...");
                Discover.getInstance().start();
            }
        }

        @Override
        public void onFinish() {
            if (mOnDiscoverListener != null && deviceList.isEmpty()) {
                mOnDiscoverListener.onDiscoveryChanged(null);
            }
        }
    };
}
