package com.kelsos.mbrc.ui.fragments.profile;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ArtistCursorAdapter;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.ui.activities.Profile;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.ui.fragments.browse.BrowseMenuItems;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenreArtistsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenreArtistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenreArtistsFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        PlaylistDialogFragment.onPlaylistSelectedListener,
        CreateNewPlaylistDialog.onPlaylistNameSelectedListener {
    private static final String GENRE_ID = "genreId";
    private static final int GROUP_ID = 2983;
    private static final int URL_LOADER = 0x15;
    private ArtistCursorAdapter mAdapter;
    private Artist artist;
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
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = mi != null ? mi.position : 0;
            artist = new Artist((Cursor) mAdapter.getItem(position));

            switch (item.getItemId()) {
                case BrowseMenuItems.GET_SUB:
                    showAlbums(artist);
                    break;
                case BrowseMenuItems.PLAYLIST:
                    final PlaylistDialogFragment dlFragment = new PlaylistDialogFragment();
                    dlFragment.setOnPlaylistSelectedListener(this);
                    dlFragment.show(getFragmentManager(), "playlist");
                    break;
                default:
                    break;

            }

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_NEXT, 0, R.string.search_context_queue_next);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_LAST, 0, R.string.search_context_queue_last);
        menu.add(GROUP_ID, BrowseMenuItems.PLAY_NOW, 0, R.string.search_context_play_now);
        menu.add(GROUP_ID, BrowseMenuItems.GET_SUB, 0, R.string.search_context_get_albums);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        return inflater.inflate(R.layout.fragment_genre_artists, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;
        baseUri = Uri.withAppendedPath(Artist.CONTENT_GENRE_URI, Uri.encode(String.valueOf(genreId)));
        return new CursorLoader(getActivity(), baseUri,
                new String[]{Artist.ARTIST_NAME}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter = new ArtistCursorAdapter(getActivity(), cursor, 0);
        this.setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Artist artist = new Artist((Cursor) mAdapter.getItem(position));
        showAlbums(artist);
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

    private void showAlbums(final Artist artist) {
        Intent intent = new Intent(getActivity(), Profile.class);
        intent.putExtra("name", artist.getArtistName());
        intent.putExtra("id", artist.getId());
        intent.putExtra("type", "artist");
        startActivity(intent);
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
