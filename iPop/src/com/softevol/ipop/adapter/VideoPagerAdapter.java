package com.softevol.ipop.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: android
 * Date: 14.03.13
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class VideoPagerAdapter extends FragmentPagerAdapter {

    private List<String> fileNames;

    public VideoPagerAdapter(FragmentManager fm, List<String> fileNames) {
        super(fm);
        this.fileNames = fileNames;
    }

    public VideoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        String file = fileNames != null ? fileNames.get(i) : null;
        if (file != null){
            VideoFragment fragment = new VideoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ImageFragment.EXTRA_FILE, file);
            fragment.setArguments(bundle);
            return fragment;
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return  fileNames != null ? fileNames.size() : 0;
    }
}
