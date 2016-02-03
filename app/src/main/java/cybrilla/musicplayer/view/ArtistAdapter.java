package cybrilla.musicplayer.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.presenter.ArtistSongsActivity;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Displays list of Artists and the number of songs for each artist.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private Activity mActivity;
    private HashMap<String, ArrayList<Song>> allArtistList;
    Iterator myVeryOwnIterator;

    public ArtistAdapter(HashMap<String, ArrayList<Song>> allArtistList, Activity activity){
        this.allArtistList = new HashMap<>(allArtistList);
        mActivity = activity;
        myVeryOwnIterator = this.allArtistList.keySet().iterator();
    }

    @Override
    public ArtistAdapter.ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_card, parent, false);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerHelper.getInstance().setStartNotification(false);
                TextView artistName = (TextView) v.findViewById(R.id.song_title);
                ArrayList<Song> songs = allArtistList.get(artistName.getText().toString());
                Intent intent = new Intent(mActivity, ArtistSongsActivity.class);
                intent.putExtra(Constants.SONGS, songs);
                mActivity.startActivity(intent);
            }
        });
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistAdapter.ArtistViewHolder holder, int position) {
        holder.songImage.setImageResource(R.drawable.no_image);
        if (myVeryOwnIterator.hasNext()){
            String artist = (String) myVeryOwnIterator.next();
            holder.songTitle.setText(artist);
            String size = String.valueOf(allArtistList.get(artist).size());
            if ("1".equals(size))
                holder.songArtist.setText(size+" song");
            else
                holder.songArtist.setText(size+" songs");
        }
    }

    @Override
    public int getItemCount() {
        return allArtistList.size();
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
