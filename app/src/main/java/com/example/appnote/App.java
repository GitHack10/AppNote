package com.example.appnote;

import android.app.Application;
import com.example.appnote.data.global.DataManager;
import com.example.appnote.data.network.ApiAppNote;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static final String BASE_URL = "http://plashet.diitcenter.ru";
    private static DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        long cacheSize = 50 * 1024 * 1024; // 50 MiB

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .cache(new Cache(getApplicationContext().getCacheDir(), cacheSize))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiAppNote apiAppNote = retrofit.create(ApiAppNote.class);

        dataManager = new DataManager(apiAppNote);
    }

    public static DataManager getDataManager() {
        return dataManager;
    }
}
