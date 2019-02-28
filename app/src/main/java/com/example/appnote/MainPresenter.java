package com.example.appnote;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;
import com.example.appnote.model.Load;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@InjectViewState
public class MainPresenter extends MvpPresenter<MvpViewMain> {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NAME = "Name";
    SharedPreferences mSettings;
    List<Load> listStr = new ArrayList<>();

    DownloadManager downloadManager;
    String _URL = "https://ia800208.us.archive.org/4/items/Popeye_forPresident/Popeye_forPresident_512kb.mp4";
    long refer;
    BroadcastReceiver downloadcomplete;
    BroadcastReceiver notificationClick;

    List<Load> list = new ArrayList<>();
    MainActivity mainActivity;

    MainPresenter(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        mSettings = mainActivity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        load();
    }

    private void load(){

        list.add(new Load());
        list.add(new Load());
        list.add(new Load());
        list.add(new Load());
        list.add(new Load());

        list.get(0).setUrl("https://img.lookmytrips.com/images/look5p3n/big-56e6d0f4ff93672409064ce0-56ec50d262f29-1beok6i.jpg");
        list.get(1).setUrl("https://img1.goodfon.ru/original/1920x1080/f/cd/nebo-okean-zakat.jpg");
        list.get(2).setUrl("https://s1.1zoom.ru/b5050/837/Bridges_Skyscrapers_USA_449757_1920x1200.jpg");
        list.get(3).setUrl("https://ia800208.us.archive.org/4/items/Popeye_forPresident/Popeye_forPresident_512kb.mp4");
        list.get(4).setUrl("https://ia800208.us.archive.org/4/items/Popeye_forPresident/Popeye_forPresident_512kb.mp4");
        list.get(0).setName("name1");
        list.get(1).setName("name2");
        list.get(2).setName("name3");
        list.get(3).setName("name4");
        list.get(4).setName("name5");
        list.get(0).setImage(true);
        list.get(1).setImage(true);
        list.get(2).setImage(true);
        list.get(3).setImage(false);
        list.get(4).setImage(false);

       // downloadVideo("https://ia800208.us.archive.org/4/items/Popeye_forPresident/Popeye_forPresident_512kb.mp4");

        if(isOnline()) {

                Toast.makeText(mainActivity.getApplicationContext(), mSettings.getInt(APP_PREFERENCES_NAME, 0)+"", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < mSettings.getInt(APP_PREFERENCES_NAME, 0); i++) {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                            +"/"+mSettings.getString(i+"a", ""));
                    file.delete();
                    try {

                        if (file.exists()) {
                            file.getCanonicalFile().delete();
                            if (file.exists()) {
                                mainActivity.getApplicationContext().deleteFile(file.getName());
                            }
                        }
                    }catch (Exception e){}
                }

            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).isImage()) {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(i + "a", list.get(i).getName());
                    editor.putBoolean(i + "b", list.get(i).isImage());
                    editor.apply();
                }else {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(i + "a", list.get(i).getName());
                    editor.putBoolean(i + "b", list.get(i).isImage());
                    editor.apply();
                }
            }

            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).isImage()) {
                    DownloadImage downloadImage = new DownloadImage();
                    downloadImage.setName(list.get(i).getName());
                    downloadImage.execute(list.get(i).getUrl());
                }else {
                   // startdownload(list.get(i).getName(), list.get(i).getUrl(),i);
                }
            }

            listStr = new ArrayList<>();

            int ret = mSettings.getInt(APP_PREFERENCES_NAME, list.size());

           // Toast.makeText(mainActivity.getApplicationContext(), mSettings.getInt(APP_PREFERENCES_NAME, 0)+"", Toast.LENGTH_SHORT).show();

            for (int i = 0; i < ret; i++) {
                listStr.add(new Load());
                listStr.get(i).setName(mSettings.getString(i + "a", ""));
                listStr.get(i).setImage(mSettings.getBoolean(i + "b", true));
            }

            getViewState().showImage(listStr, true);
        }else {
            try {
                listStr = new ArrayList<>();

                int ret = mSettings.getInt(APP_PREFERENCES_NAME, 1);


                for (int i=0;i<ret;i++){
                    listStr.add(new Load());
                    if(mSettings.getBoolean(i+"b", true)) {
                        listStr.get(i).setName(mSettings.getString(i + "a", ""));
                        listStr.get(i).setImage(mSettings.getBoolean(i + "b", true));
                    }else {
                    }

                }
                getViewState().showImage(listStr, false);
            }catch (Exception e){
                //экран нет подключения к интернету
            }

        }


    }

    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                mainActivity.getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }


    private class DownloadImage extends AsyncTask<String, String, Bitmap> {
        private String TAG = "DownloadImage";
        private String name;


        public void setName(String name) {
            this.name = name;
        }

        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(mainActivity.getApplicationContext(), result, name);
        }
    }

    public void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();

        } catch (Exception e) {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }

    public void startdownload(String name, String url, final int i) {
        downloadManager = (DownloadManager) mainActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDescription("My download").setTitle(name);
        request.setDestinationInExternalFilesDir(mainActivity, Environment.DIRECTORY_DOWNLOADS, name);
        request.setVisibleInDownloadsUi(true);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        refer = downloadManager.enqueue(request);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        notificationClick = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String extraID = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long[] references = intent.getLongArrayExtra(extraID);
                for (long r : references) {
                    if (r == refer) {
                        // do something with download file                    }
                    }
                }
            }
        };

        mainActivity.registerReceiver(notificationClick, filter);

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadcomplete =new
                BroadcastReceiver() {
                    @Override
                    public void onReceive (Context context, Intent intent){
                        long r = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        if (refer == r) {
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(r);
                            Cursor cursor = downloadManager.query(query);
                            cursor.moveToFirst();
                            //get status of the download
                            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            int status = cursor.getInt(columnIndex);
                            int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                            String saveFilePath = cursor.getString(filenameIndex);
                            int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                            int reason = cursor.getInt(columnReason);
                            switch (status) {
                                case DownloadManager.STATUS_SUCCESSFUL:
                                    break;
                                case DownloadManager.STATUS_FAILED:
                                    // do something                            break;
                                case DownloadManager.STATUS_PAUSED:
                                    // do something                            break;
                                case DownloadManager.STATUS_PENDING:
                                    // do something                            break;
                                case DownloadManager.STATUS_RUNNING:
                                    // do something                            break;
                            }
                        }
                    }
                };

        mainActivity.registerReceiver(downloadcomplete, intentFilter);
    }
}
