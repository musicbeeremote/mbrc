package com.kelsos.mbrc.ui.navigation.library.search

import android.annotation.SuppressLint
import android.support.annotation.MenuRes
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action
import com.kelsos.mbrc.ui.SectionedRecyclerViewAdapter
import com.kelsos.mbrc.ui.navigation.library.Search.SECTION_ALBUM
import com.kelsos.mbrc.ui.navigation.library.Search.SECTION_ARTIST
import com.kelsos.mbrc.ui.navigation.library.Search.SECTION_GENRE
import com.kelsos.mbrc.ui.navigation.library.Search.SECTION_TRACK
import com.kelsos.mbrc.ui.navigation.library.Search.Section
import kotterknife.bindOptionalView
import kotterknife.bindView
import javax.inject.Inject

class SearchResultAdapter
@Inject
constructor() : SectionedRecyclerViewAdapter<SearchResultAdapter.SearchViewHolder>() {
  private var data: SearchResults? = null

  private var onSearchItemSelectedListener: OnSearchItemSelected? = null

  override val sectionCount: Int
    get() = 4

  override fun getItemCount(@Section section: Int): Int {
    data?.let {
      return when (section) {
        SECTION_GENRE -> it.genreList.value?.size ?: 0
        SECTION_ARTIST -> it.artistList.value?.size ?: 0
        SECTION_ALBUM -> it.albumList.value?.size ?: 0
        SECTION_TRACK -> it.trackList.value?.size ?: 0
        else -> 0
      }.toInt()
    }

    return 0
  }

  override fun onBindHeaderViewHolder(holder: SearchViewHolder, @Section section: Int) {
    when (section) {
      SECTION_ALBUM -> holder.lineOne.setText(R.string.label_albums)
      SECTION_ARTIST -> holder.lineOne.setText(R.string.label_artists)
      SECTION_GENRE -> holder.lineOne.setText(R.string.label_genres)
      SECTION_TRACK -> holder.lineOne.setText(R.string.label_tracks)
      else -> {
      }
    }
  }

  override fun onBindViewHolder(
    holder: SearchViewHolder,
    @Section section: Int,
    relativePosition: Int,
    absolutePosition: Int
  ) {

    data?.let {

      when (section) {
        SECTION_ALBUM -> {
          val albums = it.albumList.value ?: return@let
          val album = albums[relativePosition] ?: return@let
          holder.bindAlbum(album)
        }
        SECTION_ARTIST -> {
          val artists = it.artistList.value ?: return@let
          val artist = artists[relativePosition] ?: return@let
          holder.bindArtist(artist)
        }
        SECTION_GENRE -> {
          val genres = it.genreList.value ?: return@let
          val genre = genres[relativePosition] ?: return@let
          holder.bindGenre(genre)
        }
        SECTION_TRACK -> {
          val tracks = it.trackList.value ?: return@let
          val track = tracks[relativePosition] ?: return@let
          holder.bindTrack(track)
        }
        else -> {
          throw IllegalArgumentException("Attempted to bind invalid section")
        }
      }
    }
  }

  private fun SearchViewHolder.bindTrack(track: TrackEntity) {
    this.lineOne.text = track.title
    this.lineTwo?.text = track.artist
    this.uiItemContextIndicator?.setOnClickListener { onContextClick(this, track) }
    this.itemView.setOnClickListener {
      onSearchItemSelectedListener?.trackSelected(track)
    }
  }

  private fun SearchViewHolder.bindGenre(genre: GenreEntity) {
    this.lineOne.text = genre.genre
    this.uiItemContextIndicator?.setOnClickListener { onContextClick(this, genre) }
    this.itemView.setOnClickListener {
      onSearchItemSelectedListener?.genreSelected(genre)
    }
  }

  private fun SearchViewHolder.bindArtist(artist: ArtistEntity) {
    this.lineOne.text = artist.artist
    this.uiItemContextIndicator?.setOnClickListener { onContextClick(this, artist) }
    this.itemView.setOnClickListener {
      onSearchItemSelectedListener?.artistSelected(artist)
    }
  }

  private fun SearchViewHolder.bindAlbum(album: AlbumEntity) {
    this.lineOne.text = album.album
    this.lineTwo?.text = album.artist
    this.uiItemContextIndicator?.setOnClickListener { onContextClick(this, album) }
    this.itemView.setOnClickListener {
      onSearchItemSelectedListener?.albumSelected(album)
    }
  }

  private fun onContextClick(holder: SearchViewHolder, track: TrackEntity) {
    showPopup(R.menu.popup_track, holder.uiItemContextIndicator!!, { item ->
      //onSearchItemSelectedListener?.trackSelected(item, track)
      true
    })
  }

  private fun onContextClick(holder: SearchViewHolder, genre: GenreEntity) {
    showPopup(R.menu.popup_genre, holder.uiItemContextIndicator!!, { item ->
      //onSearchItemSelectedListener?.genreSelected(item, genre)
      true
    })
  }

  private fun onContextClick(holder: SearchViewHolder, album: AlbumEntity) {
    showPopup(R.menu.popup_album, holder.uiItemContextIndicator!!, { item ->
      //onSearchItemSelectedListener?.albumSelected(item, album)
      true
    })
  }

  private fun onContextClick(holder: SearchViewHolder, artist: ArtistEntity) {
    showPopup(R.menu.popup_artist, holder.uiItemContextIndicator!!, { item ->
      //onSearchItemSelectedListener?.artistSelected(item, artist)
      true
    })
  }

  @SuppressLint("Range")
  override fun getItemViewType(
    @Section section: Int,
    relativePosition: Int,
    absolutePosition: Int
  ): Int {
    if (section == SECTION_GENRE || section == SECTION_ARTIST) {
      return VIEW_TYPE_SINGLE
    } else if (section == SECTION_ALBUM || section == SECTION_TRACK) {
      return VIEW_TYPE_DUAL
    }

    return super.getItemViewType(section, relativePosition, absolutePosition)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
    val layout: Int = when (viewType) {
      VIEW_TYPE_HEADER -> R.layout.list_section_header
      VIEW_TYPE_DUAL -> R.layout.ui_list_dual
      VIEW_TYPE_SINGLE -> R.layout.listitem_single
      else -> R.layout.listitem_single
    }
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(layout, parent, false)
    return SearchViewHolder(view)
  }

  private fun showPopup(@MenuRes menu: Int, view: View, listener: (MenuItem) -> Boolean) {
    val popupMenu = PopupMenu(view.context, view)
    popupMenu.inflate(menu)
    popupMenu.setOnMenuItemClickListener({ listener.invoke(it) })
    popupMenu.show()
  }

  fun setOnSearchItemSelectedListener(onSearchItemSelectedListener: OnSearchItemSelected) {
    this.onSearchItemSelectedListener = onSearchItemSelectedListener
  }

  interface OnSearchItemSelected {
    fun albumSelected(@Action action: String, album: AlbumEntity)

    fun albumSelected(album: AlbumEntity)

    fun artistSelected(@Action action: String, artist: ArtistEntity)

    fun artistSelected(artist: ArtistEntity)

    fun genreSelected(@Action action: String, genre: GenreEntity)

    fun genreSelected(genre: GenreEntity)

    fun trackSelected(@Action action: String, track: TrackEntity)

    fun trackSelected(track: TrackEntity)
  }

  class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val lineOne: TextView by bindView(R.id.line_one)
    val lineTwo: TextView? by bindOptionalView(R.id.line_two)
    val uiItemContextIndicator: LinearLayout? by bindOptionalView(R.id.ui_item_context_indicator)
  }

  fun update(searchResults: SearchResults) {
    this.data = searchResults
    notifyDataSetChanged()
  }

  companion object {
    private const val VIEW_TYPE_DUAL = 1
    private const val VIEW_TYPE_SINGLE = 2
  }
}