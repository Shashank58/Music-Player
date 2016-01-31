package cybrilla.musicplayer.android;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.bumptech.glide.Glide;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.adapters.AlbumSongsAdapter;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Created by shashankm on 22/01/16.
 */

public class AlbumSongActivity extends AppCompatActivity{
    private RecyclerView albumSongList;
    private ImageView albumSongImage;
    private Toolbar playingSongToolBar;
    private AlbumSongsAdapter mAdapter;
    private static final String TAG = "Album Song Activity";
    private SeekBar musicSeeker;
    private Handler seekHandler = new Handler();
    private ImageView detailForward, detailReverse, shuffle, repeat, playerControl, detailControler;
    private SlidingUpPanelLayout slidingUpPanelLayout;

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        if (VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        albumSongList = (RecyclerView) findViewById(R.id.album_song_list);
        albumSongImage = (ImageView) findViewById(R.id.album_song_image);
        musicSeeker = (SeekBar) findViewById(R.id.music_seeker);
        playingSongToolBar = (Toolbar) findViewById(R.id.playing_song_toolbar);
        playerControl = (ImageView) findViewById(R.id.player_control);
        detailForward = (ImageView) findViewById(R.id.detail_fast_forward);
        detailReverse = (ImageView) findViewById(R.id.detail_reverse);
        detailControler = (ImageView) findViewById(R.id.detail_controller);
        shuffle = (ImageView) findViewById(R.id.shuffle);
        repeat = (ImageView) findViewById(R.id.repeat);

        albumSongList.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        albumSongList.setLayoutManager(linearLayout);
        setUpAlbum();
    }

    private void setUpAlbum(){
        int pos = getIntent().getIntExtra("SongPosition", -1);
        Song song = MusicPlayerHelper.allSongsList.get(pos);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Uri uri = song.getUri();
        mmr.setDataSource(this, uri);
        if (mmr.getEmbeddedPicture() != null) {
            rawArt = mmr.getEmbeddedPicture();
            Glide.with(this).load(rawArt)
                    .asBitmap().into(albumSongImage);
        } else {
            Glide.with(this).load(R.drawable.no_image)
                    .asBitmap().into(albumSongImage);
        }
        mAdapter = new AlbumSongsAdapter(song, this);
        albumSongList.setAdapter(mAdapter);
    }

    private void setUpSlidingPanel() {
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        playingSongToolBar = (Toolbar) findViewById(R.id.playing_song_toolbar);

        playingSongToolBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayout != null && (slidingUpPanelLayout.getPanelState()
                        == PanelState.EXPANDED || slidingUpPanelLayout.getPanelState()
                        == PanelState.ANCHORED)) {
                    slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
                } else {
                    if (slidingUpPanelLayout != null) {
                        Log.e(TAG, "Expand damn it");
                        slidingUpPanelLayout.setPanelState(PanelState.EXPANDED);
                    }
                }
            }
        });

        slidingUpPanelLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelExpanded(View panel) {
                playerControl.setVisibility(View.GONE);
                if (MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
                    musicSeeker.setMax(MusicPlayerHelper.getInstance().getMediaPlayer()
                            .getDuration());
                }
                if (MusicPlayerHelper.getInstance().getIsPaused()) {
                    detailControler.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    detailControler.setImageResource(android.R.drawable.ic_media_pause);
                    musicSeeker.setMax(MusicPlayerHelper.getInstance().getMediaPlayer()
                            .getDuration());
                    seekUpdation();
                }
            }

            @Override
            public void onPanelCollapsed(View panel) {
                playerControl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

        shuffle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicPlayerHelper.getInstance().getShuffleOn()) {
                    shuffle.setImageResource(R.drawable.ic_shuffle_not_slected);
                    MusicPlayerHelper.getInstance().setShuffleOn(false);
                } else {
                    shuffle.setImageResource(R.drawable.ic_shuffle_selected);
                    MusicPlayerHelper.getInstance().setShuffleOn(true);
                }
            }
        });

        repeat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicPlayerHelper.getInstance().getRepeatOn()) {
                    repeat.setImageResource(R.drawable.ic_repeat_not_selected);
                    MusicPlayerHelper.getInstance().setRepeatOn(false);
                } else {
                    repeat.setImageResource(R.drawable.ic_repeat_selected);
                    MusicPlayerHelper.getInstance().setRepeatOn(true);
                }
            }
        });

        controllerListeners();
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

    private void controllerListeners(){
        detailControler.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicPlayerHelper.getInstance().getMediaPlayer() != null) {
                    if (MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
                        if (MusicPlayerHelper.getInstance().getIsPaused()) {
                            detailControler.setImageResource(android.R.drawable.ic_media_pause);
                            playerControl.setImageResource(android.R.drawable.ic_media_pause);
                            MusicPlayerHelper.getInstance().toggleMusicPlayer(null);
                            seekUpdation();
                        } else {
                            detailControler.setImageResource(android.R.drawable.ic_media_play);
                            playerControl.setImageResource(android.R.drawable.ic_media_play);
                            MusicPlayerHelper.getInstance().toggleMusicPlayer(null);
                        }
                    } else {
                        MusicPlayerHelper.getInstance().startMusic(MusicPlayerHelper
                                .getInstance().getSongPosition());
                        detailControler.setImageResource(android.R.drawable.ic_media_pause);
                        playerControl.setImageResource(android.R.drawable.ic_media_pause);
                        musicSeeker.setMax(MusicPlayerHelper.getInstance().getMediaPlayer()
                                .getDuration());
                        seekUpdation();
                    }
                } else {
                    MusicPlayerHelper.getInstance().initializeMediaPlayer();
                    MusicPlayerHelper.getInstance().startMusic(MusicPlayerHelper
                            .getInstance().getSongPosition());
                    detailControler.setImageResource(android.R.drawable.ic_media_pause);
                    seekUpdation();
                }
            }
        });

        detailForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerHelper.getInstance().playNextSong();
                detailControler.setImageResource(android.R.drawable.ic_media_pause);
                playerControl.setImageResource(android.R.drawable.ic_media_pause);
                song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper.getInstance()
                        .getSongPosition());
                selectedTrackTitle.setText(song.getSongTitle());
                selectedTrackArtist.setText(song.getSongArtist());
                setAlbumCover(song);
                musicSeeker.setMax(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getDuration());
                musicSeeker.setProgress(MusicPlayerHelper.getInstance().getMediaPlayer()
                        .getCurrentPosition());
            }
        });

        detailReverse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerHelper.getInstance().playPrevSong();
                detailControler.setImageResource(android.R.drawable.ic_media_pause);
                song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper
                        .getInstance().getSongPosition());
                selectedTrackTitle.setText(song.getSongTitle());
                selectedTrackArtist.setText(song.getSongArtist());
                setAlbumCover(song);
                musicSeeker.setMax(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getDuration());
                musicSeeker.setProgress(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getCurrentPosition());
            }
        });

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
}
