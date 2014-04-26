package com.kelsos.mbrc.ui.fragments.browse;

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
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.activities.Profile;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;

import java.util.HashMap;
import java.util.Map;

public class BrowseArtistFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        PlaylistDialogFragment.onPlaylistSelectedListener,
        CreateNewPlaylistDialog.onPlaylistNameSelectedListener {
    private static final int GROUP_ID = 12;
    private static final int URL_LOADER = 0x12;
    private ArtistCursorAdapter mAdapter;
    private Artist artist;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        return inflater.inflate(R.layout.fragment_library, container, false);
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
        final Map<String, String> message = getMapBase();
        message.put("type", "queue");
        message.put("position", position);
        getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION,
                new UserAction(Protocol.NOW_PLAYING, message)));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        baseUri = Artist.getContentUri();
        return new CursorLoader(getActivity(), baseUri,
                new String[]{Artist.ARTIST_NAME, Artist._ID}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new ArtistCursorAdapter(getActivity(), data, 0);
        this.setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Artist artist = new Artist((Cursor) mAdapter.getItem(position));
        showAlbums(artist);
    }

    private void showAlbums(final Artist artist) {
        Intent intent = new Intent(getActivity(), Profile.class);
        intent.putExtra("name", artist.getArtistName());
        intent.putExtra("id", artist.getId());
        intent.putExtra("type", "artist");
        startActivity(intent);
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
        message.put("selection", "artist");
        message.put("data", artist.getArtistName());
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
