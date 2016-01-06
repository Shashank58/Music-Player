package cybrilla.musicplayer.android;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.util.MusicPlayerHelper;

public class SongDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TITLE_NAME = "TITLE_NAME";
    private TextView detailSelectedTrack;
    private ImageView detailController, detailFastForward, detailReverse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_song_detail);
        detailSelectedTrack = (TextView) findViewById(R.id.detail_selected_track);
        detailController = (ImageView) findViewById(R.id.detail_controller);
        detailFastForward = (ImageView) findViewById(R.id.detail_fast_forward);
        detailReverse = (ImageView) findViewById(R.id.detail_reverse);

        if (MusicPlayerHelper.mediaPlayer.isPlaying())
            detailController.setImageResource(android.R.drawable.ic_media_pause);

        detailSelectedTrack.setText(getIntent().getStringExtra(TITLE_NAME));
        detailController.setOnClickListener(this);
        detailFastForward.setOnClickListener(this);
        detailReverse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detail_controller:
                MusicPlayerHelper.getInstance().toggleMusicPlayer(detailController);
                break;

            case R.id.detail_fast_forward:
                detailController.setImageResource(android.R.drawable.ic_media_pause);
                String title = MusicPlayerHelper.getInstance()
                        .playNextSong(SongDetailActivity.this);
                detailSelectedTrack.setText(title);
                break;

            case R.id.detail_reverse:
                detailController.setImageResource(android.R.drawable.ic_media_pause);
                String titleSong = MusicPlayerHelper.getInstance()
                                .playPrevSong(SongDetailActivity.this);
                detailSelectedTrack.setText(titleSong);
                break;
        }
    }
}
