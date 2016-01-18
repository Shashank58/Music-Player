package cybrilla.musicplayer.allsongs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.android.SongDetailActivity;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Created by shashankm on 03/01/16.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private Activity mActivity;
    private TextView selectedTractTitle;
    private ImageView playerController, selectedAlbumCover;
    private static final String TAG = "SongAdapter";
    private Toolbar songSelectedToolbar;
    public static int selectedSongPosition;
    private int lastPosition = -1;

    public SongAdapter(Activity activity){
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

    private void songSelected(View view){
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSongPosition = (int) v.getTag();
                if (songSelectedToolbar.getVisibility() == View.GONE) {
                    animateSongPlayerLayout();
                }
                selectedTractTitle = (TextView) mActivity.findViewById(R.id.selected_track_title);
                playerController = (ImageView) mActivity.findViewById(R.id.player_control);
                selectedAlbumCover = (ImageView) mActivity.findViewById(R.id.selected_album_cover);
                ImageView songImage = (ImageView) v.findViewById(R.id.song_image);
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) songImage.getDrawable());
                if (bitmapDrawable != null)
                    selectedAlbumCover.setImageBitmap(bitmapDrawable.getBitmap());
                playerController.setImageResource(R.drawable.ic_pause);
                if (MusicPlayerHelper.getInstance().getMediaPlayer() == null){
                    MusicPlayerHelper.getInstance().initializeMediaPlayer();
                }
                if (MusicPlayerHelper.getInstance().getMediaPlayer() != null &&
                        MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
                    Log.e("Song adapter", "Seriously this?");
                    MusicPlayerHelper.getInstance().getMediaPlayer().stop();
                    MusicPlayerHelper.getInstance().getMediaPlayer().reset();
                }
                int pos = (int) v.getTag();
                MusicPlayerHelper.getInstance().startMusic(pos);
                playerController.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MusicPlayerHelper.getInstance().toggleMusicPlayer(playerController);
                    }
                });
                selectedTractTitle.setText(MusicPlayerHelper.allSongsList.get(pos).getSongTitle());
            }
        });
        setToolBarListener();
    }

    private void setToolBarListener(){
        songSelectedToolbar.setOnClickListener(new OnClickListener() {
            @TargetApi(VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SongDetailActivity.class);
                intent.putExtra(Constants.TITLE_NAME,
                        selectedTractTitle.getText().toString());
                intent.putExtra(Constants.ALBUM_COVER,
                        MusicPlayerHelper.allSongsList.get(selectedSongPosition).getUri().toString());
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation
                                (mActivity, songSelectedToolbar
                                        , songSelectedToolbar.getTransitionName());
                ActivityCompat.startActivity(mActivity, intent, options.toBundle());
                AllSongsActivity.newActivityStarted = true;
            }
        });
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    private void animateSongPlayerLayout(){
        songSelectedToolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBindViewHolder(SongAdapter.SongViewHolder holder, int position) {
        holder.songCard.setTag(position);
        Song song = MusicPlayerHelper.allSongsList.get(position);
        holder.songTitle.setText(song.getSongTitle());
        holder.songArtist.setText(song.getSongArtist());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        Uri uri = song.getUri();
        mmr.setDataSource(mActivity, uri);
        try {
            rawArt = mmr.getEmbeddedPicture();
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            holder.songImage.setImageBitmap(art);
        } catch (Exception e) {
            holder.songImage.setImageResource(R.drawable.ic_action_ic_default_cover);
        } finally {
            setAnimation(holder.songCard, position);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation
                    (mActivity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return MusicPlayerHelper.allSongsList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder{
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
