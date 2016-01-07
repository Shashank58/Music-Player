package cybrilla.musicplayer.android;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.util.MusicPlayerHelper;

public class SongDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TITLE_NAME = "TITLE_NAME";
    public static final String ALBUM_COVER = "ALBUM_COVER";
    private TextView detailSelectedTrack;
    private ImageView detailController, detailFastForward, detailReverse;
    private SeekBar musicSeeker;
    Handler seekHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_song_detail);
        detailSelectedTrack = (TextView) findViewById(R.id.detail_selected_track);
        detailController = (ImageView) findViewById(R.id.detail_controller);
        detailFastForward = (ImageView) findViewById(R.id.detail_fast_forward);
        detailReverse = (ImageView) findViewById(R.id.detail_reverse);
        musicSeeker = (SeekBar) findViewById(R.id.music_seeker);

        if (MusicPlayerHelper.mediaPlayer.isPlaying()) {
            detailController.setImageResource(android.R.drawable.ic_media_pause);
        }
        detailSelectedTrack.setText(getIntent().getStringExtra(TITLE_NAME));
        detailController.setOnClickListener(this);
        detailFastForward.setOnClickListener(this);
        detailReverse.setOnClickListener(this);
        musicSeeker.setMax(MusicPlayerHelper.mediaPlayer.getDuration());
        seekUpdation();
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation(){
        musicSeeker.setProgress(MusicPlayerHelper.mediaPlayer.getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
    }

    @Override
    protected void onDestroy() {
        seekHandler.removeCallbacks(run);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detail_controller:
                MusicPlayerHelper.getInstance().toggleMusicPlayer(detailController);
                if (MusicPlayerHelper.isPaused)
                    seekHandler.removeCallbacks(run);
                else
                    seekUpdation();
                break;

            case R.id.detail_fast_forward:
                detailController.setImageResource(android.R.drawable.ic_media_pause);
                String title = MusicPlayerHelper.getInstance()
                        .playNextSong(SongDetailActivity.this);
                detailSelectedTrack.setText(title);
                break;

            case R.id.detail_reverse:
                detailController.setImageResource(android.R.drawable.ic_media_pause);
                String titleSong = MusicPlayerHelper.getInstance()
                                .playPrevSong(SongDetailActivity.this);
                detailSelectedTrack.setText(titleSong);
                break;
        }
    }
}