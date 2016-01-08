package cybrilla.musicplayer.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private ImageView playerControl, selectedAlbumCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs);
        songList = (RecyclerView) findViewById(R.id.songList);
        playingSongToolbar = (Toolbar) findViewById(R.id.playing_song_toolbar);
        selectedSongTrack = (TextView) findViewById(R.id.selected_track_title);
        playerControl = (ImageView) findViewById(R.id.player_control);
        selectedAlbumCover = (ImageView) findViewById(R.id.selected_album_cover);

        getSupportActionBar().setHomeButtonEnabled(true);

        songList.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        songList.setLayoutManager(linearLayout);
        getSongList();
    }

    private void getSongList(){
        allSongsList = new ArrayList<>(MusicPlayerHelper.getInstance().getSongList(this));
        mAdapter = new SongAdapter(allSongsList, this);
        songList.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        selectedSongTrack.setText(MusicPlayerHelper.getInstance().getSongTitle());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        mmr.setDataSource(this, MusicPlayerHelper.getInstance().getSongUri());
        try {
            rawArt = mmr.getEmbeddedPicture();
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            selectedAlbumCover.setImageBitmap(art);
        } catch (Exception e) {
            selectedAlbumCover.setImageResource(R.drawable.ic_action_ic_default_cover);
        }
        if (MusicPlayerHelper.mediaPlayer != null) {
            if (MusicPlayerHelper.mediaPlayer.isPlaying())
                playerControl.setImageResource(R.drawable.ic_pause);
            else
                playerControl.setImageResource(R.drawable.ic_play);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "Destroy called");
        MusicPlayerHelper.getInstance().releaseMediaPlayer();
        super.onDestroy();
    }
}
