package cybrilla.musicplayer.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.adapter.AlbumAdapter;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.MusicPlayerHelper;

public class AlbumsActivity extends AppCompatActivity {
    private RecyclerView albumList;
    private List<Song> allAlbumList;
    private AlbumAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        albumList = (RecyclerView) findViewById(R.id.albumList);
        albumList.setHasFixedSize(true);
        albumList.setLayoutManager(new GridLayoutManager(this, 2));

        getAlbumList();
    }

    private void getAlbumList(){
        allAlbumList = new ArrayList<>(MusicPlayerHelper.getInstance().getSongList(this));
        mAdapter = new AlbumAdapter(allAlbumList, this);
        albumList.setAdapter(mAdapter);
    }
}
