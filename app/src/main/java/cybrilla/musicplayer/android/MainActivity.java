package cybrilla.musicplayer.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.album.AlbumsActivity;
import cybrilla.musicplayer.allsongs.AllSongsActivity;

public class MainActivity extends AppCompatActivity {
    private CardView allSongs, albums, artists;
    private TextView songsText, albumsText, artistText;
    private static boolean isStartAnimation = true;
    private ViewGroup songCardStart;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isStartAnimation = true;
        initTransitions();

        allSongs = (CardView) findViewById(R.id.songCardAllSongs);
        albums = (CardView) findViewById(R.id.songCardAlbums);
        artists = (CardView) findViewById(R.id.songCardArtist);
        songCardStart = (ViewGroup) findViewById(R.id.parentLayout);
        songsText = (TextView) findViewById(R.id.all_songs);
        albumsText = (TextView) findViewById(R.id.albums);
        artistText = (TextView) findViewById(R.id.artists);

        songsText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, AllSongsActivity.class);
                isStartAnimation = false;
                initTransitions();
            }
        });

        albumsText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, AlbumsActivity.class);
                isStartAnimation = false;
                initTransitions();
            }
        });
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    private void initTransitions() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beginAllViewTransition();
            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    private void beginAllViewTransition(){
        TransitionManager.beginDelayedTransition(songCardStart, new Explode());
        toggleVisibility(allSongs, albums, artists);
    }

    private void toggleVisibility(View... views){
        if (isStartAnimation) {
            for (View view : views) {
                view.setVisibility(View.VISIBLE);
            }
        } else {
            for (View view : views) {
                view.setVisibility(View.INVISIBLE);
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity();
                }
            }, 50);
        }
    }

    @Override
    protected void onResume() {
        isStartAnimation = true;
        intent = null;
        initTransitions();
        super.onResume();
    }

    private void startActivity(){
        startActivity(intent);
    }
}
