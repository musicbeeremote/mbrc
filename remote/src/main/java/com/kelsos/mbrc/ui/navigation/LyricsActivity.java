package com.kelsos.mbrc.ui.navigation;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.LyricsAdapter;
import com.kelsos.mbrc.presenters.LyricsPresenter;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.ui.views.LyricsView;
import java.util.List;

public class LyricsActivity extends BaseActivity implements LyricsView {

  @Bind(R.id.lyrics_recycler_view) RecyclerView recyclerView;
  @Inject private LyricsAdapter adapter;
  @Inject private LyricsPresenter presenter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lyrics);
    initialize();
    ButterKnife.bind(this);
    presenter.bind(this);
    recyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);
  }

  @Override public void onPause() {
    super.onPause();
    presenter.onPause();
  }

  @Override public void onResume() {
    super.onResume();
    presenter.onResume();
  }

  @Override public void updateLyrics(List<String> lyrics) {
    adapter.updateData(lyrics);
  }
}
