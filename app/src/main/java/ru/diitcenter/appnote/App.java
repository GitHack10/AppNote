package ru.diitcenter.appnote;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import ru.diitcenter.appnote.data.global.DataManager;
import ru.diitcenter.appnote.data.network.ApiAppNote;
import ru.diitcenter.appnote.data.network.interceptors.CacheInterceptor;
import ru.diitcenter.appnote.data.network.interceptors.OfflineCacheInterceptor;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static final String BASE_URL = "http://plashet.diitcenter.ru";
    private static DataManager dataManager;
    private HttpProxyCacheServer proxy;

    @Override
    public void onCreate() {
        super.onCreate();

        long cacheSize = 100 * 1024 * 1024; // 100 MiB

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new OfflineCacheInterceptor(getApplicationContext()))
                .addNetworkInterceptor(new CacheInterceptor())
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

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(UtilsKt.getVideoCacheDir(this))
                .build();
    }
}
