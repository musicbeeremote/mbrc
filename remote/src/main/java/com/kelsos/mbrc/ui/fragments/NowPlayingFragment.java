package com.kelsos.mbrc.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.NowPlayingAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.NowPlayingListAvailable;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.events.ui.TrackMoved;
import com.kelsos.mbrc.events.ui.TrackRemoval;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.HashMap;
import java.util.Map;
import roboguice.fragment.RoboFragment;

public class NowPlayingFragment extends RoboFragment
    implements SearchView.OnQueryTextListener, NowPlayingAdapter.OnUserActionListener {

  @Bind(R.id.now_playing_recycler) RecyclerView recyclerView;
  @Inject NowPlayingAdapter adapter;
  @Inject private Bus bus;
  private LinearLayoutManager layoutManager;
  private RecyclerView.Adapter wrappedAdapter;
  private SearchView mSearchView;
  private MenuItem mSearchItem;
  private MusicTrack mTrack;
  private RecyclerViewDragDropManager dragAndDropManager;
  private RecyclerViewTouchActionGuardManager touchActionGuardManager;
  private RecyclerViewSwipeManager swipeManager;

  @Subscribe public void handleNowPlayingListAvailable(NowPlayingListAvailable event) {
    adapter.setData(event.getList());
    adapter.setPlayingTrackIndex(event.getIndex());
  }

  @Subscribe public void handlePlayingTrackChange(TrackInfoChange event) {
    if (adapter == null || !adapter.getClass().equals(NowPlayingAdapter.class)) {
      return;
    }
    final MusicTrack track = new MusicTrack(event.getArtist(), event.getTitle());
    adapter.setPlayingTrack(track);
  }

  public boolean onQueryTextSubmit(String query) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction,
        new UserAction(Protocol.NowPlayingListSearch, query.trim())));
    mSearchView.setIconified(true);
    MenuItemCompat.collapseActionView(mSearchItem);
    return false;
  }

  public boolean onQueryTextChange(String newText) {
    return true;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_now_playing, menu);
    mSearchItem = menu.findItem(R.id.now_playing_search_item);
    mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
    mSearchView.setQueryHint(getString(R.string.now_playing_search_hint));
    mSearchView.setIconifiedByDefault(true);
    mSearchView.setOnQueryTextListener(this);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override public void onStart() {
    super.onStart();
    bus.register(this);
    bus.post(new MessageEvent(ProtocolEventType.UserAction,
        new UserAction(Protocol.NowPlayingList, true)));
  }

  @Override public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.ui_fragment_nowplaying, container, false);
    ButterKnife.bind(this, view);

    final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
    animator.setSupportsChangeAnimations(false);

    layoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(animator);

    touchActionGuardManager = new RecyclerViewTouchActionGuardManager();
    touchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
    touchActionGuardManager.setEnabled(true);

    // drag & drop manager
    dragAndDropManager = new RecyclerViewDragDropManager();

    @SuppressWarnings("deprecation")
    @SuppressLint({ "NewApi", "LocalSuppress" })
    final NinePatchDrawable drawable = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
        ? (NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z3)
        : (NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z3, null);

    dragAndDropManager.setDraggingItemShadowDrawable(drawable);
    swipeManager = new RecyclerViewSwipeManager();

    adapter.setOnUserActionListener(this);

    wrappedAdapter = dragAndDropManager.createWrappedAdapter(adapter);
    wrappedAdapter = swipeManager.createWrappedAdapter(wrappedAdapter);

    recyclerView.setAdapter(wrappedAdapter);

    touchActionGuardManager.attachRecyclerView(recyclerView);
    dragAndDropManager.attachRecyclerView(recyclerView);
    swipeManager.attachRecyclerView(recyclerView);

    return view;
  }

  private int calculateNewIndex(int from, int to, int index) {
    int dist = Math.abs(from - to);
    if (dist == 1 && index == from
        || dist > 1 && from > to && index == from
        || dist > 1 && from < to && index == from) {
      index = to;
    } else if (dist == 1 && index == to) {
      index = from;
    } else if (dist > 1 && from > to && index == to || from > index && to < index) {
      index += 1;
    } else if (dist > 1 && from < to && index == to || from < index && to > index) {
      index -= 1;
    }
    return index;
  }

  @Subscribe public void handleTrackMoved(TrackMoved event) {
    // In case the action failed revert the change
    if (!event.isSuccess()) {
      adapter.restorePositions(event.getFrom(), event.getTo());
    }
  }

  @Subscribe public void handleTrackRemoval(TrackRemoval event) {
    // In case the action failed revert the change
    if (!event.isSuccess()) {
      adapter.insert(mTrack, event.getIndex());
    }
  }

  @Override public void onTrackRemoved(int position) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction,
        new UserAction(Protocol.NowPlayingListRemove, position)));
  }

  @Override public void onTrackMoved(int from, int to) {
    adapter.setPlayingTrackIndex(calculateNewIndex(from, to, adapter.getPlayingTrackIndex()));

    Map<String, Integer> move = new HashMap<>();
    move.put("from", from);
    move.put("to", to);
    bus.post(new MessageEvent(ProtocolEventType.UserAction,
        new UserAction(Protocol.NowPlayingListMove, move)));
  }

  @Override public void onItemClicked(int position) {
    adapter.setPlayingTrackIndex(position);
    bus.post(new MessageEvent(ProtocolEventType.UserAction,
        new UserAction(Protocol.NowPlayingListPlay, position + 1)));
  }

  @Override public void onPause() {
    dragAndDropManager.cancelDrag();
    super.onPause();
  }

  @Override public void onDestroyView() {
    if (dragAndDropManager != null) {
      dragAndDropManager.release();
      dragAndDropManager = null;
    }

    if (swipeManager != null) {
      swipeManager.release();
      swipeManager = null;
    }

    if (touchActionGuardManager != null) {
      touchActionGuardManager.release();
      touchActionGuardManager = null;
    }

    if (recyclerView != null) {
      recyclerView.setItemAnimator(null);
      recyclerView.setAdapter(null);
      recyclerView = null;
    }

    if (wrappedAdapter != null) {
      WrapperAdapterUtils.releaseAll(wrappedAdapter);
      wrappedAdapter = null;
    }
    adapter = null;
    layoutManager = null;
    super.onDestroyView();
  }
}
