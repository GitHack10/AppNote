package com.example.appnote.presentation.mvp;

import android.content.Context;
import android.content.SharedPreferences;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.appnote.data.global.DataManager;
import com.example.appnote.domain.model.Media;
import com.example.appnote.presentation.ui.MainActivity;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private DataManager dataManager;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NAME = "Name";
    SharedPreferences mSettings;
    List<Media.Data> listStr = new ArrayList<>();
    Media mediaStr = new Media("", new ArrayList<Media.Data>());

    Media media = new Media("", new ArrayList<Media.Data>());
    List<Media.Data> list = new ArrayList<>();
    MainActivity mainActivity;

    public MainPresenter(MainActivity mainActivity, DataManager dataManager) {
        this.mainActivity = mainActivity;
        this.dataManager = dataManager;
        mSettings = mainActivity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        load();
    }

    private void load() {

        dataManager.getAllMedia().enqueue(new Callback<Media>() {
            @Override
            public void onResponse(@NotNull Call<Media> call, @NotNull Response<Media> response) {
                if (response.isSuccessful() && getViewState() != null) {
                    if (response.body() != null) {
                        media = response.body();
                        list = media.getDataList();
                    }
                    String url = "https://videos1.ochepyatki.ru/53122/video_53122.mp4";
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getType() == 1) {
                            list.get(i).setUrl(url);
                        }
                        list.get(i).setName("name" + (i+1));
                    }
                    getViewState().showMedia(media, true);
//                    checkOnline();
                }
            }

            @Override
            public void onFailure(Call<Media> call, Throwable t) {
                // FIXME
//                try {
//                    listStr = new ArrayList<>();
//                    int ret = mSettings.getInt(APP_PREFERENCES_NAME, 1);
//
//                    for (int i = 0; i < ret; i++) {
//                        listStr.add(new Media.Data(0, "", ""));
//                        if (mSettings.getBoolean(i + "b", true)) {
//                            listStr.get(i).setName(mSettings.getString(i + "a", ""));
////                        listStr.get(i).setType(mSettings.getInt(i + "b", 0));
//                        } else { }
//                        mediaStr.setDataList(listStr);
//                    }
//                    getViewState().showMedia(mediaStr, false);
//                } catch (Exception e) {
                    getViewState().showNoNetwork("Нет подключения к интернету");
//                }
            }
        });
    }
}
