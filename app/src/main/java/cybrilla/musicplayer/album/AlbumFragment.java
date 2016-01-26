package cybrilla.musicplayer.album;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Created by shashankm on 21/01/16.
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
        View view = inflater.inflate(R.layout.activity_albums, container, false);

        albumList = (RecyclerView) view.findViewById(R.id.albumList);
        albumList.setHasFixedSize(true);
        albumList.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        getAlbumList();
        return view;
    }

    private void getAlbumList(){
        if (MusicPlayerHelper.allSongsList == null)
            MusicPlayerHelper.getInstance().getSongList(getActivity());
        mAdapter = new AlbumAdapter(getActivity());
        albumList.setAdapter(mAdapter);
    }
}
