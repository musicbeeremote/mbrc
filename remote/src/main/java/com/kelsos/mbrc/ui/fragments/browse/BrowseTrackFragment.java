package com.kelsos.mbrc.ui.fragments.browse;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackAdapter;
import com.kelsos.mbrc.dao.Track;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import roboguice.fragment.RoboFragment;

public class BrowseTrackFragment extends RoboFragment
    implements PlaylistDialogFragment.OnPlaylistSelectedListener {

  @Bind(R.id.library_recycler) RecyclerView recyclerView;
  @Inject private TrackAdapter mAdapter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private void handlePopupSelection(Pair<MenuItem, Track> pair) {
    final MenuItem item = pair.first;
    final Track track = pair.second;

    switch (item.getItemId()) {
      case R.id.popup_track_play:
        queueTracks(track, "now");
        break;
      case R.id.popup_track_playlist:
        break;
      case R.id.popup_track_queue_next:
        queueTracks(track, "next");
        break;
      case R.id.popup_track_queue_last:
        queueTracks(track, "last");
        break;
      default:
        break;
    }
  }

  private void queueTracks(Track track, String action) {

  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_library, container, false);
    ButterKnife.bind(this, view);
    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(manager);
    return view;
  }

  @Override public void onPlaylistSelected(String hash) {

  }

  @Override public void onNewPlaylistSelected() {

  }
}
