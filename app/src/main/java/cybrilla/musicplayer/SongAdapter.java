package cybrilla.musicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashankm on 03/01/16.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> allSongList;

    public SongAdapter(List<Song> allSongList){
        this.allSongList = new ArrayList<>(allSongList);
    }

    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_card, parent, false);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                Song s = allSongList.get(pos);
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
        });

        return new SongViewHolder(view);
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
