package cybrilla.musicplayer.presenter;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private AllSongsFragment allSongsFragment;

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

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        tabTitle.setText("All Songs");
        if (checkPermissions()) {
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            setViewPagerListener();
            setUpTabIcon();
        }
        toolBarTop.setCollapsible(true);
    }

    private boolean checkPermissions() {
        boolean granted;
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission.READ_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                granted = false;
            } else {
                granted = true;
            }
        } else {
            granted = true;
        }
        return granted;
    }

    /**
     * Changing tab title and menu icon with changing tabs.
     */

    private void setViewPagerListener(){
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
                    menuItem.setImageResource(android.R.drawable.btn_star);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //Check if sliding panel is expanded
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
            if (MusicPlayerHelper.getInstance().getShdStartNotification()) {
                Intent intent = new Intent(this, MediaPlayerService.class);
                startService(intent);
            }
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
     * Useful when user comes back to Main Activity after starting a new song in either
     * Artist Songs or Album Songs.
     */

    @Override
    protected void onResumeFragments() {
        SlidingPanel.getInstance().initializedSlidingLayout(this);
        if (MusicPlayerHelper.getInstance().getMediaPlayer() != null &&
                MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.setAction(Constants.STOP_NOTIFICATION);
            startService(intent);
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
        allSongsFragment = new AllSongsFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(allSongsFragment);
        adapter.addFragment(new AlbumFragment());
        adapter.addFragment(new ArtistFragment());
        adapter.addFragment(new PlaylistFragment());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupViewPager(viewPager);
                    tabLayout.setupWithViewPager(viewPager);
                    setViewPagerListener();
                    setUpTabIcon();
                }
            }
        }
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
