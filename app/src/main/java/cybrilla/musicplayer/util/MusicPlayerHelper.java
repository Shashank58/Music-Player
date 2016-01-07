package cybrilla.musicplayer.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.modle.Song;

/**
 * Created by shashankm on 05/01/16.
 */
public class MusicPlayerHelper{
    public static MediaPlayer mediaPlayer;
    private static MusicPlayerHelper instance;
    public static boolean isPaused = false;
    private List<Song> allSongsList;
    private static int songPosition;

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

    public String startMusic(int pos, Activity activity){
        songPosition = pos;
        Song s = allSongsList.get(songPosition);
        try {
            mediaPlayer.setDataSource(activity, s.getUri());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.getSongTitle();
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

    public List<Song> getSongList(Activity activity){
        allSongsList = new ArrayList<>();
        ContentResolver musicResolver = activity.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        if (musicCursor != null && musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (Media.ALBUM);
            int durationColumn = musicCursor.getColumnIndex
                    (Media.DURATION);

            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                long duration = musicCursor.getLong(durationColumn);
                allSongsList.add(new Song(id, title, artist, duration, album));
            } while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return allSongsList;
    }

    public String getSongTitle(){
        return allSongsList.get(songPosition).getSongTitle();
    }

    public Uri getSongUri() {
        return allSongsList.get(songPosition).getUri();
    }

    public String playNextSong(Activity activity){
        mediaPlayer.stop();
        mediaPlayer.reset();
        songPosition = songPosition + 1;
        return startMusic(songPosition, activity);
    }

    public String playPrevSong(Activity activity){
        mediaPlayer.stop();
        mediaPlayer.reset();
        songPosition = songPosition - 1;
        return startMusic(songPosition, activity);
    }
}
