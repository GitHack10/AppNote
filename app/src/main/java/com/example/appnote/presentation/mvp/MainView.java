package com.example.appnote.presentation.mvp;

import com.arellomobile.mvp.MvpView;
import com.example.appnote.domain.model.Media;

public interface MainView extends MvpView {

    void showMedia(Media media, boolean isOnline);

    void showNoNetwork(String message);
}
