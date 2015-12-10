package com.kelsos.mbrc.ui.fragments.browse;

import android.content.Intent;
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
import com.kelsos.mbrc.adapters.ArtistAdapter;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.ui.activities.ProfileActivity;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import roboguice.fragment.RoboFragment;

public class BrowseArtistFragment extends RoboFragment
    implements PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

  @Bind(R.id.library_recycler) RecyclerView recyclerView;
  @Inject private ArtistAdapter adapter;

  @NonNull public static BrowseArtistFragment newInstance() {
    return new BrowseArtistFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_library, container, false);
    ButterKnife.bind(this, view);
    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
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

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private void handlePopupSelection(Pair<MenuItem, ArtistDao> pair) {
    final MenuItem item = pair.first;
    final ArtistDao artist = pair.second;

    switch (item.getItemId()) {
      case R.id.popup_artist_queue_next:
        queueTracks(artist, "next");
        break;
      case R.id.popup_artist_queue_last:
        queueTracks(artist, "last");
        break;
      case R.id.popup_artist_play:
        queueTracks(artist, "now");
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

  private void queueTracks(ArtistDao artist, String action) {

  }
}
