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

/**
 * Sets all songs recycler view and plays song when a song is selected.
 * Also brings up SongDetailActivity when selected song toolbar is clicked.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private Activity mActivity;
    private TextView selectedTractTitle, selectedTrackArtist;
    private ImageView playerController, selectedAlbumCover;
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

    private void songSelected(final View view) {
        playerController = (ImageView) mActivity.findViewById(R.id.player_control);
        selectedTractTitle = (TextView) mActivity.findViewById(R.id.selected_track_title);
        selectedTrackArtist = (TextView) mActivity.findViewById(R.id.selected_track_artist);
        previouslySelectedSong = MusicPlayerHelper.getInstance().getSongPosition();
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAlbumCover = (ImageView) mActivity.findViewById(R.id.selected_album_cover);
                int pos = (int) v.getTag();
                if (v.getRootView().findViewWithTag(previouslySelectedSong) != null) {
                    Log.e(TAG, "Working?");
                    v.getRootView().findViewWithTag(previouslySelectedSong).setAlpha(0.6f);
                }
                previouslySelectedSong = pos;
                v.findViewById(R.id.songCard).setAlpha(1);
                Song song = MusicPlayerHelper.allSongsList.get(pos);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                byte[] rawArt;
                Uri uri = song.getUri();
                mmr.setDataSource(mActivity, uri);
                if (mmr.getEmbeddedPicture() != null) {
                    rawArt = mmr.getEmbeddedPicture();
                    Glide.with(mActivity).load(rawArt)
                            .asBitmap().into(selectedAlbumCover);
                } else {
                    Glide.with(mActivity).load(R.drawable.no_image)
                            .asBitmap().into(selectedAlbumCover);
                }
                playerController.setImageResource(android.R.drawable.ic_media_pause);
                if (MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()
                                || MusicPlayerHelper.getInstance().getIsPaused()) {
                    Log.e("Song adapter", "Seriously this should be called");
                    MusicPlayerHelper.getInstance().getMediaPlayer().stop();
                    MusicPlayerHelper.getInstance().getMediaPlayer().reset();
                }
                MusicPlayerHelper.getInstance().startMusic(pos);
                selectedTrackArtist.setText(MusicPlayerHelper.allSongsList.get(pos).getSongArtist());
                selectedTractTitle.setText(MusicPlayerHelper.allSongsList.get(pos).getSongTitle());
            }
        });

//        playerController.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
//                    MusicPlayerHelper.getInstance().toggleMusicPlayer(playerController);
//                } else {
//                    MusicPlayerHelper.getInstance().startMusic(MusicPlayerHelper
//                        .getInstance().getSongPosition());
//                    playerController.setImageResource(android.R.drawable.ic_media_pause);
//                }
//            }
//        });
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
