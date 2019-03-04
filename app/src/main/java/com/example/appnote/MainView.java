package com.example.appnote;

import com.arellomobile.mvp.MvpView;
import com.example.appnote.model.Load;
import com.example.appnote.model.Media;

import java.util.List;

public interface MainView extends MvpView {
//    void showImage(List<Load> media, boolean isOnline);
    void showImage(Media media, boolean isOnline);
}
