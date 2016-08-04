package com.kelsos.mbrc.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.Search.Section;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Album_Table;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.data.library.Artist_Table;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.data.library.Genre_Table;
import com.kelsos.mbrc.data.library.Track;
import com.kelsos.mbrc.data.library.Track_Table;
import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import javax.inject.Inject;

import static com.kelsos.mbrc.annotations.Search.SECTION_ALBUM;
import static com.kelsos.mbrc.annotations.Search.SECTION_ARTIST;
import static com.kelsos.mbrc.annotations.Search.SECTION_GENRE;
import static com.kelsos.mbrc.annotations.Search.SECTION_TRACK;

public class SearchResultAdapter extends SectionedRecyclerViewAdapter<SearchResultAdapter.SearchViewHolder> {

  private static final int VIEW_TYPE_DUAL = 1;
  private static final int VIEW_TYPE_SINGLE = 2;

  private final LayoutInflater inflater;
  private final Typeface robotoMedium;

  private FlowQueryList<Genre> genreList;
  private FlowQueryList<Artist> artistList;
  private FlowQueryList<Album> albumList;
  private FlowQueryList<Track> trackList;

  private OnSearchItemSelected onSearchItemSelectedListener;

  @Inject
  public SearchResultAdapter(Activity context) {
    inflater = LayoutInflater.from(context);
    robotoMedium = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_medium.ttf");
  }

  @Override
  public int getSectionCount() {
    return 4;
  }

  @Override
  public int getItemCount(@Section int section) {

    if (section == SECTION_GENRE) {
      return genreList != null ? genreList.size() : 0;
    } else if (section == SECTION_ARTIST) {
      return artistList != null ? artistList.size() : 0;
    } else if (section == SECTION_ALBUM) {
      return albumList != null ? albumList.size() : 0;
    } else if (section == SECTION_TRACK) {
      return trackList != null ? trackList.size() : 0;
    }

    return 0;
  }

  public void setQuery(@NonNull String query) {
    String like = '%' + query + '%';
    genreList = SQLite.select().from(Genre.class).where(Genre_Table.genre.like(like)).flowQueryList();
    artistList = SQLite.select().from(Artist.class).where(Artist_Table.artist.like(like)).flowQueryList();
    albumList = SQLite.select().from(Album.class).where(Album_Table.album.like(like)).flowQueryList();
    trackList = SQLite.select().from(Track.class).where(Track_Table.title.like(like)).flowQueryList();
    notifyDataSetChanged();
  }

  @Override
  public void onBindHeaderViewHolder(SearchViewHolder holder, @Section int section) {
    switch (section) {
      case SECTION_ALBUM:
        holder.lineOne.setText(R.string.label_albums);
        break;
      case SECTION_ARTIST:
        holder.lineOne.setText(R.string.label_artists);
        break;
      case SECTION_GENRE:
        holder.lineOne.setText(R.string.label_genres);
        break;
      case SECTION_TRACK:
        holder.lineOne.setText(R.string.label_tracks);
        break;
      default:
        break;
    }
  }

  @Override
  public void onBindViewHolder(SearchViewHolder holder,
      @Section int section,
      int relativePosition,
      int absolutePosition) {
    switch (section) {
      case SECTION_ALBUM:
        final Album album = albumList.get(relativePosition);
        holder.lineOne.setText(album.getAlbum());
        if (holder.lineTwo != null) {
          holder.lineTwo.setText(album.getArtist());
        }
        assert holder.uiItemContextIndicator != null;
        holder.uiItemContextIndicator.setOnClickListener(v -> onContextClick(holder, album));
        holder.itemView.setOnClickListener(v -> {
          if (onSearchItemSelectedListener == null) {
            return;
          }
          onSearchItemSelectedListener.albumSelected(album);
        });
        break;
      case SECTION_ARTIST:
        final Artist artist = artistList.get(relativePosition);
        holder.lineOne.setText(artist.getArtist());
        assert holder.uiItemContextIndicator != null;
        holder.uiItemContextIndicator.setOnClickListener(v -> onContextClick(holder, artist));
        holder.itemView.setOnClickListener(v -> {
          if (onSearchItemSelectedListener == null) {
            return;
          }
          onSearchItemSelectedListener.artistSelected(artist);
        });
        break;
      case SECTION_GENRE:
        final Genre genre = genreList.get(relativePosition);
        holder.lineOne.setText(genre.getGenre());
        assert holder.uiItemContextIndicator != null;
        holder.uiItemContextIndicator.setOnClickListener(v -> onContextClick(holder, genre));
        holder.itemView.setOnClickListener(v -> {
          if (onSearchItemSelectedListener == null) {
            return;
          }
          onSearchItemSelectedListener.genreSelected(genre);
        });
        break;
      case SECTION_TRACK:
        final Track track = trackList.get(relativePosition);
        holder.lineOne.setText(track.getTitle());
        if (holder.lineTwo != null) {
          holder.lineTwo.setText(track.getArtist());
        }
        assert holder.uiItemContextIndicator != null;
        holder.uiItemContextIndicator.setOnClickListener(v -> onContextClick(holder, track));
        holder.itemView.setOnClickListener(v -> {
          if (onSearchItemSelectedListener == null) {
            return;
          }
          onSearchItemSelectedListener.trackSelected(track);
        });
        break;
      default:
        break;
    }
  }

