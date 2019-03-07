package com.example.appnote.presentation.ui;

import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.appnote.App;
import com.example.appnote.presentation.mvp.MainPresenter;
import com.example.appnote.presentation.mvp.MainView;
import com.example.appnote.R;
import com.example.appnote.domain.model.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    @InjectPresenter
    MainPresenter mainPresenter;

    @ProvidePresenter
    MainPresenter mainPresenter(){
        return new MainPresenter(this, App.getDataManager());
    }

    PagerAdapter pager;
    ViewPager viewPager;
    int page=0;

    Timer timer;
    int pageScroll = 0;

    List<SlideFragment> fragmentList = new ArrayList<>();
    Media media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
//        lockTouchScreen();
        timer = new Timer();
        viewPager = findViewById(R.id.viewPager);

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
                if (media.getDataList().get(i).getType() == 1) stopTimer();
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

        pageSwitcher(5000, true);
    }

    // блокировка касания
    private void lockTouchScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void showMedia(Media media, boolean isOnline) {
        this.media = media;
        for (int i = 0; i < media.getDataList().size(); i++) {
            fragmentList.add(SlideFragment.newInstance(
                    media.getText(),
                    media.getDataList().get(i).getUrl(),
                    media.getDataList().get(i).getType(),
                    i, isOnline)
            );
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
                    if (pageScroll == fragmentList.size()) {
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

    @Override
    protected void onResume() {
        super.onResume();
        //убираем границы экрана
        fullScreenMode();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            fullScreenMode();
        }
    }

    private void fullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onBackPressed() {
        // disable backPressed
    }
}
