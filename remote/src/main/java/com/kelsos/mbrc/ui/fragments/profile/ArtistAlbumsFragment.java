package com.kelsos.mbrc.ui.fragments.profile;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumAdapter;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import roboguice.fragment.RoboFragment;

public class ArtistAlbumsFragment extends RoboFragment
    implements
    PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

  private static final String ARTIST_ID = "artistId";
  @Inject private AlbumAdapter adapter;
  @Bind(R.id.album_recycler) RecyclerView recyclerView;
  private GridView mGrid;
  private long artistId;

  public ArtistAlbumsFragment() {
    // Required empty public constructor
  }

  public static ArtistAlbumsFragment newInstance(long artistId) {
    ArtistAlbumsFragment fragment = new ArtistAlbumsFragment();
    Bundle args = new Bundle();
    args.putLong(ARTIST_ID, artistId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      artistId = getArguments().getLong(ARTIST_ID);
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    final View view = inflater.inflate(R.layout.ui_library_grid, container, false);
    ButterKnife.bind(this, view);
    GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
    return view;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    registerForContextMenu(mGrid);
  }

  @Override public void onPlaylistNameSelected(String name) {

  }

  @Override public void onPlaylistSelected(String hash) {

  }

  @Override public void onNewPlaylistSelected() {

  }
}
