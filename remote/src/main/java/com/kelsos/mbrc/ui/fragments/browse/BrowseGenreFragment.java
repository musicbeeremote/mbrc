package com.kelsos.mbrc.ui.fragments.browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.EndlessRecyclerViewScrollListener;
import com.kelsos.mbrc.adapters.GenreAdapter;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Genre;
import com.kelsos.mbrc.presenters.BrowseGenrePresenter;
import com.kelsos.mbrc.ui.activities.ProfileActivity;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.ui.views.BrowseGenreView;
import java.util.List;
import roboguice.RoboGuice;

public class BrowseGenreFragment extends Fragment implements PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener, BrowseGenreView, GenreAdapter.MenuItemSelectedListener {

  @Bind(R.id.library_recycler) RecyclerView recyclerView;
  @Inject private GenreAdapter adapter;
  @Inject private BrowseGenrePresenter presenter;
  private EndlessRecyclerViewScrollListener scrollListener;

  @NonNull public static BrowseGenreFragment newInstance() {
    return new BrowseGenreFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(getContext()).injectMembers(this);
    setHasOptionsMenu(true);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_now_playing, menu);
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_library, container, false);
    ButterKnife.bind(this, view);
    presenter.bind(this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);
    scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
      @Override public void onLoadMore(int page, int totalItemsCount) {
        presenter.load(page);
      }
    };
    adapter.setMenuItemSelectedListener(this);
    presenter.load();
    return view;
  }

  @Override public void onPlaylistSelected(String hash) {
  }

  @Override public void onNewPlaylistSelected() {
    final CreateNewPlaylistDialog npDialog = new CreateNewPlaylistDialog();
    npDialog.setOnPlaylistNameSelectedListener(this);
    npDialog.show(getActivity().getSupportFragmentManager(), "npDialog");
  }

  @Override public void onPlaylistNameSelected(String name) {

  }

  @Override public void update(List<Genre> data) {
    adapter.updateData(data);
  }

  @Override public void showEnqueueFailure() {
    Snackbar.make(recyclerView, R.string.genre_enqueue_failed, Snackbar.LENGTH_SHORT).show();
  }

  @Override public void showEnqueueSuccess() {
    Snackbar.make(recyclerView, R.string.genre_queued, Snackbar.LENGTH_SHORT).show();
  }

  @Override public void clear() {
    adapter.clear();
  }

  @Override public void onMenuItemSelected(MenuItem item, Genre genre) {
    switch (item.getItemId()) {
      case R.id.popup_genre_play:
        presenter.queue(genre, Queue.NOW);
        break;
      case R.id.popup_genre_queue_last:
        presenter.queue(genre, Queue.LAST);
        break;
      case R.id.popup_genre_queue_next:
        presenter.queue(genre, Queue.NEXT);
        break;
      case R.id.popup_genre_playlist:
        break;
      case R.id.popup_genre_artists:
        openProfile(genre);
        break;
      default:
        break;
    }
  }

  private void openProfile(Genre genre) {
    Intent intent = new Intent(getActivity(), ProfileActivity.class);
    intent.putExtra(ProfileActivity.TYPE, ProfileActivity.GENRE);
    intent.putExtra(ProfileActivity.ID, genre.getId());
    intent.putExtra(ProfileActivity.NAME, genre.getName());
    startActivity(intent);
  }

  @Override public void onItemClicked(Genre genre) {
    openProfile(genre);
  }

  @Override public void onResume() {
    super.onResume();
    recyclerView.addOnScrollListener(scrollListener);
  }

  @Override public void onPause() {
    super.onPause();
    recyclerView.addOnScrollListener(scrollListener);
  }
}
