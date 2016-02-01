package cybrilla.musicplayer.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.modle.Song;
import cybrilla.musicplayer.util.MusicPlayerHelper;
import cybrilla.musicplayer.view.ArtistAdapter;

/**
 * Classifies songs based on artist name. Responsible for mapping all related songs
 * to artist and sending the hash map containing the result to adapter for display.
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

    /**
     * Hash map with key as Artist name (String) and and array list of Song
     * object associated to that artist.
     */

    private void getArtistList(){
        HashMap<String, ArrayList<Song>> hashMap = new HashMap<>();
        if (MusicPlayerHelper.allSongsList == null)
            MusicPlayerHelper.getInstance().getSongList(getActivity());
        for (Song song : MusicPlayerHelper.allSongsList){
            if (hashMap.get(song.getSongArtist()) == null) {
                ArrayList<Song> songsForArtist = new ArrayList<>();
                songsForArtist.add(song);
                hashMap.put(song.getSongArtist(), songsForArtist);
            }
            else {
                ArrayList<Song> song1 = new ArrayList<>(hashMap.get(song.getSongArtist()));
                song1.add(song);
                hashMap.put(song.getSongArtist(), song1);
            }
        }
        mAdapter = new ArtistAdapter(hashMap, getActivity());
        artistList.setAdapter(mAdapter);
    }
}
