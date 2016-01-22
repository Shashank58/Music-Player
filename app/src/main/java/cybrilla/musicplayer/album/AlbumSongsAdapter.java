package cybrilla.musicplayer.album;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.android.SongDetailActivity;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Created by shashankm on 11/01/16.
 */
public class AlbumSongsAdapter extends
        RecyclerView.Adapter<AlbumSongsAdapter.AlbumSongsViewHolder> {
    private Activity mActivity;
    private Song song;

    public AlbumSongsAdapter(Song song, Activity activity){
        this.song = song;
        mActivity = activity;
    }

    @Override
    public AlbumSongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_card, parent, false);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Album Songs Adapter", "Contains? "+MusicPlayerHelper.allSongsList.contains(song));
                int pos = MusicPlayerHelper.allSongsList.indexOf(song);
                Log.e("Album Songs Adapter", "Position: "+pos);
                if (MusicPlayerHelper.getInstance().getMediaPlayer() == null){
                    MusicPlayerHelper.getInstance().initializeMediaPlayer();
                }
                if (MusicPlayerHelper.getInstance().getMediaPlayer().isPlaying()
                        || MusicPlayerHelper.getInstance().getIsPaused()){
                    Log.e("Album Song Adapter", "When song is paused And blah");
                    MusicPlayerHelper.getInstance().getMediaPlayer().stop();
                    MusicPlayerHelper.getInstance().getMediaPlayer().reset();
                }
                MusicPlayerHelper.getInstance().startMusic(pos);
                Intent intent = new Intent(mActivity, SongDetailActivity.class);
                intent.putExtra(Constants.TITLE_NAME, song.getSongTitle());
                mActivity.startActivity(intent);
            }
        });
        return new AlbumSongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumSongsViewHolder holder, int position) {
        holder.songTitle.setText(song.getSongTitle());
        holder.songArtist.setText(song.getSongArtist());
        holder.songCard.setAlpha(1);
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
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class AlbumSongsViewHolder extends RecyclerView.ViewHolder{
        protected ImageView songImage;
        protected TextView songTitle, songArtist;
        protected CardView songCard;

        public AlbumSongsViewHolder(View itemView) {
            super(itemView);
            songImage = (ImageView) itemView.findViewById(R.id.song_image);
            songTitle = (TextView) itemView.findViewById(R.id.song_title);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);
            songCard = (CardView) itemView.findViewById(R.id.songCard);
        }
    }
}
