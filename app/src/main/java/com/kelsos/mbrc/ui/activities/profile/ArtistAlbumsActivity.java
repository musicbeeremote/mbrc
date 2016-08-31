package com.kelsos.mbrc.ui.activities.profile;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumEntryAdapter;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.helper.PopupActionHandler;
import com.kelsos.mbrc.ui.activities.FontActivity;
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView;
import javax.inject.Inject;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieActivityModule;

public class ArtistAlbumsActivity extends FontActivity implements AlbumEntryAdapter.MenuItemSelectedListener {

  public static final String ARTIST_NAME = "artist_name";

  @BindView(R.id.album_recycler) EmptyRecyclerView recyclerView;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.empty_view) LinearLayout emptyView;

  @Inject PopupActionHandler actionHandler;
  @Inject AlbumEntryAdapter adapter;

  private String artist;
  private Scope scope;

  public ArtistAlbumsActivity() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    scope = Toothpick.openScopes(getApplication(), this);
    scope.installModules(new SmoothieActivityModule(this));
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
    setContentView(R.layout.activity_artist_albums);
    ButterKnife.bind(this);

    final Bundle extras = getIntent().getExtras();
    if (extras != null) {
      artist = extras.getString(ARTIST_NAME);
    }

    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setTitle(artist);
    }

    adapter.setMenuItemSelectedListener(this);
    adapter.init(artist);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);
    recyclerView.setEmptyView(emptyView);
  }

  @Override
  public void onMenuItemSelected(MenuItem menuItem, Album album) {
    actionHandler.albumSelected(menuItem, album, this);
  }

  @Override
  public void onItemClicked(Album album) {
    actionHandler.albumSelected(album, this);
  }

  @Override
  protected void onDestroy() {
    Toothpick.closeScope(this);
    super.onDestroy();
  }
}