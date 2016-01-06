package cybrilla.musicplayer.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.adapter.SongAdapter;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.MusicPlayerHelper;

public class AllSongsActivity extends AppCompatActivity{
    private List<Song> allSongsList;
    private RecyclerView songList;
    private SongAdapter mAdapter;
    private static final String TAG = "AllSongsActivity";
    private Toolbar playingSongToolbar;
    private TextView selectedSongTrack;
    private ImageView playerControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs);
        songList = (RecyclerView) findViewById(R.id.songList);
        playingSongToolbar = (Toolbar) findViewById(R.id.playing_song_toolbar);
        selectedSongTrack = (TextView) findViewById(R.id.selected_track_title);
        playerControl = (ImageView) findViewById(R.id.player_control);

        songList.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        songList.setLayoutManager(linearLayout);
        getSongList();
    }

    private void getSongList(){
        allSongsList = new ArrayList<>(MusicPlayerHelper.getInstance().getSongList(this));
        mAdapter = new SongAdapter(allSongsList, this);
        songList.setAdapter(mAdapter);
        setToolBarListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedSongTrack.setText(MusicPlayerHelper.getInstance().getSongTitle());
        if (MusicPlayerHelper.mediaPlayer != null) {
            if (MusicPlayerHelper.mediaPlayer.isPlaying())
                playerControl.setImageResource(android.R.drawable.ic_media_pause);
            else
                playerControl.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void setToolBarListener(){
        playingSongToolbar.setOnClickListener(new OnClickListener() {
            @TargetApi(VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllSongsActivity.this, SongDetailActivity.class);
                intent.putExtra(SongDetailActivity.TITLE_NAME,
                        selectedSongTrack.getText().toString());
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation
                                (AllSongsActivity.this, playingSongToolbar
                                        , playingSongToolbar.getTransitionName());
                ActivityCompat.startActivity(AllSongsActivity.this, intent, options.toBundle());
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "Destroy called");
        MusicPlayerHelper.getInstance().releaseMediaPlayer();
        super.onDestroy();
    }
}
