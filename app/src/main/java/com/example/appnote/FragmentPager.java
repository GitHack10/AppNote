package com.example.appnote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
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

import java.io.FileInputStream;
import java.util.Timer;

public class FragmentPager extends Fragment {


    private final static String KEY="image";

    public static FragmentPager newInstanse(String s,boolean isImg, int page, boolean isOnline){
        Bundle bundle = new Bundle();
        bundle.putString(KEY, s);
        bundle.putInt("PAGE", page);
        bundle.putBoolean("Image", isImg);
        bundle.putBoolean("online", isOnline);
        FragmentPager fragmentPager = new FragmentPager();
        fragmentPager.setArguments(bundle);
        return fragmentPager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    PlayerView playerView;
    ImageView imageView;
    SimpleExoPlayer mediaPlayer;
    int page;
    ProgressBar pb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pager, null);

        imageView = v.findViewById(R.id.imageView);
        playerView = v.findViewById(R.id.playerView);
        page = getArguments().getInt("PAGE");
        pb = v.findViewById(R.id.progressBar);

        if(!getArguments().getBoolean("Image")) {
            if(getArguments().getBoolean("online")) {
                initializePlayer();
                if (page == 0) startPlayer();
                imageView.setVisibility(View.INVISIBLE);
            }else {
                //чтение файла из сохраненки
            }
        }else {
            playerView.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(loadImageBitmap(getContext().getApplicationContext(), getArguments().getString(KEY)));
        }

        return v;
    }

    public Bitmap loadImageBitmap(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            //  context.deleteFile(imageName);
            fiStream    = context.openFileInput(imageName);
            bitmap      = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 3, Something went wrong!");
            e.printStackTrace();
        }
        return bitmap;
    }


    public void startPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.setPlayWhenReady(true);

            //позже проверить возвращаетли функция длину видео
            long a = mediaPlayer.getDuration();
        }
    }
    public void stopPlayer(){
        if (mediaPlayer!=null) {
            mediaPlayer.setPlayWhenReady(false);
        }
    }


    private void initializePlayer(){
        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        mediaPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

        //Initialize simpleExoPlayerView]
        playerView.setPlayer(mediaPlayer);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "CloudinaryExoplayer"));

        // Produces Extractor instances for parsing the media data..
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        Uri videoUri = Uri.parse("https://ia800208.us.archive.org/4/items/Popeye_forPresident/Popeye_forPresident_512kb.mp4");
        MediaSource videoSource = new ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
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

                if(playbackState==Player.STATE_BUFFERING){
                    pb.setVisibility(View.VISIBLE);

                }else {
                    pb.setVisibility(View.INVISIBLE);
                }

                if(playbackState == Player.STATE_ENDED){
                    if(b==0) {
                        ((MainActivity) getActivity()).pageSwitcher(2500, true);
                        a=0;
                        b++;
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
}
