package com.kelsos.mbrc.ui.fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumAdapter;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.presenters.ArtistAlbumPresenter;
import com.kelsos.mbrc.ui.views.ArtistAlbumsView;
import java.util.List;
import roboguice.RoboGuice;

public class ArtistAlbumsActivity extends AppCompatActivity implements ArtistAlbumsView, AlbumAdapter.MenuItemSelectedListener {

  public static final String ARTIST_ID = "artist_id";
  public static final String ARTIST_NAME = "artist_name";
  @Bind(R.id.album_recycler) RecyclerView recyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Inject private AlbumAdapter adapter;
  @Inject private ArtistAlbumPresenter presenter;

  private long artistId;

  public ArtistAlbumsActivity() {
    // Required empty public constructor
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_artist_albums);
    RoboGuice.getInjector(this).injectMembers(this);
    ButterKnife.bind(this);
    presenter.bind(this);
    GridLayoutManager manager = new GridLayoutManager(this, 2);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);

    adapter.setMenuItemSelectedListener(this);
    artistId = 0;
    String title = "";

    final Bundle extras = getIntent().getExtras();
    if (extras != null) {
      artistId = extras.getLong(ARTIST_ID, 0);
      title = extras.getString(ARTIST_NAME, "");
    }

    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setTitle(title);
    }

    presenter.load(artistId);
  }

  @Override public void update(List<Album> data) {
    adapter.updateData(data);
  }

  @Override public void showLoadFailed() {

  }

  @Override public void queueSuccess() {

  }

  @Override public void queueFailed() {

  }

  @Override public void onMenuItemSelected(MenuItem menuItem, Album album) {

    switch (menuItem.getItemId()) {
      case R.id.popup_album_play:
        presenter.queue(Queue.NOW, album);
        break;
      case R.id.popup_album_tracks:
        openProfile(album);
        break;
      case R.id.popup_album_queue_next:
        presenter.queue(Queue.NEXT, album);
        break;
      case R.id.popup_album_queue_last:
        presenter.queue(Queue.LAST, album);
        break;
      case R.id.popup_album_playlist:
        break;
    }
  }

  @Override public void onItemClicked(Album album) {
    openProfile(album);
  }

  private void openProfile(Album album) {
    Intent intent = new Intent(this, AlbumTracksActivity.class);
    intent.putExtra(AlbumTracksActivity.ALBUM_ID, album.getId());
    startActivity(intent);
  }
}
