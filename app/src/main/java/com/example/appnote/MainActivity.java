package com.example.appnote;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.arellomobile.mvp.MvpActivity;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.appnote.model.Load;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends MvpAppCompatActivity implements MvpViewMain {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    @InjectPresenter
    MainPresenter mainPresenter;

    @ProvidePresenter
    MainPresenter mainPresenter(){
        return new MainPresenter(this);
    }

    PagerAdapter pager;
    ViewPager viewPager;
    int page=0;

    Timer timer;
    int pageScroll = 0;

    List<FragmentPager> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        timer = new Timer();

        viewPager = findViewById(R.id.viewPager);

      /*  if(isOnline()){
            for(int i=0; i<list.size();i++) {
                deleteImage(getApplicationContext(),list.get(i).getName());
            }
        }*/



       /* DownloadVideo video = new DownloadVideo();
        video.execute("");*/

        //DownloadImage downloadImage = new DownloadImage();
        //downloadImage.setName("name");
        //downloadImage.execute("https://img.lookmytrips.com/images/look5p3n/big-56e6d0f4ff93672409064ce0-56ec50d262f29-1beok6i.jpg");

      //  new DownloadImage().execute("https://img1.goodfon.ru/original/1920x1080/f/cd/nebo-okean-zakat.jpg");

       // imageView.setImageBitmap(loadImageBitmap(getApplicationContext(), "name"));
        //imageView2.setImageBitmap(loadImageBitmap(getApplicationContext(), "my_image2.png"));

        //удаляем кешированные файлы при новом подключении

        View v = viewPager;
        //блокировка касания
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });



        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                pager.startPlayer(i);
                pager.stopPlayer(page);
                page=i;
                pageScroll=i;
                if (i==0){
                    //прописать условие для видео
                    //если на первом слайде видео, то ниже стоящий код не применяем т.к. слайд будет осуществляться только при окончании видео
                    stopTimer();
                    pageSwitcher(5000,true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        pageSwitcher(3000, true);
    }


//    public Bitmap loadImageBitmap(Context context, String imageName) {
//        Bitmap bitmap = null;
//        FileInputStream fiStream;
//        try {
//          //  context.deleteFile(imageName);
//            fiStream    = context.openFileInput(imageName);
//            bitmap      = BitmapFactory.decodeStream(fiStream);
//            fiStream.close();
//        } catch (Exception e) {
//            Log.d("saveImage", "Exception 3, Something went wrong!");
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

    @Override
    public void showImage(List<Load> list, boolean isOnline) {
        for(int i=0;i<list.size();i++){
            fragmentList.add(FragmentPager.newInstanse(list.get(i).getName(), list.get(i).isImage(), i, isOnline));
        }

        pager = new PagerAdapter(getSupportFragmentManager(), fragmentList, page);
        viewPager.setOffscreenPageLimit(8);
        viewPager.setAdapter(pager);
    }

    boolean dtch=true;
    public void pageSwitcher(long period, boolean dtch) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(), 0, period);
        this.dtch = dtch;
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (pageScroll == 5) {
                        pageScroll=0;
                        viewPager.setCurrentItem(pageScroll);
                    } else {
                        viewPager.setCurrentItem(pageScroll);
                        if(dtch) pageScroll++;
                    }
                }
            });

        }
    }

    public void stopTimer(){
        if(timer!=null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }




    private class DownloadVideo extends AsyncTask<String,String,String> {
        ProgressDialog PD;
        @Override
        protected void onPreExecute() {
         //   PD= ProgressDialog.show(MainActivity.this,null, "Please Wait ...", true);
       //     PD.setCancelable(true);
        }

        @Override
        protected String doInBackground(String... arg0) {
         //   downloadFile("https://ia800208.us.archive.org/4/items/Popeye_forPresident/Popeye_forPresident_512kb.mp4","Saple.mp4");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
           // PD.dismiss();
        }

    }

    private void downloadFile(String fileURL, String fileName) {
        try {
            String rootDir = Environment.getExternalStorageDirectory()
                    + File.separator + "Video";
            File rootFile = new File(rootDir);
            rootFile.mkdir();
            URL url = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(new File(rootFile,
                    fileName));
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (IOException e) {
            Log.d("Error....", e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //убираем границы экрана
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static class customViewGroup extends ViewGroup {

        public customViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return true;
        }
    }

   //блокировка шторки в случае необходимости
    public void block(){
         /*  WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        customViewGroup view = new customViewGroup(this);

        manager.addView(view, localLayoutParams);*/
    }

    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}

//Kotlin говно
