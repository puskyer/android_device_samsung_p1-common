/*
 * Copyright (C) 2013 The OmniROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnirom.device;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import org.omnirom.device.R;

import java.util.ArrayList;

public class DeviceSettings extends Activity {

    public static final String SHARED_PREFERENCES_BASENAME = "org.omnirom.device";
    public static final String ACTION_UPDATE_PREFERENCES = "org.omnirom.device.UPDATE";
    public static final String KEY_CATEGORY_RADIO = "category_radio";
    public static final String KEY_HSPA = "hspa";
    public static final String KEY_TVOUT_ENABLE = "tvout_enable";
    public static final String KEY_TVOUT_SYSTEM = "tvout_system";
    public static final String KEY_HDMI_ENABLE = "hdmi_enable";
    public static final String KEY_BUTTONS_DISABLE = "buttons_disable";
    public static final String KEY_BUTTONS = "buttons_category";
    public static final String KEY_BACKLIGHT_TIMEOUT = "backlight_timeout";
    public static final String KEY_VIBRATOR_TUNING = "vibrator_tuning";
    public static final String KEY_ZRAM_DISABLE = "zram_disable";
    public static final String KEY_ZRAM_NUM_DEVICE = "zram_num_device";
    public static final String KEY_ZRAM_COMP_STREAM = "zram_comp_stream";
    public static final String KEY_ZRAM_COMP_ALGORITHM = "zram_comp_algorithm";
    public static final String KEY_ZRAM_DISK_SIZE = "zram_disk_size";

    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        bar.setTitle(R.string.app_name);
        bar.setDisplayHomeAsUpEnabled(true);

        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.category_buttons_title),
                ButtonFragmentActivity.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.category_radio_title),
                RadioFragmentActivity.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.category_zram_title),
                ZramFragmentActivity.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.category_tvout_title),
                TVFragmentActivity.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.category_vibrator_tuning_title),
                VibratorFragmentActivity.class, null);
        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    public static class TabsAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public TabsAdapter(Activity activity, ViewPager pager) {
            super(activity.getFragmentManager());
            mContext = activity;
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i=0; i<mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            DeviceSettings.this.onBackPressed();
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
