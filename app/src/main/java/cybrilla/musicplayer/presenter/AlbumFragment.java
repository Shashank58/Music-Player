package cybrilla.musicplayer.presenter;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.view.AlbumAdapter;
import cybrilla.musicplayer.util.Constants;
import cybrilla.musicplayer.datahelper.MusicPlayerHelper;

/**
 * Checks for permissions (Android 6.0 and above) and sends data to adapter to set
 * album data.
 */

public class AlbumFragment extends Fragment {
    private RecyclerView albumList;
    private AlbumAdapter mAdapter;

    public AlbumFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.albums_fragment, container, false);

        albumList = (RecyclerView) view.findViewById(R.id.albumList);
        albumList.setHasFixedSize(true);
        albumList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{permission.READ_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                getAlbumList();
            }
        } else {
            getAlbumList();
        }

        return view;
    }

    private void getAlbumList(){
        if (MusicPlayerHelper.allSongsList == null)
            MusicPlayerHelper.getInstance().getSongList(getActivity());
        mAdapter = new AlbumAdapter(getActivity());
        albumList.setAdapter(mAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAlbumList();
                }
            }
        }
    }
}
