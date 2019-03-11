package com.example.appnote.presentation.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.appnote.App;
import com.example.appnote.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import java.io.File;

public class SlideFragment extends Fragment implements CacheListener {

    private static final String LOG_TAG = "SlideFragment";
    private final static String TEXT = "text";
    private final static String URL = "url";
    private final static String PAGE = "page";
    private final static String TYPE = "type";
    private final static String IS_ONLINE = "online";
    int page;

    public static SlideFragment newInstance(String text, String url, int type, int page, boolean isOnline){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT, text);
        bundle.putString(URL, url);
        bundle.putInt(PAGE, page);
        bundle.putInt(TYPE, type);
        bundle.putBoolean(IS_ONLINE, isOnline);
        SlideFragment slideFragment = new SlideFragment();
        slideFragment.setArguments(bundle);
        return slideFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    PlayerView playerView;
    ImageView imageView;
    TextView pagerTextView;
    SimpleExoPlayer mediaPlayer;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        showMedia();
    }

    private void initViews(View view) {
        imageView = view.findViewById(R.id.imageView);
        playerView = view.findViewById(R.id.playerView);
        pagerTextView = view.findViewById(R.id.TextView_pager_text);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void showMedia() {
        page = getArguments().getInt(PAGE);
        pagerTextView.setText(getArguments().getString(TEXT));
        if(getArguments().getInt(TYPE) == 1 && getArguments().getBoolean(IS_ONLINE)) {
            initializePlayer(getArguments().getString(URL));
            if (page == 0) startPlayer();
            imageView.setVisibility(View.INVISIBLE);
        } else {
            playerView.setVisibility(View.INVISIBLE);
            Picasso.get()
                    .load(getArguments().getString(URL))
                    .into(imageView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        App.getProxy(getActivity()).unregisterCacheListener(this);
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        progressBar.setSecondaryProgress(percentsAvailable);
        Log.d(LOG_TAG, String.format("onCacheAvailable. percents: %d, file: %s, url: %s", percentsAvailable, cacheFile, url));
    }

    public void startPlayer(){
        if(mediaPlayer!=null) mediaPlayer.setPlayWhenReady(true);
    }

    public void stopPlayer(){
        if (mediaPlayer!=null) mediaPlayer.setPlayWhenReady(false);
    }

    private void initializePlayer(String url){

        //Initialize the player
        mediaPlayer = newSimpleExoPlayer();
        HttpProxyCacheServer proxy = App.getProxy(getActivity());
        proxy.registerCacheListener(this, url);
        String proxyUrl = proxy.getProxyUrl(url);
        Log.d(LOG_TAG, "Use proxy url " + proxyUrl + " instead of original url " + url);

        //Initialize simpleExoPlayerView]
        playerView.setPlayer(mediaPlayer);

        // Prepare the player with the source.
        MediaSource videoSource = newVideoSource(proxyUrl);
        mediaPlayer.prepare(videoSource);

        mediaPlayer.addListener(new Player.EventListener() {
            int a=0;
            int b=0;
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if(playWhenReady){
                    if(a==0) {
                        ((MainActivity)getActivity()).stopTimer();
                        a++;
                        b=0;
                    }
                }

                progressBar.setVisibility(playbackState==Player.STATE_BUFFERING ? View.VISIBLE : View.INVISIBLE);

                if(playbackState == Player.STATE_ENDED){
                    if(b==0) {
                        ((MainActivity) getActivity()).pageSwitcher(5000, true);
                        a=0;
                        b++;
                        mediaPlayer.setPlayWhenReady(false);
                        mediaPlayer.seekTo(0);
                    }
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    // Create a default TrackSelector
    private SimpleExoPlayer newSimpleExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        return ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
    }

    // This is the MediaSource representing the media to be played.
    private MediaSource newVideoSource(String url) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        String userAgent = Util.getUserAgent(getActivity(), "AndroidVideoCache appnote");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), userAgent, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
    }
}
