package com.kelsos.mbrc.ui.fragments.profile;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ArtistCursorAdapter;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import roboguice.fragment.provided.RoboListFragment;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenreArtistsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenreArtistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenreArtistsFragment extends RoboListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
		PlaylistDialogFragment.OnPlaylistSelectedListener,
		CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {
    private static final String GENRE_ID = "genreId";
    private static final int GROUP_ID = 2983;
    private static final int URL_LOADER = 0x15;
    private ArtistCursorAdapter mAdapter;
    private long genreId;

    public GenreArtistsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param genreId The id of the genre.
     * @return A new instance of fragment GenreArtistsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenreArtistsFragment newInstance(long genreId) {
        GenreArtistsFragment fragment = new GenreArtistsFragment();
        Bundle args = new Bundle();
        args.putLong(GENRE_ID, genreId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genreId = getArguments().getLong(GENRE_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getLoaderManager().initLoader(URL_LOADER, null, this);
        return inflater.inflate(R.layout.fragment_genre_artists, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        this.setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