  private void onContextClick(SearchViewHolder holder, Track track) {
    showPopup(R.menu.popup_track, holder.uiItemContextIndicator, item -> {
      if (onSearchItemSelectedListener == null) {
        return false;
      }
      onSearchItemSelectedListener.trackSelected(item, track);
      return true;
    });
  }

  private void onContextClick(SearchViewHolder holder, Genre genre) {
    showPopup(R.menu.popup_genre, holder.uiItemContextIndicator, item -> {
      if (onSearchItemSelectedListener == null) {
        return false;
      }
      onSearchItemSelectedListener.genreSelected(item, genre);
      return true;
    });
  }

  private void onContextClick(SearchViewHolder holder, Album album) {
    showPopup(R.menu.popup_album, holder.uiItemContextIndicator, item -> {
      if (onSearchItemSelectedListener == null) {
        return false;
      }
      onSearchItemSelectedListener.albumSelected(item, album);
      return true;
    });
  }

  private void onContextClick(SearchViewHolder holder, Artist artist) {
    showPopup(R.menu.popup_artist, holder.uiItemContextIndicator, item -> {
      if (onSearchItemSelectedListener == null) {
        return false;
      }
      onSearchItemSelectedListener.artistSelected(item, artist);
      return true;
    });
  }

  @Override
  public int getItemViewType(@Section int section, int relativePosition, int absolutePosition) {
    if (section == SECTION_GENRE || section == SECTION_ARTIST) {
      return VIEW_TYPE_SINGLE;
    } else if (section == SECTION_ALBUM || section == SECTION_TRACK) {
      return VIEW_TYPE_DUAL;
    }

    return super.getItemViewType(section, relativePosition, absolutePosition);
  }

  @Override
  public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int layout;
    switch (viewType) {
      case VIEW_TYPE_HEADER:
        layout = R.layout.list_section_header;
        break;
      case VIEW_TYPE_DUAL:
        layout = R.layout.ui_list_dual;
        break;
      case VIEW_TYPE_SINGLE:
        layout = R.layout.listitem_single;
        break;
      default:
        layout = R.layout.listitem_single;
        break;
    }
    final View view = inflater.inflate(layout, parent, false);
    SearchViewHolder holder = new SearchViewHolder(view);

    if (viewType == VIEW_TYPE_HEADER) {
      holder.lineOne.setTypeface(robotoMedium);
    }

    return holder;
  }

  private void showPopup(@MenuRes int menu, View view, PopupMenu.OnMenuItemClickListener listener) {
    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
    popupMenu.inflate(menu);
    popupMenu.setOnMenuItemClickListener(listener);
    popupMenu.show();
  }

  public void setOnSearchItemSelectedListener(OnSearchItemSelected onSearchItemSelectedListener) {
    this.onSearchItemSelectedListener = onSearchItemSelectedListener;
  }

  public interface OnSearchItemSelected {
    void albumSelected(MenuItem item, Album album);

    void albumSelected(Album album);

    void artistSelected(MenuItem item, Artist artist);

    void artistSelected(Artist artist);

    void genreSelected(MenuItem item, Genre genre);

    void genreSelected(Genre genre);

    void trackSelected(MenuItem item, Track track);

    void trackSelected(Track track);
  }

  static class SearchViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.line_one)
    TextView lineOne;
    @Nullable
    @BindView(R.id.line_two)
    TextView lineTwo;
    @Nullable
    @BindView(R.id.ui_item_context_indicator)
    LinearLayout uiItemContextIndicator;

    SearchViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

