package cybrilla.musicplayer.album;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;

import cybrilla.musicplayer.R;
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

        getSupportActionBar().hide();

        if (VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
        Bitmap art;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        Uri uri = song.getUri();
        mmr.setDataSource(this, uri);
        try {
            rawArt = mmr.getEmbeddedPicture();
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            albumSongImage.setImageBitmap(art);
        } catch (Exception e) {
            albumSongImage.setImageResource(R.drawable.no_image);
        }
        mAdapter = new AlbumSongsAdapter(song, this);
        albumSongList.setAdapter(mAdapter);
    }
}
