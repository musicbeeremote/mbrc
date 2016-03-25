package com.kelsos.mbrc.ui.fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ArtistAdapter;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.presenters.GenreArtistsPresenter;
import com.kelsos.mbrc.ui.views.GenreArtistView;
import java.util.List;
import roboguice.RoboGuice;

public class GenreArtistsActivity extends AppCompatActivity
    implements GenreArtistView, ArtistAdapter.MenuItemSelectedListener {

  public static final String GENRE_ID = "genre_id";
  public static final String GENRE_NAME = "genre_name";
  @Bind(R.id.genre_artists_recycler) RecyclerView recyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Inject private ArtistAdapter adapter;
  @Inject private GenreArtistsPresenter presenter;
  private long genreId;

  public GenreArtistsActivity() {
    // Required empty public constructor
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_genre_artists);
    RoboGuice.getInjector(this).injectMembers(this);
    ButterKnife.bind(this);
    presenter.bind(this);
    RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
    adapter.setMenuItemSelectedListener(this);
    final Bundle extras = getIntent().getExtras();
    genreId = 0;

    String title = "";

    if (extras != null) {
      genreId = extras.getLong(GENRE_ID, 0);
      title = extras.getString(GENRE_NAME, "");
    }

    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setTitle(title);
    }

    presenter.load(genreId);
  }

  @Override public void update(List<Artist> data) {
    adapter.updateData(data);
  }

  @Override public void onQueueSuccess() {

  }

  @Override public void onQueueFailure() {

  }

  private void openProfile(Artist artist) {
    Intent intent = new Intent(this, ArtistAlbumsActivity.class);
    intent.putExtra(ArtistAlbumsActivity.ARTIST_ID, artist.getId());
    intent.putExtra(ArtistAlbumsActivity.ARTIST_NAME, artist.getName());
    startActivity(intent);
  }

  @Override public void onMenuItemSelected(MenuItem menuItem, Artist artist) {
    int itemId = menuItem.getItemId();
    switch (itemId) {
      case R.id.popup_artist_play:
        presenter.queue(Queue.NOW, artist);
        break;
      case R.id.popup_artist_album:
        openProfile(artist);
        break;
      case R.id.popup_artist_queue_next:
        presenter.queue(Queue.NEXT, artist);
        break;
      case R.id.popup_artist_queue_last:
        presenter.queue(Queue.LAST, artist);
        break;
      case R.id.popup_artist_playlist:
        break;
    }
  }

  @Override public void onItemClicked(Artist artist) {
    openProfile(artist);
  }
}
