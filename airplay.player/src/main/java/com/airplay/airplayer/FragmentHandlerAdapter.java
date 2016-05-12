package com.airplay.airplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import org.lee.android.util.Log;

import java.util.ArrayList;

/**
 * @author liruiyu Email:allnet@live.cn
 */
public class FragmentHandlerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private ArrayList<TabInfo> mTabs;
    private FragmentManager mFragmentManager;

    /**
     * Tab Object
     */
    public static final class TabInfo {
        public Class<?> cls;
        public String title;
        public Bundle args;

        public TabInfo(Class<?> _class, String _title, Bundle _args) {
            cls = _class;
            title = _title;
            args = _args;
        }
    }

    public FragmentHandlerAdapter(FragmentManager fm, Context context) {
        this(fm, context, new ArrayList<TabInfo>());
    }

    public FragmentHandlerAdapter(FragmentManager fm, Context context, ArrayList<TabInfo> tabInfos) {
        super(fm);
        mFragmentManager = fm;
        this.mContext = context;
        this.mTabs = tabInfos;
    }

    public FragmentHandlerAdapter addTab(TabInfo info) {
        mTabs.add(info);
        notifyDataSetChanged();
        return this;
    }

    public ArrayList<TabInfo> getTabs() {
        return mTabs;
    }

    public TabInfo getTabInfo(int position) {
        return mTabs.isEmpty() ? null : mTabs.get(position % mTabs.size());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        TabInfo info = getTabInfo(position);
        if (info == null) {
            return null;
        }
        return info.title;
    }

    @Override
    public int getCount() {
        return mTabs == null ? 0 : mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = getTabInfo(position);
        return Fragment.instantiate(mContext, info.cls.getName(), info.args);
    }

    public void clear() {
        if (mTabs != null) {
            mTabs.clear();
        }
    }

    private Fragment mShowFragment;

    public void show(TabInfo tab, int containerViewId) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mShowFragment != null) {
            transaction.hide(mShowFragment);
        }

        Fragment f = mFragmentManager.findFragmentByTag(tab.title);
        if (f == null) {
            f = Fragment.instantiate(mContext, tab.cls.getName(), tab.args);
            transaction.add(containerViewId, f, tab.title);
        } else {
            transaction.attach(f);
            transaction.show(f);
        }

        transaction.commitAllowingStateLoss();
        mShowFragment = f;
    }

    /**
     * 打印追踪信息
     */
    public void track() {
        int BackStackEntryCount = mFragmentManager.getBackStackEntryCount();
        int size = mFragmentManager.getFragments().size();

        StringBuffer buffer = new StringBuffer();
        for (int i = BackStackEntryCount; i > 0; i--) {
            FragmentManager.BackStackEntry backStackEntryAt = mFragmentManager.getBackStackEntryAt(i - 1);
            buffer.append(backStackEntryAt.getName() + ", title:" + backStackEntryAt.getBreadCrumbTitle() + "\n");
        }

        Log.anchor("BackStackEntryCount:" + BackStackEntryCount + ", size:" + size
                + "\n" + buffer.toString());
    }
}