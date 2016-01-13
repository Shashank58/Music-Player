package cybrilla.musicplayer.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;

public class AlbumSongsActivity extends AppCompatActivity {
    private RecyclerView albumSongList;
    private ImageView albumSongImage;
    private AlbumSongsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);

        albumSongList = (RecyclerView) findViewById(R.id.album_song_list);
        albumSongImage = (ImageView) findViewById(R.id.album_song_image);
        albumSongList.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        albumSongList.setLayoutManager(linearLayout);
        setUpAlbum();
    }

    private void setUpAlbum(){
        Song song = (Song) getIntent().getSerializableExtra("Song");
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
