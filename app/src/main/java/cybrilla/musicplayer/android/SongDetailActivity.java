package cybrilla.musicplayer.android;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cybrilla.musicplayer.R;

public class SongDetailActivity extends AppCompatActivity {
    public static final String TITLE_NAME = "TITLE_NAME";
    private TextView detailSelectedTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_song_detail);
        detailSelectedTrack = (TextView) findViewById(R.id.detail_selected_track);
        detailSelectedTrack.setText(getIntent().getStringExtra(TITLE_NAME));
    }
}
