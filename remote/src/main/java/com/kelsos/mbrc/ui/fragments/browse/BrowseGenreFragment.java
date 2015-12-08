package com.kelsos.mbrc.ui.fragments.browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.GenreAdapter;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.ui.activities.ProfileActivity;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;

import butterknife.ButterKnife;
import roboguice.fragment.RoboListFragment;

public class BrowseGenreFragment extends RoboListFragment
    implements PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

  @Inject private GenreAdapter mAdapter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  private void handlePopup(Pair<MenuItem, GenreDao> pair) {
    final MenuItem item = pair.first;
    final GenreDao genre = pair.second;

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

  private void queueTracks(GenreDao genre, String action) {

  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_now_playing, menu);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_library, container, false);
    ButterKnife.bind(this, view);
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
}
