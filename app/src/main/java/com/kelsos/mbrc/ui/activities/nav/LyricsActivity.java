package com.kelsos.mbrc.ui.activities.nav;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.LyricsAdapter;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.LyricsUpdated;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import roboguice.RoboGuice;

public class LyricsActivity extends BaseActivity {
  private static final String NEWLINE = "\r\n|\n";
  @Inject
  private RxBus bus;
  @BindView(R.id.lyrics_recycler_view)
  RecyclerView lyricsRecycler;
  @BindView(R.id.empty_view)
  LinearLayout emptyView;
  @BindView(R.id.empty_view_text)
  TextView emptyText;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lyrics);
    ButterKnife.bind(this);
    RoboGuice.getInjector(this).injectMembers(this);
    super.setup();

    lyricsRecycler.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    lyricsRecycler.setLayoutManager(layoutManager);
  }

  @Override
  public void onStart() {
    super.onStart();
    bus.register(this, LyricsUpdated.class, this::updateLyricsData);
  }

  @Override
  public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  private void updateLyricsData(LyricsUpdated update) {
    final String text = update.getLyrics();

    final List<String> lyrics = new ArrayList<>(Arrays.asList(text.split(NEWLINE)));

    if (lyrics.size() == 1) {
      lyricsRecycler.setVisibility(View.GONE);
      emptyText.setText(lyrics.get(0));
      emptyView.setVisibility(View.VISIBLE);
    } else {
      emptyView.setVisibility(View.GONE);
      lyricsRecycler.setVisibility(View.VISIBLE);
      LyricsAdapter adapter = new LyricsAdapter(this, lyrics);
      lyricsRecycler.setAdapter(adapter);
    }
  }

  @Override
  protected int active() {
    return R.id.nav_lyrics;
  }
}
