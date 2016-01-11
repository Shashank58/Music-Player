package cybrilla.musicplayer.modle;

import android.content.ContentUris;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by shashankm on 03/01/16.
 */
public class Song implements Serializable{
    private long songId, songDuration;
    private String songTitle, songArtist, songAlbum;

    public Song(long songId, String songTitle, String songArtist, long duration, String album) {
        this.songId = songId;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songDuration = duration;
        this.songAlbum = album;
    }

    public long getSongId(){
        return songId;
    }

    public String getSongTitle(){
        return songTitle;
    }

    public String getSongArtist(){
        return songArtist;
    }

    public long getSongDuration(){
        return songDuration;
    }


    public String getSongAlbum(){
        return songAlbum;
    }

    public Uri getUri(){
        return ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
    }
}