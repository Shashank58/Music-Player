package cybrilla.musicplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;

/**
 * Created by shashankm on 11/01/16.
 */
public class Constants {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    public static final int FOREGROUND_SERVICE = 101;
    public static final int PRIVATE_MODE = 2;
    public static final String TITLE_NAME = "TITLE_NAME";
    public static final String ALBUM_COVER = "ALBUM_COVER";
    public static final String SONG_KEY = "Song Position Key";
    public static final String SONG_NUMBER = "Song Number";

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Song song = MusicPlayerHelper.allSongsList.get(
                MusicPlayerHelper.getInstance().getSongPosition());
        Uri uri = song.getUri();
        mmr.setDataSource(context, uri);
        if (mmr.getEmbeddedPicture() != null) {
            rawArt = mmr.getEmbeddedPicture();
            bm = BitmapFactory.decodeByteArray(rawArt, 0,
                    rawArt.length, options);
        } else {
            try {
                bm = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.no_image, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bm;
    }
}
