package cybrilla.musicplayer.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.allsongs.AllSongsActivity;
import cybrilla.musicplayer.modle.Song;

/**
 * Created by shashankm on 12/01/16.
 */
public class MediaPlayerService extends Service {
    Notification status;
    private RemoteViews views, bigViews;
    private PendingIntent pendingIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case Constants.STARTFOREGROUND_ACTION:
                showNotification();
                break;

            case Constants.MAIN_ACTION:
                break;

            case Constants.PLAY_ACTION:
                toggleMusicFromNotification();
                break;

            case Constants.NEXT_ACTION:
                MusicPlayerHelper.getInstance().playNextSong();
                startNotification();
                break;

            case Constants.PREV_ACTION:
                MusicPlayerHelper.getInstance().playPrevSong();
                startNotification();
                break;

            case Constants.STOPFOREGROUND_ACTION:
                stopForeground(true);
                MusicPlayerHelper.getInstance().releaseMediaPlayer();
                stopSelf();
                break;
        }
        return START_STICKY;
    }

    private void toggleMusicFromNotification(){
        if (MusicPlayerHelper.mediaPlayer.isPlaying()) {
            MusicPlayerHelper.mediaPlayer.pause();
            views.setImageViewResource(R.id.status_bar_play,
                    android.R.drawable.ic_media_play);
            bigViews.setImageViewResource(R.id.status_bar_play,
                    android.R.drawable.ic_media_play);
            MusicPlayerHelper.isPaused = true;
            startNotification();
        } else {
            MusicPlayerHelper.mediaPlayer.start();
            Log.e("Music service", "Getting called");
            views.setImageViewResource(R.id.status_bar_play,
                    android.R.drawable.ic_media_pause);
            bigViews.setImageViewResource(R.id.status_bar_play,
                    android.R.drawable.ic_media_pause);
            MusicPlayerHelper.isPaused = false;
            startNotification();
        }
    }

    private void startNotification(){
        if (status == null)
            status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.no_image;
        status.contentIntent = pendingIntent;
        Song song = MusicPlayerHelper.allSongsList.get(MusicPlayerHelper.songPosition);

        setSongDetails(song.getSongTitle(), song.getSongArtist(), song.getSongAlbum());
        startForeground(Constants.FOREGROUND_SERVICE, status);
    }

    private void showNotification(){
        views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(this));

        Intent notificationIntent = new Intent(this, AllSongsActivity.class);
        notificationIntent.setAction(Constants.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, MediaPlayerService.class);
        previousIntent.setAction(Constants.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, MediaPlayerService.class);
        playIntent.setAction(Constants.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, MediaPlayerService.class);
        nextIntent.setAction(Constants.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, MediaPlayerService.class);
        closeIntent.setAction(Constants.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        toggleMusicFromNotification();
    }

    public void setSongDetails(String title, String artist, String album){
        views.setTextViewText(R.id.status_bar_track_name, title);
        bigViews.setTextViewText(R.id.status_bar_track_name, title);

        views.setTextViewText(R.id.status_bar_artist_name, artist);
        bigViews.setTextViewText(R.id.status_bar_artist_name, artist);

        bigViews.setTextViewText(R.id.status_bar_album_name, album);
    }
}
