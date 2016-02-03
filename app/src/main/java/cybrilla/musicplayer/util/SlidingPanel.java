package cybrilla.musicplayer.util;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class SlidingPanel implements View.OnClickListener{
    private static SlidingPanel instance;
    private SeekBar musicSeeker;
    private Handler seekHandler = new Handler();
    private Toolbar playingSongToolBar;
    private TextView selectedTrackTitle, selectedTrackArtist;
    private ImageView selectedAlbumCover, detailControler, playingSongDetail;
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
        playingSongDetail = (ImageView) mActivity.findViewById(R.id.playing_song_detail);
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
        Log.e("Sliding Panel", "Sliding down, no!");
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
        playingSongToolBar.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        repeat.setOnClickListener(this);

        slidingUpPanelLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelExpanded(View panel) {
                detailControler.setBackground(null);
                detailControler.setImageResource(0);
                playerControl.setVisibility(View.GONE);
                if (MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
                    musicSeeker.setMax(MusicPlayerHelper.getInstance().getMediaPlayer()
                            .getDuration());
                    if (MusicPlayerHelper.getInstance().getIsPaused()) {
                        detailControler.setImageResource(R.drawable.ic_play_0);
                    } else {
                        detailControler.setImageResource(R.drawable.ic_pause_0);
                        seekUpdation();
                    }
                } else {
                    Log.e("Sliding Panel", "Max getting set to one");
                    musicSeeker.setMax(Constants.DEFAULT_MAX);
                    detailControler.setImageResource(R.drawable.ic_play_0);
                }
            }

            @Override
            public void onPanelCollapsed(View panel) {
                if (MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
                    if (MusicPlayerHelper.getInstance().getIsPaused()) {
                        playerControl.setBackground(null);
                        playerControl.setImageResource(0);
                        playerControl.setImageResource(R.drawable.ic_play_0);
                    } else {
                        playerControl.setBackground(null);
                        playerControl.setImageResource(0);
                        playerControl.setImageResource(R.drawable.ic_pause_0);
                    }
                } else {
                    playerControl.setBackground(null);
                    playerControl.setImageResource(0);
                    playerControl.setImageResource(R.drawable.ic_play_0);
                }
                playerControl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
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
        playerControl.setBackground(null);
        playerControl.setImageResource(0);
        if (MusicPlayerHelper.getInstance().getMusicStartedOnce()){
            if (MusicPlayerHelper.getInstance().getIsPaused()) {
                playerControl.setImageResource(R.drawable.ic_play_0);
            }
            else {
                playerControl.setImageResource(R.drawable.ic_pause_0);
            }
        } else {
            playerControl.setImageResource(R.drawable.ic_play_0);
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
                playerControl.setImageResource(R.drawable.ic_play_0);
            else
                playerControl.setImageResource(R.drawable.ic_pause_0);
        } else {
            playerControl.setImageResource(R.drawable.ic_play_0);
        }
        setAlbumCover(song);
    }

    public void setPlayerControl(){
        if (MusicPlayerHelper.getInstance().getMusicStartedOnce()){
            if (MusicPlayerHelper.getInstance().getIsPaused())
                playerControl.setImageResource(R.drawable.ic_play_0);
            else
                playerControl.setImageResource(R.drawable.ic_pause_0);
        } else {
            playerControl.setImageResource(R.drawable.ic_play_0);
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
        playerControl.setOnClickListener(this);
        detailControler.setOnClickListener(this);
        detailForward.setOnClickListener(this);
        detailReverse.setOnClickListener(this);

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
            Glide.with(mActivity).load(rawArt)
                    .asBitmap().into(playingSongDetail);
        } else {
            Glide.with(mActivity).load(R.drawable.default_image)
                    .asBitmap().into(selectedAlbumCover);
            Glide.with(mActivity).load(R.drawable.default_image)
                    .asBitmap().into(playingSongDetail);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playing_song_toolbar:
                if (slidingUpPanelLayout != null && (getSlidingPanelState()
                        == PanelState.EXPANDED || getSlidingPanelState()
                        == PanelState.ANCHORED)) {
                    collapseSlidingPanel();
                } else {
                    if (slidingUpPanelLayout != null) {
                        expandSlidingPanel();
                    }
                }
                break;

            case R.id.shuffle:
                if (MusicPlayerHelper.getInstance().getShuffleOn()) {
                    shuffle.setImageResource(R.drawable.ic_shuffle_not_slected);
                    MusicPlayerHelper.getInstance().setShuffleOn(false);
                } else {
                    shuffle.setImageResource(R.drawable.ic_shuffle_selected);
                    MusicPlayerHelper.getInstance().setShuffleOn(true);
                }
                break;

            case R.id.repeat:
                if (MusicPlayerHelper.getInstance().getRepeatOn()) {
                    repeat.setImageResource(R.drawable.ic_repeat_not_selected);
                    MusicPlayerHelper.getInstance().setRepeatOn(false);
                } else {
                    repeat.setImageResource(R.drawable.ic_repeat_selected);
                    MusicPlayerHelper.getInstance().setRepeatOn(true);
                }
                break;

            case R.id.player_control:
                if (MusicPlayerHelper.getInstance().getMediaPlayer() == null)
                    MusicPlayerHelper.getInstance().initializeMediaPlayer();
                if (MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
                    MusicPlayerHelper.getInstance().toggleMusicPlayer(playerControl, mActivity);
                } else {
                    MusicPlayerHelper.getInstance().startMusic(MusicPlayerHelper
                            .getInstance().getSongPosition());
                    playerControl.setImageResource(R.drawable.ic_pause_0);
                }
                break;

            case R.id.detail_controller:
                if (MusicPlayerHelper.getInstance().getMediaPlayer() != null) {
                    if (MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
                        if (MusicPlayerHelper.getInstance().getIsPaused()) {
                            detailControler.setBackground(null);
                            detailControler.setImageResource(0);
                            detailControler.setBackground(mActivity.getResources().getDrawable
                                    (R.drawable.play_to_pause));
                            ((AnimationDrawable) detailControler.getBackground()).start();
                            playerControl.setImageResource(R.drawable.ic_pause_0);
                            MusicPlayerHelper.getInstance().toggleMusicPlayer
                                    (playerControl, mActivity);
                            seekUpdation();
                        } else {
                            detailControler.setBackground(null);
                            detailControler.setImageResource(0);
                            detailControler.setBackground(mActivity.getResources().getDrawable
                                    (R.drawable.pause_to_play));
                            ((AnimationDrawable) detailControler.getBackground()).start();
                            playerControl.setImageResource(R.drawable.ic_play_0);
                            MusicPlayerHelper.getInstance().toggleMusicPlayer(null, null);
                        }
                    } else {
                        MusicPlayerHelper.getInstance().startMusic(MusicPlayerHelper
                                .getInstance().getSongPosition());
                        detailControler.setBackground(null);
                        detailControler.setImageResource(0);
                        detailControler.setBackground(mActivity.getResources().getDrawable
                                (R.drawable.play_to_pause));
                        ((AnimationDrawable) detailControler.getBackground()).start();
                        playerControl.setImageResource(R.drawable.ic_pause_0);
                        musicSeeker.setMax(MusicPlayerHelper.getInstance().getMediaPlayer()
                                .getDuration());
                        seekUpdation();
                    }
                } else {
                    MusicPlayerHelper.getInstance().initializeMediaPlayer();
                    MusicPlayerHelper.getInstance().startMusic(MusicPlayerHelper
                            .getInstance().getSongPosition());
                    detailControler.setBackground(null);
                    detailControler.setImageResource(0);
                    detailControler.setBackground(mActivity.getResources().getDrawable
                            (R.drawable.play_to_pause));
                    ((AnimationDrawable) detailControler.getBackground()).start();
                    seekUpdation();
                }
                break;

            case R.id.detail_fast_forward:
                MusicPlayerHelper.getInstance().playNextSong();
                detailControler.setImageResource(R.drawable.ic_pause_0);
                playerControl.setImageResource(R.drawable.ic_pause_0);
                Song song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper.getInstance()
                        .getSongPosition());
                selectedTrackTitle.setText(song.getSongTitle());
                selectedTrackArtist.setText(song.getSongArtist());
                setAlbumCover(song);
                musicSeeker.setMax(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getDuration());
                musicSeeker.setProgress(MusicPlayerHelper.getInstance().getMediaPlayer()
                        .getCurrentPosition());
                break;

            case R.id.detail_reverse:
                MusicPlayerHelper.getInstance().playPrevSong();
                detailControler.setImageResource(R.drawable.ic_pause_0);
                Song songOne = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper
                        .getInstance().getSongPosition());
                selectedTrackTitle.setText(songOne.getSongTitle());
                selectedTrackArtist.setText(songOne.getSongArtist());
                setAlbumCover(songOne);
                musicSeeker.setMax(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getDuration());
                musicSeeker.setProgress(MusicPlayerHelper.getInstance()
                        .getMediaPlayer().getCurrentPosition());
                break;
        }
    }
}
