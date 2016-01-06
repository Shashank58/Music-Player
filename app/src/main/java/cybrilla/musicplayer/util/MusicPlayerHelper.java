package cybrilla.musicplayer.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.ImageView;

/**
 * Created by shashankm on 05/01/16.
 */
public class MusicPlayerHelper {
    public static MediaPlayer mediaPlayer;
    private static MusicPlayerHelper instance;
    public static boolean isPaused = false;

    public static MusicPlayerHelper getInstance(){
        if (instance == null){
            instance = new MusicPlayerHelper();
        }
        return instance;
    }

    public void initializeMediaPlayer(final ImageView playerController){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                toggleMusicPlayer(playerController);
            }
        });
    }

    public void toggleMusicPlayer(ImageView playerController){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPaused = true;
            if (playerController != null)
                playerController.setImageResource(android.R.drawable.ic_media_play);
        } else {
            isPaused = false;
            mediaPlayer.start();
            if (playerController != null)
                playerController.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    public void releaseMediaPlayer(){
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
