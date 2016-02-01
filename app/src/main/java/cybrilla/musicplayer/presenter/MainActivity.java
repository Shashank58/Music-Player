package cybrilla.musicplayer.presenter;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MediaPlayerService;
import cybrilla.musicplayer.util.MusicPlayerHelper;
import cybrilla.musicplayer.util.SharedPreferenceHandler;
import cybrilla.musicplayer.util.SlidingPanel;

/**
 * Set up view pager and tab icons. Sets up playing song tool bar and saves song
 * position when destroyed or paused.
 */

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolBarTop;
    private TextView tabTitle;
    private ImageView menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolBarTop = (Toolbar) findViewById(R.id.toolbarTop);
        tabTitle = (TextView) findViewById(R.id.tabTitle);
        menuItem = (ImageView) findViewById(R.id.menuItem);

        if (MusicPlayerHelper.getInstance().getMediaPlayer() == null){
            MusicPlayerHelper.getInstance().initializeMediaPlayer();
        }
        SlidingPanel.getInstance().initializedSlidingLayout(this);
        SlidingPanel.getInstance().setUpSlidingPanel();
        Log.e("Main Activity", "On Create getting called");

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
        setUpTabIcon();
    }

    @Override
    public void onBackPressed() {
        PanelState panelState = SlidingPanel.getInstance().getSlidingPanelState();
        if (panelState == PanelState.EXPANDED || panelState == PanelState.ANCHORED) {
            SlidingPanel.getInstance().collapseSlidingPanel();
        } else {
            super.onBackPressed();
        }
    }

    //Save last played song to show when app is opened again
    @Override
    protected void onPause() {
        super.onPause();
        if (MusicPlayerHelper.getInstance().getMediaPlayer() != null) {
            if (MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
                SharedPreferenceHandler.getInstance().setSongPosition(this,
                        MusicPlayerHelper.getInstance().getSongPosition());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (MusicPlayerHelper.getInstance().getMusicStartedOnce()){
            SharedPreferenceHandler.getInstance().setSongPosition(this,
                    MusicPlayerHelper.getInstance().getSongPosition());
            Intent intent = new Intent(this, MediaPlayerService.class);
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MusicPlayerHelper.getInstance().getMediaPlayer() != null) {
            SlidingPanel.getInstance().removeSeeker();
        }
    }

    /**
     * Set selected track toolbar with playing song and stop notification if running.
     */

    @Override
    protected void onResumeFragments() {
        if (MusicPlayerHelper.getInstance().getMediaPlayer() != null &&
                MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
            Log.e("Main Activity", "Resume Fragment getting called");
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.setAction(Constants.STOP_NOTIFICATION);
            startService(intent);
            SlidingPanel.getInstance().initializedSlidingLayout(this);
            SlidingPanel.getInstance().setPlayingSongDetails();
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
