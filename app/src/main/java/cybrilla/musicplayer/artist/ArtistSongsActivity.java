package cybrilla.musicplayer.artist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;

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
        ArrayList<Song> songs = new ArrayList<>
                ((ArrayList<Song>) getIntent().getExtras().get("Songs"));
        ArtistSongsAdapter adapter = new ArtistSongsAdapter(songs);
        artistSongsList.setAdapter(adapter);
    }
}
