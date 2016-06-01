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
import com.google.inject.Inject;
import com.kelsos.mbrc.helper.PopupActionHandler;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.SearchResultAdapter;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.data.library.Track;
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView;
import roboguice.RoboGuice;

public class SearchResultsActivity extends AppCompatActivity implements SearchResultAdapter.OnSearchItemSelected {

  public static final String QUERY = "com.kelsos.mbrc.extras.QUERY";
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.search_results_recycler)
  EmptyRecyclerView searchResultsRecycler;
  @BindView(R.id.empty_view_text)
  TextView emptyViewText;
  @BindView(R.id.empty_view)
  LinearLayout emptyView;

  @Inject
  private SearchResultAdapter adapter;
  @Inject
  private PopupActionHandler actionHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_results);
    ButterKnife.bind(this);
    RoboGuice.getInjector(this).injectMembers(this);

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
    super.onDestroy();
    RoboGuice.destroyInjector(this);
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
    actionHandler.albumSelected(item, album);
  }

  @Override
  public void albumSelected(Album album) {
    actionHandler.albumSelected(album);
  }

  @Override
  public void artistSelected(MenuItem item, Artist artist) {
    actionHandler.artistSelected(item, artist);
  }

  @Override
  public void artistSelected(Artist artist) {
    actionHandler.artistSelected(artist);
  }

  @Override
  public void genreSelected(MenuItem item, Genre genre) {
    actionHandler.genreSelected(item, genre);
  }

  @Override
  public void genreSelected(Genre genre) {
    actionHandler.genreSelected(genre);
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
