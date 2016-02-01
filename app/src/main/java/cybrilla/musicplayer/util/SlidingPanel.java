package cybrilla.musicplayer.util;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;

/**
 * The Sliding panel containing details of current playing song, along with a
 * detail view of playing song when expanded.
 */

public class SlidingPanel {
    private static SlidingPanel instance;
    private SeekBar musicSeeker;
    private Handler seekHandler = new Handler();
    private Toolbar playingSongToolBar;
    private TextView selectedTrackTitle, selectedTrackArtist;
    private ImageView selectedAlbumCover, detailControler;
    private ImageView detailForward, detailReverse, shuffle, repeat, playerControl;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private static Activity mActivity;

    public static SlidingPanel getInstance(){
        if (instance == null){
            instance = new SlidingPanel();
        }
        return instance;
    }

    public void initializedSlidingLayout(Activity activity){
        mActivity = activity;
        musicSeeker = (SeekBar) mActivity.findViewById(R.id.music_seeker);
        playingSongToolBar = (Toolbar) mActivity.findViewById(R.id.playing_song_toolbar);
        playerControl = (ImageView) mActivity.findViewById(R.id.player_control);
        detailForward = (ImageView) mActivity.findViewById(R.id.detail_fast_forward);
        detailReverse = (ImageView) mActivity.findViewById(R.id.detail_reverse);
        detailControler = (ImageView) mActivity.findViewById(R.id.detail_controller);
        shuffle = (ImageView) mActivity.findViewById(R.id.shuffle);
        repeat = (ImageView) mActivity.findViewById(R.id.repeat);
        selectedAlbumCover = (ImageView) mActivity.findViewById(R.id.selected_album_cover);
        selectedTrackTitle = (TextView) mActivity.findViewById(R.id.selected_track_title);
        selectedTrackArtist = (TextView) mActivity.findViewById(R.id.selected_track_artist);
        slidingUpPanelLayout = (SlidingUpPanelLayout) mActivity.findViewById(R.id.sliding_layout);

        if (MusicPlayerHelper.getInstance().getShuffleOn()){
            shuffle.setImageResource(R.drawable.ic_shuffle_selected);
        }

        if (MusicPlayerHelper.getInstance().getRepeatOn()){
            repeat.setImageResource(R.drawable.ic_repeat_selected);
        }
        if (MusicPlayerHelper.getInstance().getMusicStartedOnce())
            completion();
    }

    public PanelState getSlidingPanelState(){
        return slidingUpPanelLayout.getPanelState();
    }

    public void collapseSlidingPanel(){
        slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
    }

    public void expandSlidingPanel(){
        slidingUpPanelLayout.setPanelState(PanelState.EXPANDED);
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
                            selectedTrackTitle.setText(song.getSongTitle());
                            selectedTrackArtist.setText(song.getSongArtist());
                            setAlbumCover(song);
                        } else {
                            MusicPlayerHelper.getInstance().startMusic(MusicPlayerHelper
                                    .getInstance().getSongPosition());
                        }
                    }
                });
    }

    public void setUpSlidingPanel() {
        playingSongToolBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayout != null && (getSlidingPanelState()
                        == PanelState.EXPANDED || getSlidingPanelState()
                        == PanelState.ANCHORED)) {
                    collapseSlidingPanel();
                } else {
                    if (slidingUpPanelLayout != null) {
                        expandSlidingPanel();
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
                if (MusicPlayerHelper.getInstance().getIsPaused())
                    playerControl.setImageResource(android.R.drawable.ic_media_play);
                else
                    playerControl.setImageResource(android.R.drawable.ic_media_pause);
                playerControl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
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

    public void setPlayingSongDetails(){
        Song song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper.getInstance()
                .getSongPosition());
        selectedTrackTitle.setText(song.getSongTitle());
        selectedTrackArtist.setText(song.getSongArtist());
        setAlbumCover(song);
        if (MusicPlayerHelper.getInstance().getMusicStartedOnce()){
            if (MusicPlayerHelper.getInstance().getIsPaused())
                playerControl.setImageResource(android.R.drawable.ic_media_play);
            else
                playerControl.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            playerControl.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    public void setSongToLastPlayed(){
        int position = SharedPreferenceHandler.getInstance().getSongPosition(mActivity);
        MusicPlayerHelper.getInstance().setSongPosition(position);
        Song song = MusicPlayerHelper.allSongsList.get(position);
        Log.e("Sliding Panel", "Song title: " + song.getSongTitle());
        selectedTrackTitle.setText(song.getSongTitle());
        selectedTrackArtist.setText(song.getSongArtist());
        if (MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
            if (MusicPlayerHelper.getInstance().getIsPaused())
                playerControl.setImageResource(android.R.drawable.ic_media_play);
            else
                playerControl.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            playerControl.setImageResource(android.R.drawable.ic_media_play);
        }
        setAlbumCover(song);
    }

    public void setPlayerControl(){
        if (MusicPlayerHelper.getInstance().getMusicStartedOnce()){
            if (MusicPlayerHelper.getInstance().getIsPaused())
                playerControl.setImageResource(android.R.drawable.ic_media_play);
            else
                playerControl.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            playerControl.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    /**
     * Updates the music seeker every second to the song's current position.
     */

    private void seekUpdation() {
        musicSeeker.setProgress(MusicPlayerHelper.getInstance().getMediaPlayer()
                .getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
    }

    private void controllerListeners(){
        playerControl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicPlayerHelper.getInstance().getMediaPlayer() == null)
                    MusicPlayerHelper.getInstance().initializeMediaPlayer();
                if (MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
                    MusicPlayerHelper.getInstance().toggleMusicPlayer(playerControl);
                } else {
                    MusicPlayerHelper.getInstance().startMusic(MusicPlayerHelper
                        .getInstance().getSongPosition());
                    playerControl.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

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
                Song song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper.getInstance()
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
                Song song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper
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

    public void removeSeeker(){
        seekHandler.removeCallbacks(run);
    }

    public void setAlbumCover(Song song) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Uri uri = song.getUri();
        mmr.setDataSource(mActivity, uri);
        if (mmr.getEmbeddedPicture() != null) {
            rawArt = mmr.getEmbeddedPicture();
            Glide.with(mActivity).load(rawArt)
                    .asBitmap().into(selectedAlbumCover);
        } else {
            Glide.with(mActivity).load(R.drawable.no_image)
                    .asBitmap().into(selectedAlbumCover);
        }
    }
}
