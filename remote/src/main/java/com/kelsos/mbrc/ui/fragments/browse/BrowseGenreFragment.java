package com.kelsos.mbrc.ui.fragments.browse;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.GenreCursorAdapter;
import com.kelsos.mbrc.dao.Genre;
import com.kelsos.mbrc.dao.GenreHelper;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.ui.activities.ProfileActivity;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.util.Logger;
import roboguice.fragment.RoboListFragment;
import rx.schedulers.Schedulers;

public class BrowseGenreFragment extends RoboListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
    PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

  private static final int URL_LOADER = 1;
  @Inject private GenreCursorAdapter mAdapter;
  @Inject private RemoteApi api;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    getLoaderManager().initLoader(URL_LOADER, null, this);
    setListAdapter(mAdapter);
    mAdapter.getPopupObservable()
        .subscribe(this::handlePopup, Logger::logThrowable);
  }

  private void handlePopup(Pair<MenuItem, Genre> pair) {
    final MenuItem item = pair.first;
    final Genre genre = pair.second;

    switch (item.getItemId()) {
      case R.id.popup_genre_play:
        queueTracks(genre, "now");
        break;
      case R.id.popup_genre_queue_last:
        queueTracks(genre, "last");
        break;
      case R.id.popup_genre_queue_next:
        queueTracks(genre, "next");
        break;
      case R.id.popup_genre_playlist:
        break;
      case R.id.popup_genre_artists:
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.TYPE, ProfileActivity.GENRE);
        intent.putExtra(ProfileActivity.ID, genre.getId());
        startActivity(intent);
        break;
      default:
        break;
    }
  }

  private void queueTracks(Genre genre, String action) {
    api.nowplayingQueue("genre", action, genre.getId())
        .observeOn(Schedulers.io())
        .subscribe((r) -> {
        }, Logger::logThrowable);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_now_playing, menu);
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_library, container, false);
  }

  @Override public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), GenreHelper.CONTENT_URI,
        GenreHelper.getProjection(), null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.swapCursor(data);
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {
    mAdapter.swapCursor(null);
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
}
