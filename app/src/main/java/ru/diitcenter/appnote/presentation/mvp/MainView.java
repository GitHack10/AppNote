package ru.diitcenter.appnote.presentation.mvp;

import com.arellomobile.mvp.MvpView;
import ru.diitcenter.appnote.domain.model.Media;

public interface MainView extends MvpView {

    void showMedia(Media media, boolean isOnline);

    void showNoNetwork(String message);

    void showProgress(boolean show);
}
