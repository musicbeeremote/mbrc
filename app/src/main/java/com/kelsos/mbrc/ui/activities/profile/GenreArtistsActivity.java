package com.kelsos.mbrc.ui.activities.profile;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ArtistEntryAdapter;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.helper.PopupActionHandler;
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView;
import roboguice.RoboGuice;

public class GenreArtistsActivity extends AppCompatActivity
    implements ArtistEntryAdapter.MenuItemSelectedListener {

  public static final String GENRE_NAME = "genre_name";

  @BindView(R.id.genre_artists_recycler)
  EmptyRecyclerView recyclerView;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.empty_view)
  LinearLayout emptyView;

  @Inject
  private ArtistEntryAdapter adapter;

  @Inject
  private PopupActionHandler actionHandler;

  private String genre;

  public GenreArtistsActivity() {
    // Required empty public constructor
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_genre_artists);
    RoboGuice.getInjector(this).injectMembers(this);
    ButterKnife.bind(this);

    final Bundle extras = getIntent().getExtras();

    if (extras != null) {
      genre = extras.getString(GENRE_NAME);
    }

    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setTitle(genre);
    }

    adapter.init(genre);
    adapter.setMenuItemSelectedListener(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);
    recyclerView.setEmptyView(emptyView);
  }

  @Override public void onMenuItemSelected(MenuItem menuItem, Artist entry) {
    actionHandler.artistSelected(menuItem, entry);
  }

  @Override public void onItemClicked(Artist artist) {
    actionHandler.artistSelected(artist);
  }
}
