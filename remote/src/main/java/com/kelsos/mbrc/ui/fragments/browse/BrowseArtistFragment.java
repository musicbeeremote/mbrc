package com.kelsos.mbrc.ui.fragments.browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ArtistAdapter;
import com.kelsos.mbrc.adapters.EndlessRecyclerViewScrollListener;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.presenters.BrowseArtistPresenter;
import com.kelsos.mbrc.ui.activities.ProfileActivity;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.ui.views.BrowseArtistView;
import java.util.List;
import roboguice.fragment.RoboFragment;

public class BrowseArtistFragment extends RoboFragment
    implements PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener, BrowseArtistView, ArtistAdapter.MenuItemSelectedListener {

  @Bind(R.id.library_recycler) RecyclerView recyclerView;
  @Inject private ArtistAdapter adapter;
  @Inject private BrowseArtistPresenter presenter;
  private LinearLayoutManager layoutManager;
  private EndlessRecyclerViewScrollListener scrollListener;

  @NonNull public static BrowseArtistFragment newInstance() {
    return new BrowseArtistFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_library, container, false);
    ButterKnife.bind(this, view);
    presenter.bind(this);
    layoutManager = new LinearLayoutManager(getContext());
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

  @Override public void onResume() {
    super.onResume();
    recyclerView.addOnScrollListener(scrollListener);
  }

  @Override public void onPause() {
    super.onPause();
    recyclerView.removeOnScrollListener(scrollListener);
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

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onMenuItemSelected(MenuItem item, Artist artist) {
    switch (item.getItemId()) {
      case R.id.popup_artist_queue_next:
        presenter.queue(artist, Queue.NEXT);
        break;
      case R.id.popup_artist_queue_last:
        presenter.queue(artist, Queue.LAST);
        break;
      case R.id.popup_artist_play:
        presenter.queue(artist, Queue.NOW);
        break;
      case R.id.popup_artist_album:
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.TYPE, ProfileActivity.ARTIST);
        intent.putExtra(ProfileActivity.ID, artist.getId());
        startActivity(intent);
        break;
      case R.id.popup_artist_playlist:
        break;
      default:
        break;
    }
  }

  @Override public void onItemClicked(Artist artist) {

  }

  @Override public void showEnqueueSuccess() {

  }

  @Override public void showEnqueueFailure() {

  }

  @Override public void load(List<Artist> artists) {
    adapter.updateData(artists);
  }

  @Override public void clear() {
    adapter.clear();
  }
}
