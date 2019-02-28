package com.example.appnote;

import com.arellomobile.mvp.MvpView;
import com.example.appnote.model.Load;

import java.util.List;

public interface MvpViewMain extends MvpView {
    public void showImage(List<Load> list, boolean isOnline);
}
