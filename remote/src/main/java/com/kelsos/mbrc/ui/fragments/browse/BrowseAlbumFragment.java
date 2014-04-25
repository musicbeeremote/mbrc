package com.kelsos.mbrc.ui.fragments.browse;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumCursorAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.data.dbdata.Album;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.activities.Profile;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;

import java.util.HashMap;
import java.util.Map;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;

public class BrowseAlbumFragment extends BaseFragment implements LoaderCallbacks<Cursor>,
        GridView.OnItemClickListener,
        PlaylistDialogFragment.onPlaylistSelectedListener,
        CreateNewPlaylistDialog.onPlaylistNameSelectedListener {
    private static final int GROUP_ID = 13;
    private static final int URL_LOADER = 2;
    private AlbumCursorAdapter mAdapter;
    private GridView mGrid;
    private Album album;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(mGrid);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        final View view = inflater.inflate(R.layout.ui_library_grid, container, false);
        if (view != null) {
            mGrid = (GridView) view.findViewById(R.id.mbrc_grid_view);
            mGrid.setOnItemClickListener(this);
        }
        return view;
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
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
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = mi != null ? mi.position : 0;
            album = new Album((Cursor) mAdapter.getItem(position));
            switch (item.getItemId()) {
                case BrowseMenuItems.GET_SUB:
                    showTracks(album);
                    break;
                case BrowseMenuItems.PLAYLIST:
                    final PlaylistDialogFragment dlFragment = new PlaylistDialogFragment();
                    dlFragment.setOnPlaylistSelectedListener(this);
                    dlFragment.show(getFragmentManager(), "playlist");
                case BrowseMenuItems.PLAY_NOW:
                    break;
                case BrowseMenuItems.QUEUE_LAST:
                    break;
                case BrowseMenuItems.QUEUE_NEXT:
                    break;
                default:
                    break;
            }
            return true;
        }

        return false;
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        baseUri = Album.getContentUri();
        return new CursorLoader(getActivity(), baseUri,
                new String[] {Album.ALBUM_NAME, Artist.ARTIST_NAME}, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new AlbumCursorAdapter(getActivity(), data, 0);
        mGrid.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) { }

    private void showTracks(final Album album) {
        Intent intent = new Intent(getActivity(), Profile.class);
        intent.putExtra("name", album.getAlbumName());
        intent.putExtra("id", album.getId());
        intent.putExtra("type", "album");
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Album album = new Album((Cursor) mAdapter.getItem(position));
        showTracks(album);
    }

    @Override
    public void onPlaylistSelected(String hash) {
        Map<String, String> message = getMapBase();
        message.put("type", "add");
        message.put("hash", hash);
        getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION,
                new UserAction(Protocol.PLAYLISTS, message)));
    }

    @Override
    public void onNewPlaylistSelected() {
        final CreateNewPlaylistDialog npDialog = new CreateNewPlaylistDialog();
        npDialog.setOnPlaylistNameSelectedListener(this);
        npDialog.show(getFragmentManager(), "npDialog");
    }

    private Map<String, String> getMapBase() {
        Map<String, String> message = new HashMap<>();
        message.put("selection", "album");
        message.put("data", album.getAlbumName());
        return message;
    }


    @Override
    public void onPlaylistNameSelected(String name) {
        Map<String, String> message = getMapBase();
        message.put("type", "create");
        message.put("name", name);
        getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION,
                new UserAction(Protocol.PLAYLISTS, message)));
    }
}
