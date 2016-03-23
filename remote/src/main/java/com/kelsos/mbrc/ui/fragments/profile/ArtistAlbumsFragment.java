package com.kelsos.mbrc.ui.fragments.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumAdapter;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.presenters.ArtistAlbumPresenter;
import com.kelsos.mbrc.ui.views.ArtistAlbumsView;
import java.util.List;
import roboguice.RoboGuice;

public class ArtistAlbumsFragment extends Fragment implements ArtistAlbumsView {

  private static final String ARTIST_ID = "artistId";
  @Bind(R.id.album_recycler) RecyclerView recyclerView;
  @Inject private AlbumAdapter adapter;
  @Inject private ArtistAlbumPresenter presenter;

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
    RoboGuice.getInjector(getContext()).injectMembers(this);
    if (getArguments() != null) {
      artistId = getArguments().getLong(ARTIST_ID);
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_library_grid, container, false);
    ButterKnife.bind(this, view);
    presenter.bind(this);
    GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
    presenter.load(artistId);
    return view;
  }

  @Override public void updateArtistInfo(Artist artist) {

  }

  @Override public void update(List<Album> data) {
    adapter.updateData(data);
  }

  @Override public void showLoadFailed() {

  }
}
