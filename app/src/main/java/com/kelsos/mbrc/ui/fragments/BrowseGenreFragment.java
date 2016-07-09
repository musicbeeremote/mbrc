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
import com.kelsos.mbrc.adapters.GenreEntryAdapter;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.helper.PopupActionHandler;
import com.kelsos.mbrc.services.BrowseSync;
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView;
import roboguice.RoboGuice;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class BrowseGenreFragment extends Fragment
    implements GenreEntryAdapter.MenuItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
  @Inject
  private RxBus bus;
  @BindView(R.id.search_recycler_view)
  EmptyRecyclerView recycler;
  @BindView(R.id.empty_view)
  LinearLayout emptyView;
  @BindView(R.id.swipe_layout)
  SwipeRefreshLayout swipeLayout;

  @Inject
  private GenreEntryAdapter adapter;

  @Inject
  private PopupActionHandler actionHandler;

  @Inject
  private BrowseSync sync;
  private Subscription subscription;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_library_search, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(getContext()).injectMembers(this);
  }

  @Override public void onStart() {
    super.onStart();
    adapter.init();
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    swipeLayout.setOnRefreshListener(this);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    recycler.setLayoutManager(layoutManager);
    recycler.setHasFixedSize(true);
    adapter.setMenuItemSelectedListener(this);
    recycler.setAdapter(adapter);
    recycler.setEmptyView(emptyView);
  }

  @Override public void onMenuItemSelected(MenuItem menuItem, Genre entry) {
    actionHandler.genreSelected(menuItem, entry);
  }

  @Override public void onItemClicked(Genre genre) {
    actionHandler.genreSelected(genre);
  }

  @Override public void onRefresh() {
    if (!swipeLayout.isRefreshing()) {
      swipeLayout.setRefreshing(true);
    }

    if (subscription != null && !subscription.isUnsubscribed()) {
      return;
    }

    subscription = sync.syncGenres(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnTerminate(() -> swipeLayout.setRefreshing(false))
        .subscribe(throwable -> {
          bus.post(new NotifyUser(R.string.refresh_failed));
          Timber.v(throwable, "Refresh failed");
        }, () -> {
          adapter.refresh();
        });
  }
}
