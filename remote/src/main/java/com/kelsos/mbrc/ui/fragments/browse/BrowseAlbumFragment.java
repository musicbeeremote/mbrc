package com.kelsos.mbrc.ui.fragments.browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.presenters.BrowseAlbumPresenter;
import com.kelsos.mbrc.ui.activities.ProfileActivity;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.ui.views.BrowseAlbumView;
import java.util.List;
import roboguice.fragment.RoboFragment;

public class BrowseAlbumFragment extends RoboFragment implements PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener, AlbumAdapter.MenuItemSelectedListener, BrowseAlbumView {

  @Bind(R.id.album_recycler) RecyclerView recyclerView;
  @Inject private AlbumAdapter adapter;
  @Inject private BrowseAlbumPresenter presenter;

  @NonNull public static BrowseAlbumFragment newInstance() {
    return new BrowseAlbumFragment();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_library_grid, container, false);
    ButterKnife.bind(this, view);
    presenter.bind(this);
    RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 2);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
    adapter.setMenuItemSelectedListener(this);
    presenter.load();
    return view;
  }

  @Override public void onPlaylistSelected(String hash) {

  }

  @Override public void onNewPlaylistSelected() {
    final CreateNewPlaylistDialog npDialog = new CreateNewPlaylistDialog();
    npDialog.setOnPlaylistNameSelectedListener(this);
    npDialog.show(getFragmentManager(), "npDialog");
  }

  @Override public void onPlaylistNameSelected(String name) {

  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onMenuItemSelected(MenuItem item, Album album) {
    switch (item.getItemId()) {
      case R.id.popup_album_tracks:
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.TYPE, ProfileActivity.ALBUM);
        intent.putExtra(ProfileActivity.ID, album.getId());
        startActivity(intent);
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

  }

  @Override public void update(List<Album> data) {
    adapter.updateData(data);
  }
}
