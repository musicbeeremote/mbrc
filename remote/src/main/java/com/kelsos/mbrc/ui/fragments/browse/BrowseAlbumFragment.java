package com.kelsos.mbrc.ui.fragments.browse;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumCursorAdapter;
import com.kelsos.mbrc.data.dbdata.LibraryAlbum;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;

public class BrowseAlbumFragment extends BaseFragment implements LoaderCallbacks<Cursor>,
        GridView.OnItemClickListener,
        PlaylistDialogFragment.onPlaylistSelectedListener,
        CreateNewPlaylistDialog.onPlaylistNameSelectedListener {
    private static final int GROUP_ID = 13;
    private static final int URL_LOADER = 2;
    private AlbumCursorAdapter mAdapter;
    private GridView mGrid;
    private LibraryAlbum album;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(mGrid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.ui_library_grid, container, false);
        if (view != null) {
            mGrid = (GridView) view.findViewById(R.id.mbrc_grid_view);
            mGrid.setOnItemClickListener(this);
        }
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_NEXT, 0, R.string.search_context_queue_next);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_LAST, 0, R.string.search_context_queue_last);
        menu.add(GROUP_ID, BrowseMenuItems.PLAY_NOW, 0, R.string.search_context_play_now);
        menu.add(GROUP_ID, BrowseMenuItems.GET_SUB, 0, R.string.search_context_get_tracks);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            return true;
        }

        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new AlbumCursorAdapter(getActivity(), data, 0);
        mGrid.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        
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


    @Override
    public void onPlaylistNameSelected(String name) {

    }
}
