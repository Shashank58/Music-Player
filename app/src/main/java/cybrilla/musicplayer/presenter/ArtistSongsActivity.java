package cybrilla.musicplayer.presenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.view.ArtistSongsAdapter;
import cybrilla.musicplayer.modle.Song;

/**
 * Gets the Songs sent in from Artist adapter and sends it to it's own adapter
 * to show as the artist's songs.
 */

public class ArtistSongsActivity extends AppCompatActivity {
    private RecyclerView artistSongsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_songs);
        artistSongsList = (RecyclerView) findViewById(R.id.artist_songs_list);
        artistSongsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        artistSongsList.setLayoutManager(linearLayoutManager);
        List<Song> songs = ((ArrayList<Song>) getIntent().getExtras().get(Constants.SONGS));
        ArtistSongsAdapter adapter = new ArtistSongsAdapter(this, songs);
        artistSongsList.setAdapter(adapter);
    }
}
