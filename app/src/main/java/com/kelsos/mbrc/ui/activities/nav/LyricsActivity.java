package com.kelsos.mbrc.ui.activities.nav;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.LyricsAdapter;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.LyricsUpdatedEvent;
import com.kelsos.mbrc.presenters.LyricsPresenter;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.views.LyricsView;
import java.util.List;
import javax.inject.Inject;
import toothpick.Scope;
import toothpick.Toothpick;

public class LyricsActivity extends BaseActivity implements LyricsView {
  @Inject RxBus bus;
  @Inject LyricsPresenter presenter;

  @BindView(R.id.lyrics_recycler_view) RecyclerView lyricsRecycler;
  @BindView(R.id.empty_view) LinearLayout emptyView;
  @BindView(R.id.empty_view_text) TextView emptyText;
  private Scope scope;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    scope = Toothpick.openScopes(getApplication(), this);
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
    setContentView(R.layout.activity_lyrics);
    ButterKnife.bind(this);
    super.setup();
    lyricsRecycler.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    lyricsRecycler.setLayoutManager(layoutManager);
  }

  @Override
  protected void onDestroy() {
    Toothpick.closeScope(this);
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
    presenter.attach(this);
    bus.register(this, LyricsUpdatedEvent.class, this::onLyricsUpdated, true);
    presenter.load();
  }

  @Override
  protected void onPause() {
    super.onPause();
    bus.unregister(this);
    presenter.detach();
  }

  @Override
  public void updateLyrics(List<String> lyrics) {
    if (lyrics.size() == 1) {
      lyricsRecycler.setVisibility(View.GONE);
      emptyText.setText(lyrics.get(0));
      emptyView.setVisibility(View.VISIBLE);
    } else {
      emptyView.setVisibility(View.GONE);
      lyricsRecycler.setVisibility(View.VISIBLE);
      LyricsAdapter adapter = new LyricsAdapter(lyrics);
      lyricsRecycler.setAdapter(adapter);
    }
  }

  private void onLyricsUpdated(LyricsUpdatedEvent update) {
    presenter.updateLyrics(update.getLyrics());
  }

  @Override
  protected int active() {
    return R.id.nav_lyrics;
  }
}
