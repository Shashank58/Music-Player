package cybrilla.musicplayer.view;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.MusicPlayerHelper;
import cybrilla.musicplayer.util.SlidingPanel;

/**
 * Sets all songs recycler view and plays song when a song is selected.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private Activity mActivity;
    private static final String TAG = "SongAdapter";
    private int previouslySelectedSong;

    public SongAdapter(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_card, parent, false);

        songSelected(view);
        return new SongViewHolder(view);
    }

    /**
     * Brightens the
     * @param view the song card that was clicked.
     */
    private void songSelected(final View view) {
        previouslySelectedSong = MusicPlayerHelper.getInstance().getSongPosition();
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                if (v.getRootView().findViewWithTag(previouslySelectedSong) != null) {
                    v.getRootView().findViewWithTag(previouslySelectedSong).setAlpha(0.6f);
                }
                previouslySelectedSong = pos;
                v.findViewById(R.id.songCard).setAlpha(1);
                if (MusicPlayerHelper.getInstance().getMediaPlayer() == null)
                    MusicPlayerHelper.getInstance().initializeMediaPlayer();
                if (MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()
                                || MusicPlayerHelper.getInstance().getIsPaused()) {
                    Log.e("Song adapter", "Seriously this should be called");
                    MusicPlayerHelper.getInstance().getMediaPlayer().stop();
                    MusicPlayerHelper.getInstance().getMediaPlayer().reset();
                }
                MusicPlayerHelper.getInstance().startMusic(pos);
                SlidingPanel.getInstance().setPlayingSongDetails();
            }
        });
    }

    @Override
    public void onBindViewHolder(final SongAdapter.SongViewHolder holder, int position) {
        holder.songCard.setTag(position);
        if (position == MusicPlayerHelper.getInstance().getSongPosition()) {
            holder.songCard.setAlpha(1);
        }
        else {
            holder.songCard.setAlpha(0.6f);
        }
        final Song song = MusicPlayerHelper.allSongsList.get(position);
        holder.songTitle.setText(song.getSongTitle());
        holder.songArtist.setText(song.getSongArtist());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Uri uri = song.getUri();
        mmr.setDataSource(mActivity, uri);
        if (mmr.getEmbeddedPicture() != null) {
            rawArt = mmr.getEmbeddedPicture();
            Glide.with(mActivity).load(rawArt)
                    .asBitmap().into(holder.songImage);
        } else {
            Glide.with(mActivity).load(R.drawable.no_image)
                    .asBitmap().into(holder.songImage);
        }
    }

    @Override
    public int getItemCount() {
        return MusicPlayerHelper.allSongsList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        protected TextView songTitle, songArtist;
        protected CardView songCard;
        protected ImageView songImage;
        protected RelativeLayout songContainer;

        public SongViewHolder(View itemView) {
            super(itemView);
            songTitle = (TextView) itemView.findViewById(R.id.song_title);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);
            songCard = (CardView) itemView.findViewById(R.id.songCard);
            songImage = (ImageView) itemView.findViewById(R.id.song_image);
            songContainer = (RelativeLayout) itemView.findViewById(R.id.song_container);
        }
    }
}
