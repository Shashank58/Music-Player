package cybrilla.musicplayer.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;

/**
 * Created by shashankm on 08/01/16.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private List<Song> allAlbumsList;
    private Activity mActivity;

    public AlbumAdapter(List<Song> allAlbumsList, Activity mActivity){
        this.allAlbumsList = new ArrayList<>(allAlbumsList);
        this.mActivity = mActivity;
    }

    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.album_card, parent, false);

        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.AlbumViewHolder holder, int position) {
        holder.albumCard.setTag(position);
        Song song = allAlbumsList.get(position);
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
            holder.albumImage.setImageResource(R.drawable.ic_action_ic_default_cover);
        }
    }

    @Override
    public int getItemCount() {
        return allAlbumsList.size();
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
