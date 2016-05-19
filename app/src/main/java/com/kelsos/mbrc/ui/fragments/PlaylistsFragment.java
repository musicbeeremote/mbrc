package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.PlaylistAvailable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.RoboGuice;

public class PlaylistsFragment extends Fragment implements PlaylistAdapter.OnPlaylistPressedListener {

  @BindView(R.id.playlist_list) RecyclerView playlistList;

  @Inject private PlaylistAdapter adapter;
  @Inject private Bus bus;

  public PlaylistsFragment() {
    // Required empty public constructor
  }

  public static PlaylistsFragment newInstance() {
    return new PlaylistsFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(getContext()).injectMembers(this);
    adapter.setPlaylistPressedListener(this);
  }

  @Override public void onStart() {
    super.onStart();
    bus.register(this);
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlaylistList, true)));
  }

  @Override public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final View view = inflater.inflate(R.layout.fragment_playlists, container, false);
    ButterKnife.bind(this, view);
    playlistList.setAdapter(adapter);
    playlistList.setLayoutManager(new LinearLayoutManager(getContext()));
    return view;
  }

  @Subscribe public void onPlaylistAvailable(PlaylistAvailable event) {
    adapter.update(event.getPlaylist());
  }

  @Override public void playlistPressed(String path) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlaylistPlay, path)));
  }
}
