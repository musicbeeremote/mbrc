package com.kelsos.mbrc.ui.fragments.browse;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumCursorAdapter;
import com.kelsos.mbrc.dao.Album;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.ui.activities.ProfileActivity;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.util.Logger;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import rx.schedulers.Schedulers;

public class BrowseAlbumFragment extends RoboFragment
    implements LoaderManager.LoaderCallbacks<Cursor>, GridView.OnItemClickListener,
    PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

  private static final int URL_LOADER = 0x12;
  @Inject private AlbumCursorAdapter mAdapter;
  @Inject private RemoteApi api;
  @InjectView(R.id.mbrc_grid_view) private GridView mGrid;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.ui_library_grid, container, false);
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
    getLoaderManager().initLoader(URL_LOADER, null, this);
    mAdapter.getPopupObservable()
        .subscribe(this::handlePopup, Logger::logThrowable);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mGrid.setAdapter(mAdapter);
    mGrid.setOnItemClickListener(this);
  }

  private void handlePopup(Pair<MenuItem, Album> pair) {
    final MenuItem item = pair.first;
    final Album album = pair.second;

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

  private void queueTracks(Album album, String action) {
    api.nowplayingQueue("album", action, album.getId())
        .observeOn(Schedulers.io())
        .subscribe((r) -> {
        }, Logger::logThrowable);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    //return new CursorLoader(getActivity(), AlbumHelper.CONTENT_URI,
    //    AlbumHelper.getProjection(), null, null, null);
    return null;
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.swapCursor(data);
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {
    mAdapter.swapCursor(null);
  }
}
