package cybrilla.musicplayer.android;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.adapters.SongAdapter;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MusicPlayerHelper;
import cybrilla.musicplayer.util.SharedPreferenceHandler;

/**
 * Displays all the songs of user. Fetches song position from shared preference
 * and sets it in selected song tool bar if no song is playing. Also checks for
 * user permissions.
 */

public class AllSongsFragment extends Fragment {
    private static final String TAG = "All Songs Fragment";
    private TextView selectedSongTrack, selectedSongArtist;
    private ImageView playerControl, selectedAlbumCover;
    private RecyclerView songList;
    private SongAdapter mAdapter;
    private View view;

    public AllSongsFragment(){

    }

    private void getAllViews(){
        songList = (RecyclerView) view.findViewById(R.id.songList);
        selectedSongTrack = (TextView) getActivity().findViewById(R.id.selected_track_title);
        playerControl = (ImageView) getActivity().findViewById(R.id.player_control);
        selectedAlbumCover = (ImageView) getActivity().findViewById(R.id.selected_album_cover);
        selectedSongArtist = (TextView) getActivity().findViewById(R.id.selected_track_artist);

        songList.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false);
        songList.setLayoutManager(linearLayout);
        getSongList();
        if (!MusicPlayerHelper.getInstance().getMusicStartedOnce() ||
                 MusicPlayerHelper.getInstance().getMediaPlayer() == null) {
            setSongToLastPlayed();
        }
    }

    private void getSongList(){
        MusicPlayerHelper.getInstance().getSongList(getActivity());
        Log.e(TAG, "This is bull");
        mAdapter = new SongAdapter(getActivity());
        songList.setAdapter(mAdapter);
    }

    private void setSongToLastPlayed(){
        int position = SharedPreferenceHandler.getInstance().getSongPosition(getActivity());
        MusicPlayerHelper.getInstance().setSongPosition(position);
        MusicPlayerHelper.getInstance().setIsPaused(true);
        Song song = MusicPlayerHelper.allSongsList.get(position);
        Log.e("All Songs Fragment", "Song title: " + song.getSongTitle());
        selectedSongTrack.setText(song.getSongTitle());
        selectedSongArtist.setText(song.getSongArtist());
        if (MusicPlayerHelper.getInstance().getMediaPlayer() != null) {
            if (MusicPlayerHelper.getInstance().getIsPaused())
                playerControl.setImageResource(android.R.drawable.ic_media_play);
            else
                playerControl.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            playerControl.setImageResource(android.R.drawable.ic_media_play);
        }
        byte[] rawArt;
        Uri uri = song.getUri();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getActivity(), uri);
        Log.e("All songs Fragment", "Are you the freaking reason?");
        if (mmr.getEmbeddedPicture() != null){
            rawArt = mmr.getEmbeddedPicture();
            Glide.with(this).load(rawArt)
                    .asBitmap().into(selectedAlbumCover);
        } else {
            Glide.with(this).load(R.drawable.no_image)
                    .asBitmap().into(selectedAlbumCover);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_all_songs, container, false);
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "printing?");
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{permission.READ_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                getAllViews();
            }
        } else {
            getAllViews();
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllViews();
                }
            }
        }
    }
}
