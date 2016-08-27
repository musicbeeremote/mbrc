package com.kelsos.mbrc.ui.activities.profile;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackEntryAdapter;
import com.kelsos.mbrc.data.library.Track;
import com.kelsos.mbrc.domain.AlbumInfo;
import com.kelsos.mbrc.helper.PopupActionHandler;
import com.kelsos.mbrc.ui.activities.FontActivity;
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieActivityModule;

public class AlbumTracksActivity extends FontActivity implements TrackEntryAdapter.MenuItemSelectedListener {

  public static final String ALBUM = "albumName";

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.list_tracks) EmptyRecyclerView listTracks;
  @BindView(R.id.empty_view) LinearLayout emptyView;

  @Inject TrackEntryAdapter adapter;
  @Inject PopupActionHandler actionHandler;

  private AlbumInfo album;
  private Scope scope;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public AlbumTracksActivity() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    scope = Toothpick.openScopes(getApplication(), this);
    scope.installModules(new SmoothieActivityModule(this));
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
    setContentView(R.layout.activity_album_tracks);
    ButterKnife.bind(this);
    final Bundle extras = getIntent().getExtras();

    if (extras != null) {
      album = extras.getParcelable(ALBUM);
    }

    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);

      if (TextUtils.isEmpty(album.album())) {
        actionBar.setTitle(R.string.non_album_tracks);
      } else {
        actionBar.setTitle(album.album());
      }

    }

    adapter.init(album);
    adapter.setMenuItemSelectedListener(this);
    listTracks.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    listTracks.setAdapter(adapter);
    listTracks.setEmptyView(emptyView);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    final int itemId = item.getItemId();

    if (itemId == android.R.id.home) {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @OnClick(R.id.play_album)
  public void onPlayClicked() {

  }

  @Override
  public void onMenuItemSelected(MenuItem menuItem, Track entry) {
    actionHandler.trackSelected(menuItem, entry);
  }

  @Override
  public void onItemClicked(Track track) {
    actionHandler.trackSelected(track);
  }

  @Override
  protected void onDestroy() {
    Toothpick.closeScope(this);
    super.onDestroy();
  }
}
