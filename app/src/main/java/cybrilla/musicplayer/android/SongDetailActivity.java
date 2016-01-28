package cybrilla.musicplayer.android;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MediaPlayerService;
import cybrilla.musicplayer.util.MusicPlayerHelper;
import cybrilla.musicplayer.util.SharedPreferenceHandler;

public class SongDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView detailSelectedTrack, detailSelectedArtist;
    private ImageView detailController, detailFastForward, detailReverse, detailSelectedCover;
    private SeekBar musicSeeker;
    private ImageView shuffle, repeat;
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
        detailSelectedCover = (ImageView) findViewById(R.id.detail_selected_cover);
        detailSelectedArtist = (TextView) findViewById(R.id.detail_selected_artist);
        shuffle = (ImageView) findViewById(R.id.shuffle);
        repeat = (ImageView) findViewById(R.id.repeat);

        if (MusicPlayerHelper.getInstance().getMediaPlayer() == null) {
            detailController.setImageResource(android.R.drawable.ic_media_play);
        } else if (!MusicPlayerHelper.getInstance().getIsPaused()) {
            detailController.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            detailController.setImageResource(android.R.drawable.ic_media_play);
        }
        setSongData();
        detailController.setOnClickListener(this);
        detailFastForward.setOnClickListener(this);
        detailReverse.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        repeat.setOnClickListener(this);

        if (MusicPlayerHelper.getInstance().getShuffleOn()){
            shuffle.setImageResource(R.drawable.ic_shuffle_selected);
        }

        if (MusicPlayerHelper.getInstance().getRepeatOn()){
            repeat.setImageResource(R.drawable.ic_repeat_selected);
        }

        if (MusicPlayerHelper.getInstance().getMediaPlayer() == null) {
            MusicPlayerHelper.getInstance().initializeMediaPlayer();
        } else {
            if (MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
                musicSeeker.setMax(MusicPlayerHelper.getInstance().getMediaPlayer().getDuration());
                seekUpdation();
                completion();
            } else if (MusicPlayerHelper.getInstance().getIsPaused() &&
                    MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
                musicSeeker.setMax(MusicPlayerHelper.getInstance().getMediaPlayer().getDuration());
                completion();
                musicSeeker.setProgress(MusicPlayerHelper.getInstance().getMediaPlayer()
                        .getCurrentPosition());
            }
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

    private void setSongData() {
        Song song = MusicPlayerHelper.allSongsList.get(
                MusicPlayerHelper.getInstance().getSongPosition());
        setAlbumCover(song);
        detailSelectedArtist.setText(song.getSongArtist());
        detailSelectedTrack.setText(song.getSongTitle());
    }

    @Override
    protected void onRestart() {
        if (MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
            detailController.setImageResource(android.R.drawable.ic_media_pause);
        }
        super.onRestart();
    }

    private void setAlbumCover(Song song) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Uri uri = song.getUri();
        mmr.setDataSource(this, uri);
        if (mmr.getEmbeddedPicture() != null) {
            rawArt = mmr.getEmbeddedPicture();
            Glide.with(this).load(rawArt)
                    .asBitmap().into(detailSelectedCover);
        } else {
            Glide.with(this).load(R.drawable.no_image)
                    .asBitmap().into(detailSelectedCover);
        }
    }

    private void completion() {
        MusicPlayerHelper.getInstance().getMediaPlayer().
                setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (!MusicPlayerHelper.getInstance().getRepeatOn()) {
                            MusicPlayerHelper.getInstance()
                                    .playNextSong();
                            Song song = MusicPlayerHelper.allSongsList.get
                                    (MusicPlayerHelper.getInstance().getSongPosition());
                            detailSelectedTrack.setText(song.getSongTitle());
                            detailSelectedArtist.setText(song.getSongArtist());
                            setAlbumCover(song);
                        } else {
                            MusicPlayerHelper.getInstance().startMusic(MusicPlayerHelper
                                .getInstance().getSongPosition());
                        }
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
            Intent intent = new Intent(this, MediaPlayerService.class);
            startService(intent);
        }
        if (!MusicPlayerHelper.getInstance().getIsPaused())
            seekHandler.removeCallbacks(run);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.setAction(Constants.STOP_NOTIFICATION);
        startService(intent);
        if (MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
            seekUpdation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MusicPlayerHelper.getInstance().getIsPaused() ||
                MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
            SharedPreferenceHandler.getInstance().setSongPosition(this,
                    MusicPlayerHelper.getInstance().getSongPosition());
        }
        seekHandler.removeCallbacks(run);
    }

    @Override
    public void onClick(View v) {
        Song song;
        switch (v.getId()) {
            case R.id.detail_controller:
                if (!MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
                    MusicPlayerHelper.getInstance().startMusic(
                            MusicPlayerHelper.getInstance().getSongPosition());
                    musicSeeker.setMax(MusicPlayerHelper.getInstance()
                            .getMediaPlayer().getDuration());
                } else {
                    MusicPlayerHelper.getInstance().toggleMusicPlayer
                            (null);
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
                MusicPlayerHelper.getInstance().playNextSong();
                detailController.setImageResource(android.R.drawable.ic_media_pause);
                song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper.getInstance()
                        .getSongPosition());
                detailSelectedTrack.setText(song.getSongTitle());
                detailSelectedArtist.setText(song.getSongArtist());
                setAlbumCover(song);
                musicSeeker.setMax(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getDuration());
                musicSeeker.setProgress(MusicPlayerHelper.getInstance().getMediaPlayer()
                        .getCurrentPosition());
                break;

            case R.id.detail_reverse:
                MusicPlayerHelper.getInstance().playPrevSong();
                detailController.setImageResource(android.R.drawable.ic_media_pause);
                song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper
                        .getInstance().getSongPosition());
                detailSelectedTrack.setText(song.getSongTitle());
                detailSelectedArtist.setText(song.getSongArtist());
                setAlbumCover(song);
                musicSeeker.setMax(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getDuration());
                musicSeeker.setProgress(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getCurrentPosition());
                break;

            case R.id.shuffle:
                if (MusicPlayerHelper.getInstance().getShuffleOn()){
                    shuffle.setImageResource(R.drawable.ic_shuffle_not_slected);
                    MusicPlayerHelper.getInstance().setShuffleOn(false);
                } else {
                    shuffle.setImageResource(R.drawable.ic_shuffle_selected);
                    MusicPlayerHelper.getInstance().setShuffleOn(true);
                }
                break;

            case R.id.repeat:
                if (MusicPlayerHelper.getInstance().getRepeatOn()){
                    repeat.setImageResource(R.drawable.ic_repeat_not_selected);
                    MusicPlayerHelper.getInstance().setRepeatOn(false);
                } else {
                    repeat.setImageResource(R.drawable.ic_repeat_selected);
                    MusicPlayerHelper.getInstance().setRepeatOn(true);
                }
                break;
        }
    }
}
