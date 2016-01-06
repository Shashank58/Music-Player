package cybrilla.musicplayer.android;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs);
        songList = (RecyclerView) findViewById(R.id.songList);
        playingSongToolbar = (Toolbar) findViewById(R.id.playing_song_toolbar);
        selectedSongTrack = (TextView) findViewById(R.id.selected_track_title);

        songList.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        songList.setLayoutManager(linearLayout);

        getSongList();
    }

    private void getSongList(){
        allSongsList = new ArrayList<>();
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        if (musicCursor != null && musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (Media.ALBUM);
            int durationColumn = musicCursor.getColumnIndex
                    (Media.DURATION);
            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                long duration = musicCursor.getLong(durationColumn);
                allSongsList.add(new Song(id, title, artist, duration, album));
            } while (musicCursor.moveToNext());
            musicCursor.close();
        }
        mAdapter = new SongAdapter(allSongsList, this);
        songList.setAdapter(mAdapter);
        setToolBarListener();
    }

    private void setToolBarListener(){
        playingSongToolbar.setOnClickListener(new OnClickListener() {
            @TargetApi(VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllSongsActivity.this, SongDetailActivity.class);
                intent.putExtra(SongDetailActivity.TITLE_NAME,
                        selectedSongTrack.getText().toString());
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation
                                (AllSongsActivity.this, playingSongToolbar
                                        , playingSongToolbar.getTransitionName());
                ActivityCompat.startActivity(AllSongsActivity.this, intent, options.toBundle());
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "Destroy called");
        MusicPlayerHelper.getInstance().releaseMediaPlayer();
        super.onDestroy();
    }
}
