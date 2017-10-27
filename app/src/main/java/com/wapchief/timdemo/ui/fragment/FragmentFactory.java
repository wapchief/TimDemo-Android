package com.wapchief.timdemo.ui.fragment;

import android.support.v4.app.Fragment;

import java.util.HashMap;

/**
 * Created by wapchief on 2017/9/6.
 * Fragment工厂
 */

public class FragmentFactory {
    private static HashMap<Integer, Fragment> fragments;

    public static Fragment createFragment(int position) {
        fragments = new HashMap<Integer, Fragment>();
        Fragment fragment = fragments.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    fragment = new FriendsFragment();
                    break;
                case 2:
                    fragment = new SettingFragment();
                    break;
                default:
                    break;
            }
            fragments.put(position, fragment);
        }
        return fragment;
    }
}
