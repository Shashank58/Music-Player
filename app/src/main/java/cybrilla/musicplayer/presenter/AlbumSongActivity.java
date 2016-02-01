package cybrilla.musicplayer.presenter;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.view.AlbumSongsAdapter;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.MusicPlayerHelper;
import cybrilla.musicplayer.util.SlidingPanel;

/**
 * Activity which contains songs of an album. Sends data to adapter to set it.
 */

public class AlbumSongActivity extends AppCompatActivity{
    private RecyclerView albumSongList;
    private ImageView albumSongImage;
    private AlbumSongsAdapter mAdapter;
    private static final String TAG = "Album Song Activity";

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        //Makes status bar transparent and allows image to appear behind it.
        if (VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        albumSongList = (RecyclerView) findViewById(R.id.album_song_list);
        albumSongImage = (ImageView) findViewById(R.id.album_song_image);

        albumSongList.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        albumSongList.setLayoutManager(linearLayout);
        setUpAlbum();
        SlidingPanel.getInstance().initializedSlidingLayout(this);
        SlidingPanel.getInstance().setUpSlidingPanel();
        SlidingPanel.getInstance().setPlayingSongDetails();
    }

    private void setUpAlbum(){
        int pos = getIntent().getIntExtra(Constants.SONG_POSITION, -1);
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
}
