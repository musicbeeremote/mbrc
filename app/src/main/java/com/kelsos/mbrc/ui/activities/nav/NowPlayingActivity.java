package com.kelsos.mbrc.ui.activities.nav;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.NowPlayingAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.NowPlaying;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.domain.TrackInfo;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.rx.RxUtils;
import com.kelsos.mbrc.services.NowPlayingSync;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.ui.drag.SimpleItenTouchHelper;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;

public class NowPlayingActivity extends BaseActivity
    implements SearchView.OnQueryTextListener, NowPlayingAdapter.NowPlayingListener {

  @BindView(R.id.now_playing_list) RecyclerView nowPlayingList;
  @BindView(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;
  @Inject RxBus bus;
  @Inject NowPlayingAdapter adapter;
  @Inject NowPlayingSync sync;
  private SearchView mSearchView;
  private MenuItem mSearchItem;
  private Scope scope;

  private void handlePlayingTrackChange(TrackInfo event) {
    if (adapter == null || !adapter.getClass().equals(NowPlayingAdapter.class)) {
      return;
    }
    adapter.setPlayingTrackIndex(new NowPlaying(event.getArtist(), event.getTitle()));
    adapter.notifyDataSetChanged();
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    //inflater.inflate(R.menu.menu_now_playing, menu);
    //mSearchItem = menu.findItem(R.id.now_playing_search_item);
    //mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
    //mSearchView.setQueryHint(getString(R.string.now_playing_search_hint));
    //mSearchView.setIconifiedByDefault(true);
    //mSearchView.setOnQueryTextListener(this);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    scope = Toothpick.openScopes(getApplication(), this);
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
    setContentView(R.layout.activity_nowplaying);
    ButterKnife.bind(this);
    super.setup();
    RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
    nowPlayingList.setLayoutManager(manager);
    nowPlayingList.setAdapter(adapter);
    nowPlayingList.getItemAnimator().setChangeDuration(0);
    ItemTouchHelper.Callback callback = new SimpleItenTouchHelper(adapter);
    ItemTouchHelper helper = new ItemTouchHelper(callback);
    helper.attachToRecyclerView(nowPlayingList);
    adapter.setListener(this);
    swipeRefreshLayout.setOnRefreshListener(this::refresh);
    refresh();
  }

  private void refresh() {
    if (!swipeRefreshLayout.isRefreshing()) {
      swipeRefreshLayout.setRefreshing(true);
    }

    sync.syncNowPlaying(Schedulers.io()).compose(RxUtils.uiTask()).subscribe(() -> {
      adapter.refresh();
      swipeRefreshLayout.setRefreshing(false);
    }, throwable -> {
      Timber.v(throwable, "Failed");
    });
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
    bus.register(this,
        TrackInfoChangeEvent.class,
        trackInfoChangeEvent -> handlePlayingTrackChange(trackInfoChangeEvent.getTrackInfo()),
        true);
  }

  @Override
  public void onPause() {
    super.onPause();
    bus.unregister(this);
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

  @Override
  public void onPress(int position) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingListPlay, position + 1)));
  }

  @Override
  public void onMove(int from, int to) {
    adapter.setPlayingTrackIndex(calculateNewIndex(from, to, adapter.getPlayingTrackIndex()));

    Map<String, Integer> move = new HashMap<>();
    move.put("from", from);
    move.put("to", to);
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingListMove, move)));
  }

  @Override
  public void onDismiss(int position) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingListRemove, position)));
  }

  @Override
  protected int active() {
    return R.id.nav_now_playing;
  }

  @Override
  protected void onDestroy() {
    Toothpick.closeScope(this);
    super.onDestroy();
  }
}
