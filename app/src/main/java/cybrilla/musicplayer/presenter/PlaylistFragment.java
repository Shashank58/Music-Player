package cybrilla.musicplayer.presenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.database.PlaylistDBHelper;
import cybrilla.musicplayer.modle.Playlist;

/**
 * Contains a list of all play lists added by user.
 */

public class PlaylistFragment extends Fragment {
    private PlaylistDBHelper playlistDBHelper;
    private RecyclerView playlist;
    private List<Playlist> playlistSongs;
    private ImageView addPlaylist;

    public PlaylistFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (playlistDBHelper == null){
            playlistDBHelper = new PlaylistDBHelper(getActivity());
        }
        addPlaylist = (ImageView) getActivity().findViewById(R.id.menuItem);
        addPlaylist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.playlist_dialog, null);
                final EditText playlistName = (EditText) dialogView.findViewById
                            (R.id.playlist_name_edittext);
                builder.setView(dialogView)
                        .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!"".equals(playlistName.getText().toString())){

                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_fragment, container, false);
        if (playlistDBHelper.getCount() == 0){
            view.findViewById(R.id.no_playlist_text).setVisibility(View.VISIBLE);
        } else {
            playlistSongs = new ArrayList<>(playlistDBHelper.getAllPlaylistSongs());
        }
        playlist = (RecyclerView) view.findViewById(R.id.playlist);
        playlist.setHasFixedSize(true);
        playlist.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}
