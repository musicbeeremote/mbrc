package com.kelsos.mbrc.ui.fragments.browse;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.GenreCursorAdapter;
import com.kelsos.mbrc.data.dbdata.LibraryGenre;
import com.kelsos.mbrc.ui.activities.Profile;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;

import java.util.HashMap;
import java.util.Map;

public class BrowseGenreFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        PlaylistDialogFragment.onPlaylistSelectedListener,
        CreateNewPlaylistDialog.onPlaylistNameSelectedListener {
    private static final int GROUP_ID = 11;
    private static final int URL_LOADER = 1;
    private GenreCursorAdapter mAdapter;
    private LibraryGenre genre;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_NEXT, 0, R.string.search_context_queue_next);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_LAST, 0, R.string.search_context_queue_last);
        menu.add(GROUP_ID, BrowseMenuItems.PLAY_NOW, 0, R.string.search_context_play_now);
        menu.add(GROUP_ID, BrowseMenuItems.GET_SUB, 0, R.string.search_context_get_artists);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = mi != null ? mi.position : 0;
            final Cursor line = (Cursor) mAdapter.getItem(position);
            genre = new LibraryGenre(line);

            switch (item.getItemId()) {
                case BrowseMenuItems.GET_SUB:
                    ShowArtists(genre);
                    break;
                case BrowseMenuItems.PLAYLIST:
                    final PlaylistDialogFragment dlFragment = new PlaylistDialogFragment();
                    dlFragment.setOnPlaylistSelectedListener(this);
                    dlFragment.show(getFragmentManager(), "playlist");
                case BrowseMenuItems.PLAY_NOW:
                    QueueTrack("now");
                    break;
                case BrowseMenuItems.QUEUE_LAST:
                    QueueTrack("last");
                    break;
                case BrowseMenuItems.QUEUE_NEXT:
                    QueueTrack("next");
                    break;
                default:
                    break;
            }
            return true;
        } else {
            return false;
        }

    }

    private void QueueTrack(String position) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Uri baseUri = LibraryGenre.CONTENT_URI;
        return new CursorLoader(getActivity(), baseUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new GenreCursorAdapter(getActivity(), data, 0);
        setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_now_playing, menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final LibraryGenre genre = new LibraryGenre((Cursor) mAdapter.getItem(position));
        ShowArtists(genre);
    }

    private void ShowArtists(final LibraryGenre genre) {
        Intent intent = new Intent(getActivity(), Profile.class);
        intent.putExtra("name", genre.getGenreName());
        intent.putExtra("id", genre.getId());
        intent.putExtra("type", "genre");
        startActivity(intent);
    }

    @Override
    public void onPlaylistSelected(String hash) {
    }

    @Override
    public void onNewPlaylistSelected() {
        final CreateNewPlaylistDialog npDialog = new CreateNewPlaylistDialog();
        npDialog.setOnPlaylistNameSelectedListener(this);
        npDialog.show(getFragmentManager(), "npDialog");
    }

    private Map<String, String> getMapBase() {
        Map<String, String> message = new HashMap<>();
        message.put("selection", "genre");
        message.put("data", genre.getGenreName());
        return message;
    }


    @Override
    public void onPlaylistNameSelected(String name) {

    }
}
