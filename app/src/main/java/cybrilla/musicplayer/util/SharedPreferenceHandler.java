package cybrilla.musicplayer.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Stores position of last song played in Shared preference and retrieves it
 * when needed.
 */

public class SharedPreferenceHandler {
    private static SharedPreferenceHandler instance;
    private SharedPreferences pref;
    private Editor editor;

    public static SharedPreferenceHandler getInstance(){
        if (instance == null)
            instance = new SharedPreferenceHandler();
        return instance;
    }

    public void setSongPosition(Activity activity, int position){
        pref = activity.getSharedPreferences(Constants.SONG_KEY, Constants.PRIVATE_MODE);
        editor = pref.edit();

        editor.putInt(Constants.SONG_NUMBER, position);
        editor.apply();
    }

    public int getSongPosition(Activity activity){
        pref = activity.getSharedPreferences(Constants.SONG_KEY, Constants.PRIVATE_MODE);
        return pref.getInt(Constants.SONG_NUMBER, 0);
    }
}
