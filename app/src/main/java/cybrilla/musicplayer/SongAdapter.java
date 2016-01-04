package cybrilla.musicplayer;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashankm on 03/01/16.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> allSongList;
    private Activity mActivity;
    private MediaPlayer mediaPlayer;
    private TextView selectedTractTitle;
    private ImageView playerController;

    public SongAdapter(List<Song> allSongList, Activity activity){
        this.allSongList = new ArrayList<>(allSongList);
        this.mActivity = activity;
    }

    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_card, parent, false);
        setMediaPlayer();
        selectedTractTitle = (TextView) mActivity.findViewById(R.id.selected_track_title);
        playerController = (ImageView) mActivity.findViewById(R.id.player_control);

        songSelected(view);

        return new SongViewHolder(view);
    }

    private void setMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                toggleMusicPlayer();
            }
        });
    }

    private void toggleMusicPlayer(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playerController.setImageResource(R.drawable.ic_play);
        } else {
            mediaPlayer.start();
            playerController.setImageResource(R.drawable.ic_pause);
        }
    }

    private void songSelected(View view){
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                int pos = (int) v.getTag();
                Song s = allSongList.get(pos);
                playerController.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleMusicPlayer();
                    }
                });
                selectedTractTitle.setText(s.getSongTitle());
                try {
                    mediaPlayer.setDataSource(mActivity, s.getUri());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
