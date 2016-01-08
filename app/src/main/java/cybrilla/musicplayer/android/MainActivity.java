package cybrilla.musicplayer.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import cybrilla.musicplayer.R;

public class MainActivity extends AppCompatActivity {
    private TextView allSongs, albums, artists;
    private static boolean isStartAnimation = true;
    private ViewGroup songCardStart;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isStartAnimation = true;
        initTransitions();

        allSongs = (TextView) findViewById(R.id.all_songs);
        albums = (TextView) findViewById(R.id.albums);
        artists = (TextView) findViewById(R.id.artists);
        songCardStart = (ViewGroup) findViewById(R.id.songCardStart);

        allSongs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, AllSongsActivity.class);
                isStartAnimation = false;
                initTransitions();
            }
        });

        albums.setOnClickListener(new OnClickListener() {
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
            }, 150);

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
