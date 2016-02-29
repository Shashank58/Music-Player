package cybrilla.musicplayer.view;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import cybrilla.musicplayer.datahelper.MusicPlayerHelper;
import cybrilla.musicplayer.datahelper.SlidingPanel;

/**
 * Displays list of all songs of the selected artist.
 */

public class ArtistSongsAdapter extends RecyclerView.Adapter<ArtistSongsAdapter
            .ArtistSongsViewHolder> {
    private List<Song> artistSongs;
    private Activity mActivity;

    public ArtistSongsAdapter(Activity activity, List<Song> artistSongs){
        this.artistSongs = new ArrayList<>(artistSongs);
        mActivity = activity;
    }

    @Override
    public ArtistSongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_card, parent, false);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                Song song = artistSongs.get(pos);
                int position = song.getSongPosition();
                Log.e("Album Songs Adapter", "Position: "+position);
                if (MusicPlayerHelper.getInstance().getMediaPlayer() == null){
                    MusicPlayerHelper.getInstance().initializeMediaPlayer();
                }
                if (MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()
                        || MusicPlayerHelper.getInstance().getIsPaused()){
                    Log.e("Album Song Adapter", "When song is paused And blah");
                    MusicPlayerHelper.getInstance().getMediaPlayer().stop();
                    MusicPlayerHelper.getInstance().getMediaPlayer().reset();
                }
                MusicPlayerHelper.getInstance().startMusic(position);
                MusicPlayerHelper.getInstance().setIsPaused(false);
                SlidingPanel.getInstance().initializedSlidingLayout(mActivity);
                SlidingPanel.getInstance().setUpSlidingPanel();
                SlidingPanel.getInstance().setPlayingSongDetails();
            }
        });

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
