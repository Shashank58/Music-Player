package cybrilla.musicplayer.android;

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
import cybrilla.musicplayer.adapters.AlbumSongsAdapter;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Created by shashankm on 22/01/16.
 */

public class AlbumSongActivity extends AppCompatActivity{
    private RecyclerView albumSongList;
    private ImageView albumSongImage;
    private AlbumSongsAdapter mAdapter;

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

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
    }

    private void setUpAlbum(){
        int pos = getIntent().getIntExtra("SongPosition", -1);
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
