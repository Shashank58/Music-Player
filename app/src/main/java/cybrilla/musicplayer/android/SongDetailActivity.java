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

        if (MusicPlayerHelper.mediaPlayer.isPlaying()) {
            detailController.setImageResource(android.R.drawable.ic_media_pause);
        }
        detailSelectedTrack.setText(getIntent().getStringExtra(Constants.TITLE_NAME));
        detailController.setOnClickListener(this);
        detailFastForward.setOnClickListener(this);
        detailReverse.setOnClickListener(this);
        musicSeeker.setMax(MusicPlayerHelper.mediaPlayer.getDuration());
        musicSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    MusicPlayerHelper.mediaPlayer.seekTo(progress);
                    musicSeeker.setProgress(MusicPlayerHelper.mediaPlayer.getCurrentPosition());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekUpdation();
        completion();
    }

    private void completion(){
        MusicPlayerHelper.mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                MusicPlayerHelper.getInstance()
                        .playNextSong();
                detailSelectedTrack.setText(MusicPlayerHelper.allSongsList.
                                get(MusicPlayerHelper.songPosition).getSongTitle());
            }
        });
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
    protected void onPause() {
        seekHandler.removeCallbacks(run);
        super.onPause();
    }

    @Override
    protected void onResume() {
        seekHandler.postDelayed(run, 1000);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        seekHandler.removeCallbacks(run);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Song song;
        switch (v.getId()){
            case R.id.detail_controller:
                MusicPlayerHelper.getInstance().toggleMusicPlayer(detailController);
                if (MusicPlayerHelper.isPaused) {
                    seekHandler.removeCallbacks(run);
                    detailController.setImageResource(android.R.drawable.ic_media_play);
                }
                else {
                    seekUpdation();
                    detailController.setImageResource(android.R.drawable.ic_media_pause);
                }
                break;

            case R.id.detail_fast_forward:
                detailController.setImageResource(android.R.drawable.ic_media_pause);
                Log.e("Song detail activity", "Position: " + MusicPlayerHelper.songPosition);
                song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper.songPosition+1);
                Log.e("Song detail activity", "Position: " + MusicPlayerHelper.songPosition);
                detailSelectedTrack.setText(song.getSongTitle());
                Intent nextService = new Intent(this, MediaPlayerService.class);
                nextService.setAction(Constants.NEXT_ACTION);
                startService(nextService);
                break;

            case R.id.detail_reverse:
                detailController.setImageResource(android.R.drawable.ic_media_pause);
                song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper.songPosition-1);
                detailSelectedTrack.setText(song.getSongTitle());
                //musicSeeker.setProgress(MusicPlayerHelper.mediaPlayer.getCurrentPosition());
                Intent prevService = new Intent(this, MediaPlayerService.class);
                prevService.setAction(Constants.PREV_ACTION);
                startService(prevService);
                break;
        }
    }
}
