package com.kelsos.mbrc.ui.fragments.browse;

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
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackCursorAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.data.dbdata.Track;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;

import java.util.HashMap;
import java.util.Map;

public class BrowseTrackFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, PlaylistDialogFragment.onPlaylistSelectedListener {
    private static final int GROUP_ID = 14;
    private static final int URL_LOADER = 0x53;
    private TrackCursorAdapter mAdapter;
    private Track track;

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
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = mi != null ? mi.position : 0;
            track = new Track((Cursor) mAdapter.getItem(position));

            switch (item.getItemId()) {
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
        }

        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        baseUri = Track.getContentUri();
        return new CursorLoader(getActivity(), baseUri,
                new String[]{Track.TITLE, Artist.ARTIST_NAME}, null, null, null);
    }

    private void QueueTrack(String position) {
        final Map<String, String> message = getMapBase();
        message.put("type", "queue");
        message.put("position", position);
        getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION,
                new UserAction(Protocol.NOW_PLAYING, message)));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new TrackCursorAdapter(getActivity(), data, 0);
        this.setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onPlaylistSelected(String hash) {

    }

    @Override
    public void onNewPlaylistSelected() {

    }

    private Map<String, String> getMapBase() {
        Map<String, String> message = new HashMap<>();
        message.put("selection", "track");
        message.put("data", track.getHash());
        return message;
    }

}
