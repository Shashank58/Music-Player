package cybrilla.musicplayer.allsongs;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
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
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "printing?");
                ActivityCompat.requestPermissions(this,
                        new String[]{permission.READ_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                getAllViews();
            }
        } else {
            getAllViews();
        }
    }

    private void getAllViews(){
        setContentView(R.layout.activity_all_songs);
        songList = (RecyclerView) findViewById(R.id.songList);
        playingSongToolbar = (Toolbar) findViewById(R.id.playing_song_toolbar);
        selectedSongTrack = (TextView) findViewById(R.id.selected_track_title);
        playerControl = (ImageView) findViewById(R.id.player_control);
        selectedAlbumCover = (ImageView) findViewById(R.id.selected_album_cover);
        songList.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        songList.setLayoutManager(linearLayout);
        getSongList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllViews();
                }
            }
        }
    }


    private void getSongList(){
        MusicPlayerHelper.getInstance().getSongList(this);
        mAdapter = new SongAdapter(this);
        songList.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        if ( ContextCompat.checkSelfPermission(this,
                permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            int pos = MusicPlayerHelper.songPosition;
            selectedSongTrack.setText(MusicPlayerHelper.allSongsList.get(pos).getSongTitle());
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            Bitmap art;
            BitmapFactory.Options bfo = new BitmapFactory.Options();
            mmr.setDataSource(this, MusicPlayerHelper.allSongsList.get(pos).getUri());
            try {
                rawArt = mmr.getEmbeddedPicture();
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                selectedAlbumCover.setImageBitmap(art);
            } catch (Exception e) {
                selectedAlbumCover.setImageResource(R.drawable.ic_action_ic_default_cover);
            }
            if (MusicPlayerHelper.mediaPlayer != null) {
                if (MusicPlayerHelper.mediaPlayer.isPlaying()) {
                    Log.e(TAG, "Getting called by any chance?");
                    playerControl.setImageResource(R.drawable.ic_pause);
                }
                else {
                    playerControl.setImageResource(R.drawable.ic_play);
                    Log.e(TAG, "Getting called by any chance?");
                }
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "Destroy called");
        super.onDestroy();
    }
}
