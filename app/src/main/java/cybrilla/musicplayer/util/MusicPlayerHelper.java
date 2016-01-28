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

import cybrilla.musicplayer.modle.Song;

/**
 * Main class responsible for playing song, getting all songs from user's phone.
 * In charge of all functions with respect to media player.
 **/

public class MusicPlayerHelper{
    private MediaPlayer mediaPlayer = null;
    private static MusicPlayerHelper instance;
    private boolean isPaused = false;
    public static List<Song> allSongsList;
    private int songPosition;
    private boolean musicStartedOnce = false;

    public static MusicPlayerHelper getInstance(){
        if (instance == null){
            Log.e("Music player helper", "Creating new instance");
            instance = new MusicPlayerHelper();
        }
        return instance;
    }

    public void initializeMediaPlayer(){
        if (mediaPlayer == null) {
            Log.e("Music player helper", "Creating new media player");
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

    public boolean getIsPaused(){
        return isPaused;
    }

    public void setIsPaused(boolean pausedValue){
        isPaused = pausedValue;
    }

    public void setSongPosition(int pos){
        songPosition = pos;
    }

    public int getSongPosition(){
        return songPosition;
    }

    public boolean getMusicStartedOnce(){
        return musicStartedOnce;
    }

    public void startMusic(int pos){
        musicStartedOnce = true;
        this.songPosition = pos;
        Song s = allSongsList.get(songPosition);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(s.getPath());
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            isPaused = false;
            Log.e("Music player helper", "Path is: " + s.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleMusicPlayer(ImageView playerController){
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPaused = true;
                if (playerController != null) {
                    playerController.setImageResource(android.R.drawable.ic_media_play);
                }
            } else {
                isPaused = false;
                mediaPlayer.start();
                if (playerController != null)
                    playerController.setImageResource(android.R.drawable.ic_media_pause);
            }
        } else {
            Log.e("Music Player Helper", "Yay song starting");
            initializeMediaPlayer();
            startMusic(songPosition);
            if (playerController != null)
                playerController.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    public void releaseMediaPlayer(){
        if (mediaPlayer != null) {
            Log.e("Music Player Helper", "Media Player releasing");
            mediaPlayer.stop();
            mediaPlayer.reset();
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
            int songPosition = 0;
            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                long duration = musicCursor.getLong(durationColumn);
                String path = musicCursor.getString(dataColumn);
                long albumId = musicCursor.getShort(albumIdColumn);
                allSongsList.add(new Song(id, title, artist, duration, album, path
                        , albumId, songPosition));
                songPosition++;
            } while (musicCursor.moveToNext());
            musicCursor.close();
        }
    }

    public void playNextSong(){
        if (mediaPlayer.isPlaying() || isPaused) {
            Log.e("Music Player Helper", "Music player is stopping and resetting");
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        songPosition += 1;
        startMusic(songPosition);
    }

    public void playPrevSong(){
        if (mediaPlayer.isPlaying() || isPaused) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        songPosition -= 1;
        startMusic(songPosition);
    }
}
