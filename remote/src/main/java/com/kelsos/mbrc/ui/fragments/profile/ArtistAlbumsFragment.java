package com.kelsos.mbrc.ui.fragments.profile;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumCursorAdapter;
import com.kelsos.mbrc.data.dbdata.Album;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArtistAlbumsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArtistAlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ArtistAlbumsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        GridView.OnItemClickListener,
        PlaylistDialogFragment.onPlaylistSelectedListener,
        CreateNewPlaylistDialog.onPlaylistNameSelectedListener {

    private AlbumCursorAdapter mAdapter;
    private GridView mGrid;
    private Album album;

    private static final String ARTIST_ID = "artistId";
    private static final int URL_LOADER = 0x832d;

    private long artistId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param artistId Parameter 1.
     * @return A new instance of fragment ArtistAlbumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtistAlbumsFragment newInstance(long artistId) {
        ArtistAlbumsFragment fragment = new ArtistAlbumsFragment();
        Bundle args = new Bundle();
        args.putLong(ARTIST_ID, artistId);
        fragment.setArguments(args);
        return fragment;
    }
    public ArtistAlbumsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(mGrid);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistId = getArguments().getLong(ARTIST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        final View view = inflater.inflate(R.layout.ui_library_grid, container, false);
        if (view != null) {
            mGrid = (GridView) view.findViewById(R.id.mbrc_grid_view);
            mGrid.setOnItemClickListener(this);
        }
        return view;
    }

    @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;
        baseUri = Uri.withAppendedPath(Album.CONTENT_ARTIST_URI, Uri.encode(String.valueOf(artistId)));
        return new CursorLoader(getActivity(), baseUri,
                new String[] {Album.ALBUM_NAME, Artist.ARTIST_NAME}, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter = new AlbumCursorAdapter(getActivity(), cursor, 0);
        mGrid.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) { }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        album = new Album((Cursor) mAdapter.getItem(position));
        if (BuildConfig.DEBUG) {
            Log.d("al", String.valueOf(album.getId()));
        }
    }

    @Override
    public void onPlaylistNameSelected(String name) {

    }

    @Override
    public void onPlaylistSelected(String hash) {

    }

    @Override
    public void onNewPlaylistSelected() {

    }
}
