package cybrilla.musicplayer.android;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.adapter.AlbumAdapter;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MusicPlayerHelper;

public class AlbumsActivity extends AppCompatActivity {
    private RecyclerView albumList;
    private List<Song> allAlbumList;
    private AlbumAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if ( ContextCompat.checkSelfPermission(this,
                permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{permission.READ_EXTERNAL_STORAGE},
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_albums);
            albumList = (RecyclerView) findViewById(R.id.albumList);
            albumList.setHasFixedSize(true);
            albumList.setLayoutManager(new GridLayoutManager(this, 2));

            getAlbumList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }
    }

    private void getAlbumList(){
        MusicPlayerHelper.getInstance().getSongList(this);
        mAdapter = new AlbumAdapter(this);
        albumList.setAdapter(mAdapter);
    }
}
