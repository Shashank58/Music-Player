package cybrilla.musicplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.modle.Playlist;


/**
 * Created by shashankm on 28/01/16.
 */
public class PlaylistDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "playlist.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "playlist_table";
    private static final String SONG_POSITION = "song_position";
    private static final String PLAYLIST_NAME = "playlist_name";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            SONG_POSITION + " INTEGER PRIMARY KEY, " + PLAYLIST_NAME + " TEXT)";
    private SQLiteDatabase db;

    public PlaylistDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public int getCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT * from " + TABLE_NAME;
        Cursor c = db.rawQuery(countQuery, null);
        c.moveToFirst();
        db.close();
        if (c.getCount() > 0) {
            return c.getInt(0);
        }
        else {
            c.close();
            return 0;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertValues(int songPosition, String playlistName){
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_POSITION, songPosition);
        contentValues.put(PLAYLIST_NAME, playlistName);
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public void insertValues(List<Playlist> playlistSongs){
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (Playlist playlist : playlistSongs){
            contentValues.put(SONG_POSITION, playlist.getPlaylistSongPosition());
            contentValues.put(PLAYLIST_NAME, playlist.getPlaylistName());
            db.insert(TABLE_NAME, null, contentValues);
        }
        db.close();
    }

    public List<Playlist> getAllPlaylistSongs(){
        List<Playlist> playlistSongs = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()){
            do {
                playlistSongs.add(new Playlist(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return playlistSongs;
    }
}
