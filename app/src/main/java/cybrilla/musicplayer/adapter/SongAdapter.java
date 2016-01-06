package cybrilla.musicplayer.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build.VERSION_CODES;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Created by shashankm on 03/01/16.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> allSongList;
    private Activity mActivity;
    private TextView selectedTractTitle;
    private ImageView playerController;
    private static final String TAG = "SongAdapter";
    private Toolbar songSelectedToolbar;

    public SongAdapter(List<Song> allSongList, Activity activity){
        this.allSongList = new ArrayList<>(allSongList);
        this.mActivity = activity;
    }

    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_card, parent, false);
        if (MusicPlayerHelper.mediaPlayer == null) {
            MusicPlayerHelper.getInstance().initializeMediaPlayer(playerController);
        }
        songSelectedToolbar = (Toolbar) mActivity.findViewById(R.id.playing_song_toolbar);

        songSelected(view);

        return new SongViewHolder(view);
    }

    private void songSelected(View view){
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songSelectedToolbar.getVisibility() == View.GONE) {
                    animateSongPlayerLayout();
                    selectedTractTitle = (TextView) mActivity.findViewById(R.id.selected_track_title);
                    playerController = (ImageView) mActivity.findViewById(R.id.player_control);
                }
                playerController.setImageResource(android.R.drawable.ic_media_pause);
                if (MusicPlayerHelper.mediaPlayer.isPlaying() || MusicPlayerHelper.isPaused) {
                    MusicPlayerHelper.mediaPlayer.stop();
                    MusicPlayerHelper.mediaPlayer.reset();
                }
                int pos = (int) v.getTag();
                String title = MusicPlayerHelper.getInstance().startMusic(pos, mActivity);
                playerController.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MusicPlayerHelper.getInstance().toggleMusicPlayer(playerController);
                    }
                });
                selectedTractTitle.setText(title);
            }
        });
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    private void animateSongPlayerLayout(){
        //TransitionManager.beginDelayedTransition(songSelectedToolbar, new Fade());
        songSelectedToolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBindViewHolder(SongAdapter.SongViewHolder holder, int position) {
        holder.songCard.setTag(position);
        Song song = allSongList.get(position);
        holder.songTitle.setText(song.getSongTitle());
        holder.songArtist.setText(song.getSongArtist());
    }

    @Override
    public int getItemCount() {
        return allSongList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder{
        protected TextView songTitle, songArtist;
        protected CardView songCard;

        public SongViewHolder(View itemView) {
            super(itemView);
            songTitle = (TextView) itemView.findViewById(R.id.song_title);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);
            songCard = (CardView) itemView.findViewById(R.id.songCard);
        }
    }
}
