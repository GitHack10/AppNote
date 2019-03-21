package ru.diitcenter.appnote.presentation.ui;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import ru.diitcenter.appnote.App;
import ru.diitcenter.appnote.presentation.CustomViewPager;
import ru.diitcenter.appnote.presentation.mvp.MainPresenter;
import ru.diitcenter.appnote.presentation.mvp.MainView;
import com.example.appnote.R;
import ru.diitcenter.appnote.domain.model.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    @InjectPresenter
    MainPresenter mainPresenter;

    @ProvidePresenter
    MainPresenter mainPresenter(){
        return new MainPresenter(this, App.getDataManager());
    }

    PagerAdapter pager;
    CustomViewPager viewPager;
    Button tryAgainButton;
    ProgressBar progressBar;
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
        timer = new Timer();
        viewPager = findViewById(R.id.viewPager);
        tryAgainButton = findViewById(R.id.Button_main_tryAgain);
        progressBar = findViewById(R.id.ProgressBar_main_progress);
        View v = viewPager;
        lockTouchScreen();

        //блокировка свайпов ViewPager
//        viewPager.disableScroll(true);

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

    @Override
    public void showMedia(Media media, boolean isOnline) {
//        try {
//            UtilsKt.cleanVideoCacheDir(this);
//        } catch (IOException e) {
//            // FIXME
//        }
        tryAgainButton.setVisibility(View.GONE);
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
        viewPager.setOffscreenPageLimit(media.getDataList().size());
        viewPager.setAdapter(pager);
    }

    @Override
    public void showNoNetwork(String message) {
        Toast myToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        myToast.show();
        tryAgainButton.setVisibility(View.VISIBLE);
        tryAgainButton.setOnClickListener((View v) -> {
            tryAgainButton.setVisibility(View.GONE);
            myToast.cancel();
            mainPresenter.load();
        });
    }

    @Override
    public void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
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
            runOnUiThread(() -> {
                if (pageScroll == fragmentList.size()) {
                    pageScroll=0;
                    viewPager.setCurrentItem(pageScroll);
                } else {
                    viewPager.setCurrentItem(pageScroll);
                    if(dtch) pageScroll++;
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
            //убираем границы экрана
            fullScreenMode();
        }
    }

    // блокировка касания
    private void lockTouchScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void fullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed() {
        // disable backPressed
    }
}
