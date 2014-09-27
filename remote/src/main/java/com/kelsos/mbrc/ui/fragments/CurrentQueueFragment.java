package com.kelsos.mbrc.ui.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.ListView;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.NowPlayingAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.data.dbdata.QueueTrack;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.events.ui.TrackMoved;
import com.kelsos.mbrc.events.ui.TrackRemoval;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;


public class CurrentQueueFragment extends BaseListFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    @Inject private Injector injector;
    private NowPlayingAdapter adapter;
    private QueueTrack mTrack;

    private DragSortListView mDslv;
    private DragSortController mController;

    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = true;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;

    public DragSortController buildController(DragSortListView dslv) {
        // defaults are
        // dragStartMode = onDown
        // removeMode = flingRight
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setFlingHandleId(R.id.click_remove);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        return controller;
    }

    @Subscribe public void handlePlayingTrackChange(TrackInfoChange event) {
        if (adapter == null || !adapter.getClass().equals(NowPlayingAdapter.class)) {
            return;
        }
        //adapter.setPlayingTrackIndex(adapter.getPosition(new NowPlayingTrack(event.getArtist(), event.getTitle())));
        adapter.notifyDataSetChanged();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        inflater.inflate(R.menu.menu_now_playing, menu);

    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDslv = (DragSortListView) getListView();
        mDslv.setDropListener(onDrop);
        mDslv.setRemoveListener(onRemove);
        registerForContextMenu(getListView());
        injector.injectMembers(getListView());
    }

    @Override public void onStart() {
        super.onStart();
        Map<String, String> message = new HashMap<>();
        message.put("type", "list");
        getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.NOW_PLAYING, message)));
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.ui_fragment_nowplaying, container, false);
        mDslv = (DragSortListView) mView.findViewById(android.R.id.list);
        mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(dragEnabled);
        return mView;
    }

    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //adapter.setPlayingTrackIndex(position);
        adapter.notifyDataSetChanged();
        getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.NOW_PLAYING_PLAY, position + 1)));
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override public void drop(int from, int to) {
            if (from != to) {
               // mTrack = adapter.getItem(from);
                //adapter.remove(mTrack);
               // adapter.insert(mTrack, to);
                adapter.notifyDataSetChanged();

                //adapter.setPlayingTrackIndex(calculateNewIndex(from, to, adapter.getPlayingTrackIndex()));

                Map<String, Object> move = new HashMap<>();
                move.put("type", "move");
                move.put("from", from);
                move.put("to", to);
                getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.NOW_PLAYING, move)));
            }
        }
    };

    private int calculateNewIndex(int from, int to, int index) {
        int dist = Math.abs(from - to);
        int rIndex = index;
        if (dist == 1 && index == from
                || dist > 1 && from > to && index == from
                || dist > 1 && from < to && index == from) {
            rIndex = to;
        } else if (dist == 1 && index == to) {
            rIndex = from;
        } else if (dist > 1 && from > to && index == to
                || from > index && to < index) {
            rIndex += 1;
        } else if (dist > 1 && from < to && index == to
                || from < index && to > index) {
            rIndex -= 1;
        }
        return rIndex;
    }

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override public void remove(int which) {
            //mTrack = adapter.getItem(which);
            //adapter.remove(mTrack);
            adapter.notifyDataSetChanged();
            Map<String, Object> remove = new HashMap<>();
            remove.put("type","remove");
            remove.put("index", which);
            getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.NOW_PLAYING, remove)));
        }
    };

    @Subscribe public void handleTrackMoved(TrackMoved event) {
        // In case the action failed revert the change
        if (!event.isSuccess()) {
            //mTrack = adapter.getItem(event.getTo());
            //adapter.remove(mTrack);
           // adapter.insert(mTrack, event.getFrom());
        }
    }

    @Subscribe public void handleTrackRemoval(TrackRemoval event) {
        // In case the action failed revert the change
        if (!event.isSuccess()) {
            //adapter.insert(mTrack, event.getIndex());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;
        baseUri = QueueTrack.getContentUri();
        return new CursorLoader(getActivity(), baseUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.setNotificationUri(getActivity().getContentResolver(), QueueTrack.getContentUri());
        adapter = new NowPlayingAdapter(getActivity(), cursor, 0);
        this.setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }
}
