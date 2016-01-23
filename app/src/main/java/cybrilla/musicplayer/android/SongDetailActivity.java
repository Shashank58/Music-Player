package cybrilla.musicplayer.android;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MediaPlayerService;
import cybrilla.musicplayer.util.MusicPlayerHelper;
import cybrilla.musicplayer.util.SharedPreferenceHandler;

public class SongDetailActivity extends AppCompatActivity implements View.OnClickListener {
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
        if (MusicPlayerHelper.getInstance().getMediaPlayer() == null){
            detailController.setImageResource(android.R.drawable.ic_media_play);
        } else if (!MusicPlayerHelper.getInstance().getIsPaused()) {
            Log.e("Song Detail Activity", "Pause button being set");
            detailController.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            detailController.setImageResource(android.R.drawable.ic_media_play);
        }
        detailSelectedTrack.setText(getIntent().getStringExtra(Constants.TITLE_NAME));
        detailController.setOnClickListener(this);
        detailFastForward.setOnClickListener(this);
        detailReverse.setOnClickListener(this);
        if (MusicPlayerHelper.getInstance().getMediaPlayer() == null) {
            MusicPlayerHelper.getInstance().initializeMediaPlayer();
            musicSeeker.setMax(0);
        }else {
            musicSeeker.setMax(MusicPlayerHelper.getInstance().getMediaPlayer().getDuration());
            seekUpdation();
            completion();
        }
        musicSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MusicPlayerHelper.getInstance().getMediaPlayer().seekTo(progress);
                    musicSeeker.setProgress(MusicPlayerHelper.getInstance()
                            .getMediaPlayer().getCurrentPosition());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onRestart() {
        if (MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
            Log.e("Song Detail Activity", "Pause button being set");
            detailController.setImageResource(android.R.drawable.ic_media_pause);
        }
        super.onRestart();
    }

    private void completion() {
        MusicPlayerHelper.getInstance().getMediaPlayer().
                setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        MusicPlayerHelper.getInstance()
                                .playNextSong();
                        detailSelectedTrack.setText(MusicPlayerHelper.allSongsList.
                                get(MusicPlayerHelper.getInstance().getSongPosition())
                                .getSongTitle());
                    }
                });
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation() {
        musicSeeker.setProgress(MusicPlayerHelper.getInstance().getMediaPlayer()
                .getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MusicPlayerHelper.getInstance().getIsPaused() ||
                MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
            SharedPreferenceHandler.getInstance().setSongPosition(this,
                    MusicPlayerHelper.getInstance().getSongPosition());
            seekHandler.removeCallbacks(run);
            Intent intent = new Intent(this, MediaPlayerService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.setAction(Constants.STOP_NOTIFICATION);
        startService(intent);
        seekUpdation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MusicPlayerHelper.getInstance().getIsPaused() ||
                MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
            SharedPreferenceHandler.getInstance().setSongPosition(this,
                    MusicPlayerHelper.getInstance().getSongPosition());
            seekHandler.removeCallbacks(run);
        }
    }

    @Override
    public void onClick(View v) {
        Song song;
        switch (v.getId()) {
            case R.id.detail_controller:
                if (musicSeeker.getMax() == 0){
                    MusicPlayerHelper.getInstance().startMusic(
                        MusicPlayerHelper.getInstance().getSongPosition());
                    musicSeeker.setMax(MusicPlayerHelper.getInstance()
                                .getMediaPlayer().getDuration());
                } else {
                    MusicPlayerHelper.getInstance().toggleMusicPlayer(null);
                }
                if (MusicPlayerHelper.getInstance().getIsPaused()) {
                    seekHandler.removeCallbacks(run);
                    detailController.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    seekUpdation();
                    detailController.setImageResource(android.R.drawable.ic_media_pause);
                }
                break;

            case R.id.detail_fast_forward:
                detailController.setImageResource(android.R.drawable.ic_media_pause);
                song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper.getInstance()
                        .getSongPosition() + 1);
                detailSelectedTrack.setText(song.getSongTitle());
                MusicPlayerHelper.getInstance().playNextSong();
                musicSeeker.setMax(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getDuration());
                musicSeeker.setProgress(MusicPlayerHelper.getInstance().getMediaPlayer()
                        .getCurrentPosition());
                break;

            case R.id.detail_reverse:
                detailController.setImageResource(android.R.drawable.ic_media_pause);
                song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper
                        .getInstance().getSongPosition() - 1);
                detailSelectedTrack.setText(song.getSongTitle());
                MusicPlayerHelper.getInstance().playPrevSong();
                musicSeeker.setMax(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getDuration());
                musicSeeker.setProgress(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getCurrentPosition());
                break;
        }
    }
}
