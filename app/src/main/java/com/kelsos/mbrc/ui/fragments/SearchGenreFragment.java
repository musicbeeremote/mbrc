package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.data.Queue;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.GenreSearchResults;
import com.kelsos.mbrc.utilities.ScrollListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.RoboGuice;

public class SearchGenreFragment extends Fragment implements GenreEntryAdapter.MenuItemSelectedListener {
  @Inject Bus bus;
  @BindView(R.id.search_recycler_view) RecyclerView recycler;
  @BindView(R.id.empty_view) LinearLayout emptyView;
  @Inject private ScrollListener scrollListener;
  private String mDefault;
  @Inject private GenreEntryAdapter adapter;

  @Subscribe public void handleSearchDefaultAction(SearchDefaultAction action) {
    mDefault = action.getAction();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.ui_fragment_library_search, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(getContext()).injectMembers(this);
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
    recycler.addOnScrollListener(scrollListener);
  }

  @Override public void onPause() {
    super.onPause();
    bus.unregister(this);
    recycler.removeOnScrollListener(scrollListener);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    recycler.setLayoutManager(layoutManager);
    recycler.setHasFixedSize(true);
    adapter.setMenuItemSelectedListener(this);
    recycler.setAdapter(adapter);
    displayProperView(false);
  }

  @Subscribe public void handleGenreSearchResults(GenreSearchResults results) {
    displayProperView(results.getList().isEmpty());
    adapter.update(results.getList());
  }

  @Override public void onMenuItemSelected(MenuItem menuItem, Genre entry) {
    final String qContext = Protocol.LibraryQueueGenre;
    final String gSub = Protocol.LibraryGenreArtists;
    String query = entry.getGenre();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_genre_queue_next:
        ua = new UserAction(qContext, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_genre_queue_last:
        ua = new UserAction(qContext, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_genre_play:
        ua = new UserAction(qContext, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_genre_artists:
        ua = new UserAction(gSub, query);
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  @Override public void onItemClicked(Genre genre) {
    if (!mDefault.equals(Const.SUB)) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction,
          new UserAction(Protocol.LibraryQueueGenre, new Queue(mDefault, genre.getGenre()))));
    } else {
      bus.post(new MessageEvent(ProtocolEventType.UserAction,
          new UserAction(Protocol.LibraryGenreArtists, genre.getGenre())));
    }
  }

  public void displayProperView(boolean noData) {
    if (noData) {
      emptyView.setVisibility(View.VISIBLE);
      recycler.setVisibility(View.GONE);
    } else {
      emptyView.setVisibility(View.GONE);
      recycler.setVisibility(View.VISIBLE);
    }
  }
}
