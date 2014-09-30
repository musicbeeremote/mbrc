package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.events.ui.TrackMoved;
import com.kelsos.mbrc.events.ui.TrackRemoval;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.squareup.otto.Subscribe;


public class CurrentQueueFragment extends BaseListFragment {
    @Inject private Injector injector;



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
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override public void drop(int from, int to) {
            if (from != to) {

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

        }
    };

    @Subscribe public void handleTrackMoved(TrackMoved event) {
        // In case the action failed revert the change
        if (!event.isSuccess()) {

        }
    }

    @Subscribe public void handleTrackRemoval(TrackRemoval event) {
        // In case the action failed revert the change
        if (!event.isSuccess()) {

        }
    }


}
