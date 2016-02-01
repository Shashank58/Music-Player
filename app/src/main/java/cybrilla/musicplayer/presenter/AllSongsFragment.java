package cybrilla.musicplayer.presenter;

import android.Manifest.permission;
import android.content.pm.PackageManager;
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

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.view.SongAdapter;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.util.MusicPlayerHelper;
import cybrilla.musicplayer.util.SlidingPanel;

/**
 * Sends data containing all songs of user. Checks if music has been started once.
 * If not asks Sliding Panel class to set song to last played. Also checks for
 * user permissions.
 */

public class AllSongsFragment extends Fragment {
    private static final String TAG = "All Songs Fragment";
    private RecyclerView songList;
    private SongAdapter mAdapter;
    private View view;

    public AllSongsFragment(){

    }

    private void getAllViews(){
        songList = (RecyclerView) view.findViewById(R.id.songList);

        songList.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false);
        songList.setLayoutManager(linearLayout);
        getSongList();
        if (!MusicPlayerHelper.getInstance().getMusicStartedOnce()) {
            SlidingPanel.getInstance().setSongToLastPlayed();
        } else {
            SlidingPanel.getInstance().setPlayerControl();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SlidingPanel.getInstance().setPlayingSongDetails();
    }

    private void getSongList(){
        MusicPlayerHelper.getInstance().getSongList(getActivity());
        mAdapter = new SongAdapter(getActivity());
        songList.setAdapter(mAdapter);
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
