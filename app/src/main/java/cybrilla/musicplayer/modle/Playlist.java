package cybrilla.musicplayer.modle;

/**
 * Created by shashankm on 28/01/16.
 */
public class Playlist {
    private int playlistSongPosition;
    private String playlistName;

    public Playlist(int songPosition, String playlistName){
        this.playlistSongPosition = songPosition;
        this.playlistName = playlistName;
    }

    public int getPlaylistSongPosition(){
        return playlistSongPosition;
    }

    public String getPlaylistName(){
        return playlistName;
    }
}
