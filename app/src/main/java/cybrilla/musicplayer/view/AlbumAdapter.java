package cybrilla.musicplayer.view;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.presenter.AlbumSongActivity;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Sets up all albums.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private Activity mActivity;

    public AlbumAdapter(Activity mActivity){
        this.mActivity = mActivity;
    }

    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.album_card, parent, false);

        view.setOnClickListener(new OnClickListener() {
            @TargetApi(VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                MusicPlayerHelper.getInstance().setStartNotification(false);
                int pos = (int) v.getTag();
                ImageView imageStart = (ImageView) v.findViewById(R.id.album_image);
                Intent intent = new Intent(mActivity, AlbumSongActivity.class);
                intent.putExtra(Constants.SONG_POSITION, pos);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation
                        (mActivity,imageStart, imageStart.getTransitionName());
                ActivityCompat.startActivity(mActivity, intent, options.toBundle());
            }
        });

        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlbumAdapter.AlbumViewHolder holder, int position) {
        holder.albumCard.setTag(position);
        Song song = MusicPlayerHelper.allSongsList.get(position);
        holder.albumName.setText(song.getSongAlbum());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Uri uri = song.getUri();
        mmr.setDataSource(mActivity, uri);
        //if (mmr.getEmbeddedPicture() != null) {
            rawArt = mmr.getEmbeddedPicture();
            Glide.with(mActivity).load(rawArt).placeholder(R.drawable.no_image)
                    .crossFade().into(holder.albumImage);
//        } else {
//            Glide.with(mActivity).load(R.drawable.no_image)
//                    .asBitmap().into(holder.albumImage);
//        }
    }

    @Override
    public int getItemCount() {
        return MusicPlayerHelper.allSongsList.size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder{
        protected ImageView albumImage;
        protected TextView albumName;
        protected CardView albumCard;
        protected LinearLayout albumContainer;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            albumImage = (ImageView) itemView.findViewById(R.id.album_image);
            albumName = (TextView) itemView.findViewById(R.id.album_name);
            albumCard = (CardView) itemView.findViewById(R.id.albumCard);
            albumContainer = (LinearLayout) itemView.findViewById(R.id.album_container);
        }
    }
}
