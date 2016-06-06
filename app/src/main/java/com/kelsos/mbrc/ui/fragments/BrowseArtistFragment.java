package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ArtistEntryAdapter;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.helper.PopupActionHandler;
import com.kelsos.mbrc.services.BrowseSync;
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView;
import com.squareup.otto.Bus;
import roboguice.RoboGuice;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class BrowseArtistFragment extends Fragment
    implements ArtistEntryAdapter.MenuItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
  @Inject
  Bus bus;
  @BindView(R.id.search_recycler_view)
  EmptyRecyclerView recycler;
  @BindView(R.id.empty_view)
  LinearLayout emptyView;
  @BindView(R.id.swipe_layout)
  SwipeRefreshLayout swipeLayout;
  @Inject
  private ArtistEntryAdapter adapter;

  @Inject
  private PopupActionHandler actionHandler;

  @Inject
  private BrowseSync sync;
  private Subscription subscription;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(getContext()).injectMembers(this);
    adapter.init(null);
  }

  @Override public void onStart() {
    super.onStart();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_library_search, container, false);
    ButterKnife.bind(this, view);
    swipeLayout.setOnRefreshListener(this);
    return view;
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override public void onPause() {
    super.onPause();
    bus.unregister(this);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    recycler.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    recycler.setLayoutManager(layoutManager);
    adapter.setMenuItemSelectedListener(this);
    recycler.setAdapter(adapter);
    recycler.setEmptyView(emptyView);
  }

  @Override public void onMenuItemSelected(MenuItem menuItem, Artist entry) {
    actionHandler.artistSelected(menuItem, entry);
  }

  @Override public void onItemClicked(Artist artist) {
    actionHandler.artistSelected(artist);
  }

  @Override public void onRefresh() {
    if(!swipeLayout.isRefreshing()) {
      swipeLayout.setRefreshing(true);
    }

    if (subscription != null && !subscription.isUnsubscribed()) {
      return;
    }

    subscription = sync.syncArtists(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnTerminate(() -> swipeLayout.setRefreshing(false))
        .subscribe(t -> {
          bus.post(new NotifyUser(R.string.refresh_failed));
          Timber.v(t, "Failed");
        }, () -> adapter.refresh());
  }
}
