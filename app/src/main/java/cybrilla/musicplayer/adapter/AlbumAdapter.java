package cybrilla.musicplayer.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.android.AlbumSongsActivity;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Created by shashankm on 08/01/16.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private Activity mActivity;
    private int lastPosition = -1;

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
                int pos = (int) v.getTag();
                Song song = MusicPlayerHelper.allSongsList.get(pos);
                ImageView imageStart = (ImageView) v.findViewById(R.id.album_image);
                Intent intent = new Intent(mActivity, AlbumSongsActivity.class);
                intent.putExtra("Song", song);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation
                        (mActivity,imageStart, imageStart.getTransitionName());
                ActivityCompat.startActivity(mActivity, intent, options.toBundle());
            }
        });

        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.AlbumViewHolder holder, int position) {
        holder.albumCard.setTag(position);
        Song song = MusicPlayerHelper.allSongsList.get(position);
        holder.albumName.setText(song.getSongAlbum());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        Uri uri = song.getUri();
        mmr.setDataSource(mActivity, uri);
        try {
            rawArt = mmr.getEmbeddedPicture();
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            holder.albumImage.setImageBitmap(art);
        } catch (Exception e) {
            holder.albumImage.setImageResource(R.drawable.no_image);
        } finally {
            setAnimation(holder.albumCard, position);
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

    public static class AlbumViewHolder extends RecyclerView.ViewHolder{
        private ImageView albumImage;
        private TextView albumName;
        private CardView albumCard;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            albumImage = (ImageView) itemView.findViewById(R.id.album_image);
            albumName = (TextView) itemView.findViewById(R.id.album_name);
            albumCard = (CardView) itemView.findViewById(R.id.albumCard);
        }
    }
}
