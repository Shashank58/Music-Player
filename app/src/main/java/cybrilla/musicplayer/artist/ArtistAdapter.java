package cybrilla.musicplayer.artist;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Created by shashankm on 22/01/16.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private Activity mActivity;

    public ArtistAdapter(Activity activity){
        mActivity = activity;
    }

    @Override
    public ArtistAdapter.ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_card, parent, false);

        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistAdapter.ArtistViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return MusicPlayerHelper.allSongsList.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder{
        protected ImageView songImage;
        protected TextView songTitle, songArtist;
        protected CardView songCard;


        public ArtistViewHolder(View itemView) {
            super(itemView);
            songImage = (ImageView) itemView.findViewById(R.id.song_image);
            songTitle = (TextView) itemView.findViewById(R.id.song_title);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);
            songCard = (CardView) itemView.findViewById(R.id.songCard);
        }
    }
}
