package com.reaperberri.encrypture;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerActivity extends AppCompatActivity {

    private String videoPath;
    private SimpleExoPlayer player;

    /* Elements */
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoPath = getIntent().getStringExtra("videoPath");

        /* Elements */
        playerView = findViewById(R.id.video_player_view);

        // Initialize ExoPlayer
        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        playerView.setPlayer(player);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "yourApplicationName"));
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoPath));
        player.prepare(videoSource);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Release player
        player.release();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Pause player
        player.setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Resume player
        player.setPlayWhenReady(true);
    }
}
