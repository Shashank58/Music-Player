package cybrilla.musicplayer.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.android.MainActivity;
import cybrilla.musicplayer.modle.Song;

/**
 * Created by shashankm on 12/01/16.
 */
public class MediaPlayerService extends Service {
    private Notification notification;
    private RemoteViews views, bigViews;
    private PendingIntent pendingIntent, quitPendingIntent, previousPendingIntent;
    private PendingIntent playpausePendingIntent, nextPendingIntent;
    private NotificationManager mNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager)getSystemService
                (Context.NOTIFICATION_SERVICE);
        Log.e("Media Player Service", "Starting service");

        // Initialize pending intents
        quitPendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("cybrilla.musicplayer.util.quit"), 0);
        previousPendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("cybrilla.musicplayer.util.previous"), 0);
        playpausePendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("cybrilla.musicplayer.util.playpause"), 0);
        nextPendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("cybrilla.musicplayer.util.next"), 0);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                        MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT);

        setUpNotification();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cybrilla.musicplayer.util.quit");
        intentFilter.addAction("cybrilla.musicplayer.util.previous");
        intentFilter.addAction("cybrilla.musicplayer.util.previousNoRestart");
        intentFilter.addAction("cybrilla.musicplayer.util.playpause");
        intentFilter.addAction("cybrilla.musicplayer.util.next");

        registerReceiver(broadcastReceiver, intentFilter);
        startForeground(Constants.FOREGROUND_SERVICE, notification);
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case "cybrilla.musicplayer.util.quit":
                    MusicPlayerHelper.getInstance().getMediaPlayer().stop();
                    MusicPlayerHelper.getInstance().releaseMediaPlayer();
                    unregisterReceiver(broadcastReceiver);
                    stopSelf();
                    break;

                case "cybrilla.musicplayer.util.playpause":
                    toggleMusic();
                    Log.e("Media Player Service", "Play pause working");
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (Constants.STOP_NOTIFICATION.equals(action)){
            unregisterReceiver(broadcastReceiver);
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    private void toggleMusic(){
        if (MusicPlayerHelper.getInstance().getIsPaused()){
            MusicPlayerHelper.getInstance().getMediaPlayer().start();
            MusicPlayerHelper.getInstance().setIsPaused(false);
        } else {
            MusicPlayerHelper.getInstance().setIsPaused(true);
            MusicPlayerHelper.getInstance().getMediaPlayer().pause();
        }
        setUpNotification();
        updateNotification();
    }

    private void setUpNotification(){
        if (views == null || bigViews == null) {
            views = new RemoteViews(getPackageName(),
                    R.layout.status_bar);
            bigViews = new RemoteViews(getPackageName(),
                    R.layout.status_bar_expanded);
            views.setOnClickPendingIntent(R.id.status_bar_play, playpausePendingIntent);
            bigViews.setOnClickPendingIntent(R.id.status_bar_play, playpausePendingIntent);

            views.setOnClickPendingIntent(R.id.status_bar_collapse, quitPendingIntent);
            bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, quitPendingIntent);
        }

        mNotificationManager = (NotificationManager) getSystemService
                (Context.NOTIFICATION_SERVICE);
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getAlbumArt(this));

        Song s = MusicPlayerHelper.allSongsList.get(
                MusicPlayerHelper.getInstance().getSongPosition());

        String songTitle = s.getSongTitle();
        String songArtist = s.getSongArtist();
        String songAlbum = s.getSongAlbum();

        bigViews.setTextViewText(R.id.status_bar_track_name, songTitle);
        views.setTextViewText(R.id.status_bar_track_name, songTitle);

        bigViews.setTextViewText(R.id.status_bar_album_name, songAlbum);
        bigViews.setTextViewText(R.id.status_bar_artist_name, songArtist);
        if (MusicPlayerHelper.getInstance().getIsPaused()) {
            views.setImageViewResource(R.id.status_bar_play,
                    android.R.drawable.ic_media_play);
            bigViews.setImageViewResource(R.id.status_bar_play,
                    android.R.drawable.ic_media_play);
        } else {
            views.setImageViewResource(R.id.status_bar_play,
                    android.R.drawable.ic_media_pause);
            bigViews.setImageViewResource(R.id.status_bar_play,
                    android.R.drawable.ic_media_pause);
        }
        Notification.Builder notificationBuilder =
                    new Notification.Builder(this).setOngoing(true).setAutoCancel(false);
        notification = notificationBuilder.build();
        notification.contentIntent = pendingIntent;
        notification.contentView = views;
        notification.bigContentView = bigViews;
        notification.icon = R.drawable.no_image;
    }

    private void updateNotification(){
        mNotificationManager.notify(Constants.FOREGROUND_SERVICE, notification);
    }
}
