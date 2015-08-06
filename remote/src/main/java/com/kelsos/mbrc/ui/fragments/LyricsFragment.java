package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.LyricsAdapter;
import com.kelsos.mbrc.events.ui.LyricsUpdated;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Arrays;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class LyricsFragment extends RoboFragment {
  public static final String NEWLINE = "\r\n";
  @Inject Bus bus;
  @InjectView(R.id.lyrics_recycler_view) private RecyclerView mRecyclerView;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(layoutManager);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.ui_fragment_lyrics, container, false);
  }

  @Override public void onStart() {
    super.onStart();
    bus.register(this);
  }

  @Override public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  @Subscribe public void updateLyricsData(LyricsUpdated update) {
    final ArrayList<String> lyrics =
        new ArrayList<>(Arrays.asList(update.getLyrics().split(NEWLINE)));
    LyricsAdapter adapter = new LyricsAdapter(getActivity(), lyrics);
    mRecyclerView.setAdapter(adapter);
  }
}
