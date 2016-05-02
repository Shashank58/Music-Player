package cybrilla.musicplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.datahelper.MusicPlayerHelper;
import cybrilla.musicplayer.modle.Song;

/**
 * Created by shashankm on 11/01/16.
 */
public class Constants {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    public static final int FOREGROUND_SERVICE = 101;
    public static final int PRIVATE_MODE = 2;
    public static final int DEFAULT_MAX = 10001;
    public static final String SONG_KEY = "Song Position Key";
    public static final String SONG_NUMBER = "Song Number";
    public static final String SONG_POSITION = "Song Position";
    public static final String SONGS = "Songs";
    private static final String PACKAGE = "cybrilla.musicplayer";
    public static final String STOP_NOTIFICATION = PACKAGE + "action.stopnotification";
    public static final String MUSIC_PLAYER_QUIT = PACKAGE + ".util.quit";
    public static final String MUSIC_PLAYER_PREVIOUS = PACKAGE + ".util.previous";
    public static final String MUSIC_PLAYER_PLAY_PAUSE = PACKAGE + ".util.playpause";
    public static final String MUSIC_PLAYER_NEXT = PACKAGE + ".util.next";

    /**
     * Sets album art in the notification.
     *
     * @param context service context
     * @return album's image or default image if no album art exists
     */
    public static Bitmap getAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        if (MusicPlayerHelper.allSongsList != null) {
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
                            R.drawable.default_image, options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bm;
        } else {
            return null;
        }
    }
}
