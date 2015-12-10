package com.kelsos.mbrc.ui.fragments.browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumAdapter;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.ui.activities.ProfileActivity;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import roboguice.fragment.RoboFragment;

public class BrowseAlbumFragment extends RoboFragment
    implements
    PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

  @Inject private AlbumAdapter adapter;

  @Bind(R.id.album_recycler) RecyclerView recyclerView;

  @NonNull public static BrowseAlbumFragment newInstance() {
    return new BrowseAlbumFragment();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_library_grid, container, false);
    ButterKnife.bind(this, view);
    RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(),2);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
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

  private void handlePopup(Pair<MenuItem, AlbumDao> pair) {
    final MenuItem item = pair.first;
    final AlbumDao album = pair.second;

    switch (item.getItemId()) {
      case R.id.popup_album_tracks:
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.TYPE, ProfileActivity.ALBUM);
        intent.putExtra(ProfileActivity.ID, album.getId());
        startActivity(intent);
        break;
      case R.id.popup_album_play:
        queueTracks(album, "now");
        break;
      case R.id.popup_album_queue_last:
        queueTracks(album, "last");
        break;
      case R.id.popup_album_queue_next:
        queueTracks(album, "next");
        break;
      case R.id.popup_album_playlist:
        break;
      default:
        break;
    }
  }

  private void queueTracks(AlbumDao album, String action) {

  }
}
