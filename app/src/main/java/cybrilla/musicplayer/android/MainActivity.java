package cybrilla.musicplayer.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MediaPlayerService;
import cybrilla.musicplayer.util.MusicPlayerHelper;
import cybrilla.musicplayer.util.SharedPreferenceHandler;

/**
 * Set up view pager and tab icons. Sets up playing song tool bar and saves song
 * position when destroyed or paused.
 */

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolBarTop;
    private TextView tabTitle, selectedTrackTitle, selectedTrackArtist;
    private ImageView selectedAlbumCover, playerControl, menuItem, detailControler;
    private ImageView detailForward, detailReverse, shuffle, repeat;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private static final String TAG = "Main Activity";
    private SeekBar musicSeeker;
    private Handler seekHandler = new Handler();
    private Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolBarTop = (Toolbar) findViewById(R.id.toolbarTop);
        tabTitle = (TextView) findViewById(R.id.tabTitle);
        selectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        selectedAlbumCover = (ImageView) findViewById(R.id.selected_album_cover);
        playerControl = (ImageView) findViewById(R.id.player_control);
        selectedTrackArtist = (TextView) findViewById(R.id.selected_track_artist);
        menuItem = (ImageView) findViewById(R.id.menuItem);
        musicSeeker = (SeekBar) findViewById(R.id.music_seeker);
        detailControler = (ImageView) findViewById(R.id.detail_controller);
        detailForward = (ImageView) findViewById(R.id.detail_fast_forward);
        detailReverse = (ImageView) findViewById(R.id.detail_reverse);
        shuffle = (ImageView) findViewById(R.id.shuffle);
        repeat = (ImageView) findViewById(R.id.repeat);

        if (MusicPlayerHelper.getInstance().getShuffleOn()){
            shuffle.setImageResource(R.drawable.ic_shuffle_selected);
        }

        if (MusicPlayerHelper.getInstance().getRepeatOn()){
            repeat.setImageResource(R.drawable.ic_repeat_selected);
        }

        if (MusicPlayerHelper.getInstance().getMediaPlayer() == null){
            MusicPlayerHelper.getInstance().initializeMediaPlayer();
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        tabTitle.setText("All Songs");
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        toolBarTop.setCollapsible(true);
        //Change actionbar text with tab change
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int pos = tabLayout.getSelectedTabPosition();
                if (pos == 0 && (!"All Songs".equals(tabTitle.getText().toString()))) {
                    tabTitle.setText("All Songs");
                    menuItem.setImageResource(android.R.drawable.ic_menu_search);
                } else if (pos == 1 && (!"Albums".equals(tabTitle.getText().toString()))) {
                    tabTitle.setText("Albums");
                    menuItem.setImageResource(android.R.drawable.ic_menu_search);
                } else if (pos == 2 && (!"Artists".equals(tabTitle.getText().toString()))) {
                    tabTitle.setText("Artists");
                    menuItem.setImageResource(android.R.drawable.ic_menu_search);
                } else if (pos == 3 && (!"Playlist".equals(tabTitle.getText().toString()))) {
                    tabTitle.setText("Playlist");
                    menuItem.setImageResource(R.drawable.ic_add_playlist);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setUpSlidingPanel();
        setUpTabIcon();
        completion();
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

    private void setUpSlidingPanel() {
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.isTouchEnabled();

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
                seekHandler.removeCallbacks(run);
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
                if (MusicPlayerHelper.getInstance().getShuffleOn()){
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
                if (MusicPlayerHelper.getInstance().getRepeatOn()){
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

    private void setAlbumCover(Song song) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Uri uri = song.getUri();
        mmr.setDataSource(this, uri);
        if (mmr.getEmbeddedPicture() != null) {
            rawArt = mmr.getEmbeddedPicture();
            Glide.with(this).load(rawArt)
                    .asBitmap().into(selectedAlbumCover);
        } else {
            Glide.with(this).load(R.drawable.no_image)
                    .asBitmap().into(selectedAlbumCover);
        }
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
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == PanelState.EXPANDED ||
                        slidingUpPanelLayout.getPanelState() == PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    //Save last played song to show when app is opened again
    @Override
    protected void onPause() {
        super.onPause();
        if (MusicPlayerHelper.getInstance().getMediaPlayer() != null) {
            if (MusicPlayerHelper.getInstance().getIsPaused() ||
                    MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
                SharedPreferenceHandler.getInstance().setSongPosition(this,
                        MusicPlayerHelper.getInstance().getSongPosition());
            }
            Intent intent = new Intent(this, MediaPlayerService.class);
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MusicPlayerHelper.getInstance().getMediaPlayer() != null) {
            if (MusicPlayerHelper.getInstance().getIsPaused() ||
                    MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
                SharedPreferenceHandler.getInstance().setSongPosition(this,
                        MusicPlayerHelper.getInstance().getSongPosition());
            }
        }
    }

    /**
     * Set selected track toolbar with playing song and stop notification if running.
     */

    @Override
    protected void onResumeFragments() {
        if (MusicPlayerHelper.getInstance().getMediaPlayer() != null &&
                (MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()
                        || MusicPlayerHelper.getInstance().getIsPaused())) {
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.setAction(Constants.STOP_NOTIFICATION);
            startService(intent);
            Song song = MusicPlayerHelper.allSongsList.get(
                    MusicPlayerHelper.getInstance().getSongPosition());
            selectedTrackTitle.setText(song.getSongTitle());
            selectedTrackArtist.setText(song.getSongArtist());
            Log.e("Main Activity", "Song playing: " + song.getSongTitle());
            if (MusicPlayerHelper.getInstance().getIsPaused()) {
                playerControl.setImageResource(android.R.drawable.ic_media_play);
            } else {
                Log.e("Main Activity", "Getting set to pause");
                playerControl.setImageResource(android.R.drawable.ic_media_pause);
            }
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            Bitmap art;
            BitmapFactory.Options bfo = new BitmapFactory.Options();
            Uri uri = song.getUri();
            mmr.setDataSource(this, uri);
            try {
                rawArt = mmr.getEmbeddedPicture();
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                selectedAlbumCover.setImageBitmap(art);
            } catch (Exception e) {
                selectedAlbumCover.setImageResource(R.drawable.ic_action_ic_default_cover);
            }

        }
        super.onResumeFragments();
    }

    private void setUpTabIcon() {
        TextView tabOne = (TextView) LayoutInflater.from(this)
                .inflate(R.layout.custom_tab, null);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_all_songs, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this)
                .inflate(R.layout.custom_tab, null);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_album, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this)
                .inflate(R.layout.custom_tab, null);
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_brush, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this)
                .inflate(R.layout.custom_tab, null);
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_playlist, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllSongsFragment());
        adapter.addFragment(new AlbumFragment());
        adapter.addFragment(new ArtistFragment());
        adapter.addFragment(new PlaylistFragment());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
