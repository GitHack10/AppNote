package com.example.appnote;

import com.arellomobile.mvp.MvpView;
import com.example.appnote.model.Load;
import com.example.appnote.model.Media;

import java.util.List;

public interface MainView extends MvpView {
//    void showImage(List<Load> list, boolean isOnline);
    void showImage(List<Media> list, boolean isOnline);
}
