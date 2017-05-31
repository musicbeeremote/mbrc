package com.kelsos.mbrc.ui.navigation.library.search

import android.annotation.SuppressLint
import android.app.Activity
import android.support.annotation.MenuRes
import android.support.annotation.Nullable
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.Search.SECTION_ALBUM
import com.kelsos.mbrc.annotations.Search.SECTION_ARTIST
import com.kelsos.mbrc.annotations.Search.SECTION_GENRE
import com.kelsos.mbrc.annotations.Search.SECTION_TRACK
import com.kelsos.mbrc.annotations.Search.Section
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.ui.SectionedRecyclerViewAdapter
import javax.inject.Inject

class SearchResultAdapter
@Inject
constructor(context: Activity) : SectionedRecyclerViewAdapter<SearchResultAdapter.SearchViewHolder>() {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var data: SearchResults? = null

  private var onSearchItemSelectedListener: OnSearchItemSelected? = null

  override val sectionCount: Int
    get() = 4

  override fun getItemCount(@Section section: Int): Int {
    data?.let {
      return when (section) {
        SECTION_GENRE -> it.genreList.count
        SECTION_ARTIST -> it.artistList.count
        SECTION_ALBUM -> it.albumList.count
        SECTION_TRACK -> it.trackList.count
        else -> 0L
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
      val position = relativePosition.toLong()

      when (section) {
        SECTION_ALBUM -> {
          val album = it.albumList.getItem(position)
          album?.let { holder.bindAlbum(it) }
        }
        SECTION_ARTIST -> {
          val artist = it.artistList.getItem(position)
          artist?.let { holder.bindArtist(it) }
        }
        SECTION_GENRE -> {
          val genre = it.genreList.getItem(position)
          genre?.let { holder.bindGenre(it) }
        }
        SECTION_TRACK -> {
          val track = it.trackList.getItem(position)
          track?.let { holder.bindTrack(it) }
        }
        else -> {
          throw IllegalArgumentException("Attempted to bind invalid section")
        }
      }
    }


  }

  private fun SearchViewHolder.bindTrack(track: Track) {
    this.lineOne.text = track.title
    this.lineTwo?.text = track.artist
    this.uiItemContextIndicator?.setOnClickListener { onContextClick(this, track) }
    this.itemView.setOnClickListener {
      onSearchItemSelectedListener?.trackSelected(track)
    }
  }

  private fun SearchViewHolder.bindGenre(genre: Genre) {
    this.lineOne.text = genre.genre
    this.uiItemContextIndicator?.setOnClickListener { onContextClick(this, genre) }
    this.itemView.setOnClickListener {
      onSearchItemSelectedListener?.genreSelected(genre)
    }
  }

  private fun SearchViewHolder.bindArtist(artist: Artist) {
    this.lineOne.text = artist.artist
    this.uiItemContextIndicator?.setOnClickListener { onContextClick(this, artist) }
    this.itemView.setOnClickListener {
      onSearchItemSelectedListener?.artistSelected(artist)
    }
  }

  private fun SearchViewHolder.bindAlbum(album: Album) {
    this.lineOne.text = album.album
    this.lineTwo?.text = album.artist
    this.uiItemContextIndicator?.setOnClickListener { onContextClick(this, album) }
    this.itemView.setOnClickListener {
      onSearchItemSelectedListener?.albumSelected(album)
    }
  }

  private fun onContextClick(holder: SearchViewHolder, track: Track) {
    showPopup(R.menu.popup_track, holder.uiItemContextIndicator!!, { item ->
      onSearchItemSelectedListener?.trackSelected(item, track)
      true
    })
  }

  private fun onContextClick(holder: SearchViewHolder, genre: Genre) {
    showPopup(R.menu.popup_genre, holder.uiItemContextIndicator!!, { item ->
      onSearchItemSelectedListener?.genreSelected(item, genre)
      true
    })
  }

  private fun onContextClick(holder: SearchViewHolder, album: Album) {
    showPopup(R.menu.popup_album, holder.uiItemContextIndicator!!, { item ->
      onSearchItemSelectedListener?.albumSelected(item, album)
      true
    })
  }

  private fun onContextClick(holder: SearchViewHolder, artist: Artist) {
    showPopup(R.menu.popup_artist, holder.uiItemContextIndicator!!, { item ->
      onSearchItemSelectedListener?.artistSelected(item, artist)
      true
    })
  }

  @SuppressLint("Range")
  override fun getItemViewType(@Section section: Int, relativePosition: Int, absolutePosition: Int): Int {
    if (section == SECTION_GENRE || section == SECTION_ARTIST) {
      return VIEW_TYPE_SINGLE
    } else if (section == SECTION_ALBUM || section == SECTION_TRACK) {
      return VIEW_TYPE_DUAL
    }

    return super.getItemViewType(section, relativePosition, absolutePosition)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
    val layout: Int
    when (viewType) {
      VIEW_TYPE_HEADER -> layout = R.layout.list_section_header
      VIEW_TYPE_DUAL -> layout = R.layout.ui_list_dual
      VIEW_TYPE_SINGLE -> layout = R.layout.listitem_single
      else -> layout = R.layout.listitem_single
    }
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
    fun albumSelected(item: MenuItem, album: Album)

    fun albumSelected(album: Album)

    fun artistSelected(item: MenuItem, artist: Artist)

    fun artistSelected(artist: Artist)

    fun genreSelected(item: MenuItem, genre: Genre)

    fun genreSelected(genre: Genre)

    fun trackSelected(item: MenuItem, track: Track)

    fun trackSelected(track: Track)
  }

  class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.line_one) lateinit var lineOne: TextView
    @BindView(R.id.line_two) @Nullable @JvmField var lineTwo: TextView? = null
    @BindView(R.id.ui_item_context_indicator) @Nullable @JvmField var uiItemContextIndicator: LinearLayout? = null

    init {
      ButterKnife.bind(this, itemView)
    }
  }

  companion object {

    private val VIEW_TYPE_DUAL = 1
    private val VIEW_TYPE_SINGLE = 2
  }

  fun update(searchResults: SearchResults) {
    this.data = searchResults
    notifyDataSetChanged()
  }
}

