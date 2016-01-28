package cybrilla.musicplayer.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.android.SongDetailActivity;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
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
    private Toolbar songSelectedToolbar;
    private int selectedSong = -1;

    public SongAdapter(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_card, parent, false);

        songSelectedToolbar = (Toolbar) mActivity.findViewById(R.id.playing_song_toolbar);
        songSelected(view);
        return new SongViewHolder(view);
    }

    private void songSelected(final View view) {
        playerController = (ImageView) mActivity.findViewById(R.id.player_control);
        selectedTractTitle = (TextView) mActivity.findViewById(R.id.selected_track_title);
        selectedTrackArtist = (TextView) mActivity.findViewById(R.id.selected_track_artist);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAlbumCover = (ImageView) mActivity.findViewById(R.id.selected_album_cover);
                int pos = (int) v.getTag();
                if (selectedSong != -1){
                    if (v.getRootView().findViewWithTag(selectedSong) != null)
                      v.getRootView().findViewWithTag(selectedSong).setAlpha(0.6f);
                }
                selectedSong = pos;
                v.getRootView().findViewWithTag(pos).setAlpha(1);
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
                if (MusicPlayerHelper.getInstance().getMediaPlayer() == null) {
                    MusicPlayerHelper.getInstance().initializeMediaPlayer();
                }
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

        playerController.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerHelper.getInstance().toggleMusicPlayer(playerController);
            }
        });
        setToolBarListener();
    }

    private void setToolBarListener() {
        songSelectedToolbar.setOnClickListener(new OnClickListener() {
            @TargetApi(VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SongDetailActivity.class);
                intent.putExtra(Constants.TITLE_NAME,
                        selectedTractTitle.getText().toString());
                intent.putExtra(Constants.SONG_ARTIST,
                        selectedTrackArtist.getText().toString());
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation
                                (mActivity, songSelectedToolbar
                                        , songSelectedToolbar.getTransitionName());
                ActivityCompat.startActivity(mActivity, intent, options.toBundle());
            }
        });
    }

    @Override
    public void onBindViewHolder(SongAdapter.SongViewHolder holder, int position) {
        holder.songCard.setTag(position);
        if (position == selectedSong)
            holder.songCard.setAlpha(1);
        else
            holder.songCard.setAlpha(0.6f);
        Song song = MusicPlayerHelper.allSongsList.get(position);
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

        public SongViewHolder(View itemView) {
            super(itemView);
            songTitle = (TextView) itemView.findViewById(R.id.song_title);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);
            songCard = (CardView) itemView.findViewById(R.id.songCard);
            songImage = (ImageView) itemView.findViewById(R.id.song_image);
        }
    }
}
