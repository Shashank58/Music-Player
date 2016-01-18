package cybrilla.musicplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cybrilla.musicplayer.R;

/**
 * Created by shashankm on 11/01/16.
 */
public class Constants {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    public static final String TITLE_NAME = "TITLE_NAME";
    public static final String ALBUM_COVER = "ALBUM_COVER";
    public static final String MAIN_ACTION = "cybrilla.musicplayer.action.allSongs";
    public static final String PREV_ACTION = "cybrilla.musicplayer.action.prev";
    public static final String PLAY_ACTION = "cybrilla.musicplayer.action.play";
    public static final String NEXT_ACTION = "cybrilla.musicplayer.action.next";
    public static final String STOP_NOTIFICATION = "cybrilla.musicplayer.action.stopnotification";
    public static final String STARTFOREGROUND_ACTION = "cybrilla.musicplayer.action.startforeground";
    public static final String STOPFOREGROUND_ACTION = "cybrilla.musicplayer.action.stopforeground";
    public static final int FOREGROUND_SERVICE = 101;
    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.no_image, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }
}
