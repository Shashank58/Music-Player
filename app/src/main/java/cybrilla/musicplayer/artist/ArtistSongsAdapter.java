package cybrilla.musicplayer.artist;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;

/**
 * Created by shashankm on 26/01/16.
 */
public class ArtistSongsAdapter extends RecyclerView.Adapter<ArtistSongsAdapter
            .ArtistSongsViewHolder> {
    private ArrayList<Song> artistSongs;

    public ArtistSongsAdapter(ArrayList<Song> artistSongs){
        this.artistSongs = new ArrayList<>(artistSongs);
    }

    @Override
    public ArtistSongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_card, parent, false);

        return new ArtistSongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistSongsViewHolder holder, int position) {
        holder.songCard.setTag(position);
        Song song = artistSongs.get(position);
        holder.songTitle.setText(song.getSongTitle());
        holder.songArtist.setText(song.getSongAlbum());
        holder.songImage.setImageResource(R.drawable.no_image);
    }

    @Override
    public int getItemCount() {
        return artistSongs.size();
    }

    public static class ArtistSongsViewHolder extends RecyclerView.ViewHolder{
        protected TextView songTitle, songArtist;
        protected CardView songCard;
        protected ImageView songImage;

        public ArtistSongsViewHolder(View itemView) {
            super(itemView);
            songTitle = (TextView) itemView.findViewById(R.id.song_title);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);
            songCard = (CardView) itemView.findViewById(R.id.songCard);
            songImage = (ImageView) itemView.findViewById(R.id.song_image);
        }
    }
}
