package com.dlna;

import android.content.Context;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;

import com.abooc.util.Debug;

import org.lee.java.util.Empty;

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


    private Discover(Context context) {
        Flint.setVerboseLoggingEnabled(false);
        mMediaRouter = MediaRouter.getInstance(context);

        mMediaRouteSelector = new MediaRouteSelector.Builder()
//                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                .addControlCategory(FlintMediaControlIntent.categoryForFlint(APPLICATION_ID))
                .build();
        mMediaRouterCallback = new MediaRouterCallback();
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

    private Device mSelectedDevice;

    public void setDeviceState(Device device, Connectable.State state) {
        mSelectedDevice = device;
        mSelectedDevice.changeState(state);
    }

    public Device getSelectedDevice() {
        return mSelectedDevice;
    }

    /**
     * 是否有媒体设备
     *
     * @return
     */
    public boolean noRoutes() {
        return Empty.isEmpty(deviceList);
    }

    public void addLocalDevice() {
//        mSelectedDevice = new Device();
//        mSelectedDevice.changeState(Connectable.State.CONNECTED);
//        mSelectedDevice.setName(mMediaRouter.getDefaultRoute().getName());
//        deviceList.add(0, mSelectedDevice);
    }

    /**
     * 获取当前检索到的设备
     *
     * @return
     */
    public ArrayList<Device> getDevicesList() {
        return deviceList;
    }

    public void start() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
        int flags = MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY | MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN;
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback, flags);
    }

    public void stop() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    private class MediaRouterCallback extends MediaRouter.Callback {
        @Override
        public void onRouteAdded(MediaRouter router, RouteInfo route) {
            Debug.anchor(route.toString());
            FlintDevice flintDevice = FlintDevice.getFromBundle(route.getExtras());
//            String name = flintDevice.getFriendlyName();
//            if (name != null && name.contains("兔子视频")) {
//                return;
//            }

            Device device = new Device();
            device.setFlintDevice(flintDevice);
            if (!deviceList.contains(device)) {
                deviceList.add(device);

                if (mSelectedDevice != null && mSelectedDevice.equals(device)) {
                    device.changeState(mSelectedDevice.getState());
                }

                if (mOnDiscoverListener != null) {
                    mOnDiscoverListener.onDiscoveryChanged(deviceList);
                }
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

                if (mOnDiscoverListener != null) {
                    mOnDiscoverListener.onDiscoveryChanged(deviceList);
                }
            }
        }
    }

}
