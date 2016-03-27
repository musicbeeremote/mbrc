package com.kelsos.mbrc.ui.fragments.browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumAdapter;
import com.kelsos.mbrc.adapters.EndlessGridRecyclerViewScrollListener;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.presenters.BrowseAlbumPresenter;
import com.kelsos.mbrc.ui.fragments.profile.AlbumTracksActivity;
import com.kelsos.mbrc.ui.views.BrowseAlbumView;
import java.util.List;
import roboguice.RoboGuice;

public class BrowseAlbumFragment extends Fragment
    implements AlbumAdapter.MenuItemSelectedListener, BrowseAlbumView {

  @Bind(R.id.album_recycler) RecyclerView recyclerView;
  @Inject private AlbumAdapter adapter;
  @Inject private BrowseAlbumPresenter presenter;
  private EndlessGridRecyclerViewScrollListener scrollListener;

  @NonNull public static BrowseAlbumFragment newInstance() {
    return new BrowseAlbumFragment();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_library_grid, container, false);
    ButterKnife.bind(this, view);
    RoboGuice.getInjector(getContext()).injectMembers(this);
    presenter.bind(this);
    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
    scrollListener = new EndlessGridRecyclerViewScrollListener(layoutManager) {
      @Override public void onLoadMore(int page, int totalItemsCount) {
        presenter.load(page);
      }
    };
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);
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

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onMenuItemSelected(MenuItem item, Album album) {
    switch (item.getItemId()) {
      case R.id.popup_album_tracks:
        openProfile(album);
        break;
      case R.id.popup_album_play:
        presenter.queue(album, Queue.NOW);
        break;
      case R.id.popup_album_queue_last:
        presenter.queue(album, Queue.LAST);
        break;
      case R.id.popup_album_queue_next:
        presenter.queue(album, Queue.NEXT);
        break;
      case R.id.popup_album_playlist:
        break;
      default:
        break;
    }
  }

  @Override public void onItemClicked(Album album) {
    openProfile(album);
  }

  private void openProfile(Album album) {
    Intent intent = new Intent(getContext(), AlbumTracksActivity.class);
    Bundle bundle = new Bundle();
    bundle.putLong(AlbumTracksActivity.ALBUM_ID, album.getId());
    intent.putExtras(bundle);
    startActivity(intent);
  }

  @Override public void updateData(List<Album> data) {
    adapter.updateData(data);
  }

  @Override public void clearData() {
    adapter.clearData();
  }
}
