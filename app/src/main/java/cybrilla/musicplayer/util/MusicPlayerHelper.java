package cybrilla.musicplayer.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;

/**
 * Main helper class responsible for playing song, getting all songs from user's phone.
 * In charge of all functions with respect to media player.
 **/

public class MusicPlayerHelper{
    private MediaPlayer mediaPlayer = null;
    private static MusicPlayerHelper instance;
    private boolean isPaused = false;
    public static List<Song> allSongsList;
    private int songPosition = 0;
    private boolean musicStartedOnce = false; //If false then no song is played before it
    private boolean shuffleOn = false;
    private boolean repeatOn = false;
    public boolean startNotification = true;

    public static MusicPlayerHelper getInstance(){
        if (instance == null){
            instance = new MusicPlayerHelper();
        }
        return instance;
    }

    public void initializeMediaPlayer(){
        if (mediaPlayer == null) {
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

    public boolean getShdStartNotification(){
        return startNotification;
    }

    public void setStartNotification(boolean value){
        startNotification = value;
    }

    public void setMusicStartedOnce(boolean value){
        musicStartedOnce = value;
    }

    public void setIsPaused(boolean pausedValue){
        isPaused = pausedValue;
    }

    public void setSongPosition(int pos){
        songPosition = pos;
    }

    public void setShuffleOn(boolean value){
        shuffleOn = value;
    }

    public void setRepeatOn(boolean value){
        repeatOn = value;
    }

    public int getSongPosition(){
        return songPosition;
    }

    public boolean getMusicStartedOnce(){
        return musicStartedOnce;
    }

    public boolean getShuffleOn(){
        return shuffleOn;
    }

    public boolean getRepeatOn(){
        return repeatOn;
    }

    public void startMusic(int pos){
        musicStartedOnce = true;
        songPosition = pos;
        Song s = allSongsList.get(songPosition);
        try {
            mediaPlayer.setDataSource(s.getPath());
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            isPaused = false;
            completion();
            Log.e("Music player helper", "Path is: " + s.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void completion() {
        MusicPlayerHelper.getInstance().getMediaPlayer().
                setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        if (repeatOn) {
                            startMusic(songPosition);
                        } else if (shuffleOn){
                            playRandomSong();
                            SlidingPanel.getInstance().setPlayingSongDetails();
                        } else {
                            playNextSong();
                            SlidingPanel.getInstance().setPlayingSongDetails();
                        }
                    }
                });
    }

    public void toggleMusicPlayer(ImageView playerController, Activity activity){
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPaused = true;
                if (playerController != null) {
                    Log.e("Music Player Helper", "Pause to Play");
                    playerController.setBackground(null);
                    playerController.setImageResource(0);
                    playerController.setBackground(activity.getResources().getDrawable
                            (R.drawable.pause_to_play));
                    ((AnimationDrawable) playerController.getBackground()).start();
                }
            } else {
                isPaused = false;
                mediaPlayer.start();
                if (playerController != null) {
                    Log.e("Music Player Helper", "Play to Pause");
                    playerController.setBackground(null);
                    playerController.setImageResource(0);
                    playerController.setBackground(activity.getResources().getDrawable
                            (R.drawable.play_to_pause));
                    ((AnimationDrawable) playerController.getBackground()).start();
                }
            }

        } else {
            initializeMediaPlayer();
            startMusic(songPosition);
            if (playerController != null)
                playerController.setImageResource(R.drawable.ic_pause_0);
        }
    }

    public void releaseMediaPlayer(){
        if (mediaPlayer != null) {
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
            Log.e("Music Player Helper", "Stopping and resetting");
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        if (!shuffleOn) {
            if (songPosition < allSongsList.size()) {
                songPosition += 1;
            } else if (songPosition == allSongsList.size()) {
                songPosition = 0;
            }
        } else {
            playRandomSong();
        }
        startMusic(songPosition);
    }

    private void playRandomSong(){
        Random random = new Random();
        songPosition = random.nextInt(allSongsList.size());
    }

    public void playPrevSong(){
        if (mediaPlayer.isPlaying() || isPaused) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        if (!shuffleOn) {
            if (songPosition > 0) {
                songPosition -= 1;
            } else if (songPosition == 0) {
                songPosition = allSongsList.size();
            }
        } else {
            playRandomSong();
        }
        startMusic(songPosition);
    }
}
