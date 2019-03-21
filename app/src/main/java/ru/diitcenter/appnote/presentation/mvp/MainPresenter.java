package ru.diitcenter.appnote.presentation.mvp;

import android.content.Context;
import android.content.SharedPreferences;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import ru.diitcenter.appnote.data.global.DataManager;
import ru.diitcenter.appnote.domain.model.Media;
import ru.diitcenter.appnote.presentation.ui.MainActivity;
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
    SharedPreferences mSettings;

    Media media = new Media("", new ArrayList<>());
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

    public void load() {

        getViewState().showProgress(true);
        dataManager.getAllMedia().enqueue(new Callback<Media>() {
            @Override
            public void onResponse(@NotNull Call<Media> call, @NotNull Response<Media> response) {
                if (response.isSuccessful() && getViewState() != null) {
                    if (response.body() != null) {
                        media = response.body();
                        list = media.getDataList();
                    }
//                    String url = "https://videos1.ochepyatki.ru/53122/video_53122.mp4";
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setName("name" + (i+1));
                    }
                    getViewState().showProgress(false);
                    getViewState().showMedia(media, true);
//                    checkOnline();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Media> call, @NotNull Throwable t) {
                // FIXME
                getViewState().showProgress(false);
                getViewState().showNoNetwork("Нет подключения к интернету");
            }
        });
    }
}
