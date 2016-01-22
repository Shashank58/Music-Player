package cybrilla.musicplayer.artist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.util.MusicPlayerHelper;

/**
 * Created by shashankm on 22/01/16.
 */
public class ArtistFragment extends Fragment {
    private RecyclerView artistList;
    private ArtistAdapter mAdapter;
    
    public ArtistFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artist_fragment, container, false);
        artistList = (RecyclerView) view.findViewById(R.id.artist_list);
        artistList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        artistList.setLayoutManager(linearLayoutManager);
        getArtistList();

        return view;
    }

    private void getArtistList(){
        if (MusicPlayerHelper.allSongsList == null)
            MusicPlayerHelper.getInstance().getSongList(getActivity());
        mAdapter = new ArtistAdapter(getActivity());
        artistList.setAdapter(mAdapter);
    }
}
