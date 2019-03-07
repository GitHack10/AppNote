package com.example.appnote.presentation.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {

    List<SlideFragment> list;
    public int page;

    public PagerAdapter(FragmentManager fm, List<SlideFragment> list, int page) {
        super(fm);
        this.list = list;
        this.page = page;
    }

    @Override
    public Fragment getItem(int i) {
            return list.get(i);
    }

    public void startPlayer(int i){
        list.get(i).startPlayer();
    }

    public void stopPlayer(int i){
        list.get(i).stopPlayer();
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
