package com.kelsos.mbrc.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.dbdata.Playlist;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.AvailablePlaylists;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.activities.PlaylistActivity;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class PlaylistFragment extends BaseListFragment {
    private static final int GROUP_ID = 1;
    private static final int PLAY_NOW = 1;
    private static final int GET_PLAYLIST = 2;
    private PlaylistAdapter adapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_playlist, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<>();
        map.put("type",Protocol.PLAYLISTS_GET);
        getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.PLAYLISTS, map)));
    }

    @Subscribe public void handlePlaylistsAvailable(AvailablePlaylists playlists) {
        adapter = new PlaylistAdapter(getActivity(), R.layout.ui_list_dual, playlists.getList());
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Playlist list = adapter.getItem(position);
        openPlaylist(list);
    }

    private void openPlaylist(final Playlist list) {
        Intent intent = new Intent(this.getActivity(), PlaylistActivity.class);
        intent.putExtra("name", list.getName());
        intent.putExtra("tracks", list.getTracks());
        intent.putExtra("hash", list.getHash());
        startActivity(intent);
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, GET_PLAYLIST, 0, R.string.playlist_list_get);
        menu.add(GROUP_ID, PLAY_NOW, 0, R.string.playlist_list_play_now);
    }

    @Override public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Playlist line = adapter.getItem(mi.position);
            String query = line.getHash();

            UserAction ua = null;
            switch (item.getItemId()) {
                case GET_PLAYLIST:
                    ua = new UserAction(Protocol.PLAYLIST_GET_FILES, query);
                    openPlaylist(line);
                    break;
                case PLAY_NOW:
                    ua = new UserAction(Protocol.PLAYLIST_PLAY_NOW, query);
                    break;
            }

            getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, ua));
            return true;
        } else {
            return false;
        }
    }

}