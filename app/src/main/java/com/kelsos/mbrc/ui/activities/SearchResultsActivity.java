package com.kelsos.mbrc.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.SearchResultAdapter;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.data.library.Track;
import com.kelsos.mbrc.helper.PopupActionHandler;
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView;
import javax.inject.Inject;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieActivityModule;

public class SearchResultsActivity extends AppCompatActivity implements SearchResultAdapter.OnSearchItemSelected {

  public static final String QUERY = "com.kelsos.mbrc.extras.QUERY";
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.search_results_recycler) EmptyRecyclerView searchResultsRecycler;
  @BindView(R.id.empty_view_text) TextView emptyViewText;
  @BindView(R.id.empty_view) LinearLayout emptyView;

  @Inject SearchResultAdapter adapter;
  @Inject PopupActionHandler actionHandler;
  private Scope scope;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    scope = Toothpick.openScopes(getApplication(), this);
    scope.installModules(new SmoothieActivityModule(this));
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
    setContentView(R.layout.activity_search_results);
    ButterKnife.bind(this);

    final String query = getIntent().getStringExtra(QUERY);
    if (TextUtils.isEmpty(query)) {
      finish();
    } else {
      adapter.setQuery(query);
    }

    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(query);
    }

    searchResultsRecycler.setAdapter(adapter);
    searchResultsRecycler.setEmptyView(emptyView);
    searchResultsRecycler.setLayoutManager(new LinearLayoutManager(this));
    adapter.setOnSearchItemSelectedListener(this);
    emptyViewText.setText(R.string.no_results_found);
  }

  @Override
  protected void onDestroy() {
    Toothpick.closeScope(this);
    super.onDestroy();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void albumSelected(MenuItem item, Album album) {
    actionHandler.albumSelected(item, album, this);
  }

  @Override
  public void albumSelected(Album album) {
    actionHandler.albumSelected(album, this);
  }

  @Override
  public void artistSelected(MenuItem item, Artist artist) {
    actionHandler.artistSelected(item, artist, this);
  }

  @Override
  public void artistSelected(Artist artist) {
    actionHandler.artistSelected(artist, this);
  }

  @Override
  public void genreSelected(MenuItem item, Genre genre) {
    actionHandler.genreSelected(item, genre, this);
  }

  @Override
  public void genreSelected(Genre genre) {
    actionHandler.genreSelected(genre, this);
  }

  @Override
  public void trackSelected(MenuItem item, Track track) {
    actionHandler.trackSelected(item, track);
  }

  @Override
  public void trackSelected(Track track) {
    actionHandler.trackSelected(track);
  }
}
