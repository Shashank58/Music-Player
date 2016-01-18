package cybrilla.musicplayer.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;

/**
 * Created by shashankm on 05/01/16.
 */
public class MusicPlayerHelper{
    public static MediaPlayer mediaPlayer;
    private static MusicPlayerHelper instance;
    public static boolean isPaused = false;
    public static List<Song> allSongsList;
    public static int songPosition;

    public static MusicPlayerHelper getInstance(){
        if (instance == null){
            instance = new MusicPlayerHelper();
        }
        return instance;
    }

    public void initializeMediaPlayer(){
        Log.e("Music player helper", "shouldn't start");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                Log.e("Music player helper", "Starting song");
                isPaused = false;
            }
        });
    }

    public void startMusic(int pos){
        songPosition = pos;
        Song s = allSongsList.get(songPosition);
        try {
            mediaPlayer.setDataSource(s.getPath());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleMusicPlayer(ImageView playerController){
        if (mediaPlayer == null)
            initializeMediaPlayer();
        else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPaused = true;
                if (playerController != null)
                    playerController.setImageResource(R.drawable.ic_play);
            } else {
                isPaused = false;
                mediaPlayer.start();
                if (playerController != null)
                    playerController.setImageResource(R.drawable.ic_pause);
            }
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

    public void getSongList(Activity activity){
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
            int dataColumn = musicCursor.getColumnIndex
                    (Media.DATA);
            int albumIdColumn = musicCursor.getColumnIndex
                    (Media.ALBUM_ID);
            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                long duration = musicCursor.getLong(durationColumn);
                String path = musicCursor.getString(dataColumn);
                long albumId = musicCursor.getShort(albumIdColumn);
                allSongsList.add(new Song(id, title, artist, duration, album, path, albumId));
            } while (musicCursor.moveToNext());
            musicCursor.close();
        }
    }

    public void playNextSong(){
        Log.e("Music player helper", "Value of media Player: "+mediaPlayer.isPlaying());
        mediaPlayer.stop();
        mediaPlayer.reset();
        songPosition = songPosition + 1;
        startMusic(songPosition);
    }


    public void playPrevSong(){
        Log.e("Music player helper", "Value of media Player: "+mediaPlayer.isPlaying());
        mediaPlayer.stop();
        mediaPlayer.reset();
        songPosition = songPosition - 1;
        startMusic(songPosition);
    }
}
