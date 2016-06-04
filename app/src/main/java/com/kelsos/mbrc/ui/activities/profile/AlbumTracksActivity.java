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
import butterknife.OnClick;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackEntryAdapter;
import com.kelsos.mbrc.data.library.Track;
import com.kelsos.mbrc.helper.PopupActionHandler;
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView;
import roboguice.RoboGuice;

public class AlbumTracksActivity extends AppCompatActivity
    implements TrackEntryAdapter.MenuItemSelectedListener {

  public static final String ALBUM_NAME = "albumName";

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.list_tracks)
  EmptyRecyclerView listTracks;

  @BindView(R.id.empty_view)
  LinearLayout emptyView;

  @Inject
  private TrackEntryAdapter adapter;

  @Inject
  private PopupActionHandler actionHandler;

  private String album;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public AlbumTracksActivity() {
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_album_tracks);
    RoboGuice.getInjector(this).injectMembers(this);
    ButterKnife.bind(this);
    final Bundle extras = getIntent().getExtras();

    if (extras != null) {
      album = extras.getString(ALBUM_NAME);
    }

    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setTitle(album);
    }

    adapter.init(album);
    adapter.setMenuItemSelectedListener(this);
    listTracks.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    listTracks.setAdapter(adapter);
    listTracks.setEmptyView(emptyView);

  }

  @Override public boolean onOptionsItemSelected(final MenuItem item) {
    final int itemId = item.getItemId();

    if (itemId == android.R.id.home) {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @OnClick(R.id.play_album) public void onPlayClicked() {

  }


  @Override public void onMenuItemSelected(MenuItem menuItem, Track entry) {
    actionHandler.trackSelected(menuItem, entry);

  }

  @Override public void onItemClicked(Track track) {
    actionHandler.trackSelected(track);
  }
}
