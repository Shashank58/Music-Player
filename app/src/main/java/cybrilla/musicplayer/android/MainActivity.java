package cybrilla.musicplayer.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.album.AlbumFragment;
import cybrilla.musicplayer.allsongs.AllSongsFragment;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MediaPlayerService;
import cybrilla.musicplayer.util.MusicPlayerHelper;
import cybrilla.musicplayer.util.SharedPreferenceHandler;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolBarTop, playingSongToolbar;
    private TextView tabTitle, selectedTrackTitle;
    private ImageView selectedAlbumCover, playerControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolBarTop = (Toolbar) findViewById(R.id.toolbarTop);
        tabTitle = (TextView) findViewById(R.id.tabTitle);
        playingSongToolbar = (Toolbar) findViewById(R.id.playing_song_toolbar);
        selectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        selectedAlbumCover = (ImageView) findViewById(R.id.selected_album_cover);
        playerControl = (ImageView) findViewById(R.id.player_control);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        tabTitle.setText("All Songs");
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        toolBarTop.setCollapsible(true);

        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int pos = tabLayout.getSelectedTabPosition();
                if (pos == 0 && (!"All Songs".equals(tabTitle.getText().toString()))) {
                    tabTitle.setText("All Songs");
                } else if (pos == 1 && (!"Albums".equals(tabTitle.getText().toString()))) {
                    tabTitle.setText("Albums");
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setUpTabIcon();
    }

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

    @Override
    protected void onResumeFragments() {
        if (MusicPlayerHelper.getInstance().getMediaPlayer() != null &&
                (MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()
                    || MusicPlayerHelper.getInstance().getIsPaused())) {
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.setAction(Constants.STOP_NOTIFICATION);
            startService(intent);
            if (playingSongToolbar.getVisibility() == View.GONE) {
                playingSongToolbar.setVisibility(View.VISIBLE);
            }
            Song song = MusicPlayerHelper.allSongsList.get(
                    MusicPlayerHelper.getInstance().getSongPosition());
            selectedTrackTitle.setText(song.getSongTitle());
            Log.e("Main Activity", "Song state: "+MusicPlayerHelper.getInstance().getIsPaused());
            if (MusicPlayerHelper.getInstance().getIsPaused()) {
                playerControl.setImageResource(R.drawable.ic_play);
            } else {
                Log.e("Main Activity", "Getting set to pause");
                playerControl.setImageResource(R.drawable.ic_pause);
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
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllSongsFragment());
        adapter.addFragment(new AlbumFragment());
        //adapter.addFragment(threeFragment, "Artists");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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
