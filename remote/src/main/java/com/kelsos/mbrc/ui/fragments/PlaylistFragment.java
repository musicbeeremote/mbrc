package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistAdapter;
import com.kelsos.mbrc.data.SyncManager;
import roboguice.fragment.RoboFragment;

public class PlaylistFragment extends RoboFragment {

  @Bind(R.id.playlist_recycler) RecyclerView recyclerView;
  @Inject private SyncManager syncManager;
  @Inject private PlaylistAdapter adapter;

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_fragment_playlist, container, false);
    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
    return view;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onStart() {
    super.onStart();
    syncManager.startPlaylistSync();
  }

  public static PlaylistFragment newInstance() {
    return new PlaylistFragment();
  }
}
