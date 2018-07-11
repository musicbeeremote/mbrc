package com.kelsos.mbrc.ui.navigation.library.albumtracks

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.databinding.FragmentAlbumTracksBinding
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackEntryAdapter
import com.kelsos.mbrc.utilities.RemoteUtils.sha1
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AlbumTracksFragment : Fragment(), MenuItemSelectedListener<Track> {

  private val adapter: TrackEntryAdapter by inject()
  private val viewModel: AlbumTracksViewModel by viewModel()

  private lateinit var album: AlbumInfo
  private var _binding: FragmentAlbumTracksBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentAlbumTracksBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.albumTracksAlbum.text = album.album
    binding.albumTracksArtist.text = album.artist
    loadCover(album.artist, album.album)

    binding.albumTracksPlay.isGone = false
    binding.albumTracksPlay.setOnClickListener {
      // TODO: Queue
    }
    adapter.setMenuItemSelectedListener(this)
    binding.albumTracksTrackList.layoutManager = LinearLayoutManager(requireContext())
    binding.albumTracksTrackList.adapter = adapter

    viewModel.load(album)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    album = AlbumTracksFragmentArgs.fromBundle(requireArguments()).run {
      AlbumInfo(album, artist, "")
    }
  }

  private fun loadCover(artist: String, album: String) {
    val cache = File(requireContext().cacheDir, "covers")
    Picasso.get()
      .load(File(cache, sha1("${artist}_$album")))
      .noFade()
      .config(Bitmap.Config.RGB_565)
      .error(R.drawable.ic_image_no_cover)
      .placeholder(R.drawable.ic_image_no_cover)
      .resizeDimen(R.dimen.cover_size, R.dimen.cover_size)
      .centerCrop()
      .into(binding.albumTracksCover)
  }

  override fun onMenuItemSelected(@IdRes itemId: Int, item: Track) {
  }

  override fun onItemClicked(item: Track) {
  }

  suspend fun update(tracks: PagingData<Track>) {
    adapter.submitData(tracks)
  }

  fun queue(success: Boolean, tracks: Int) {
    val message = if (success) {
      getString(R.string.queue_result__success, tracks)
    } else {
      getString(R.string.queue_result__failure)
    }
    Snackbar.make(binding.root, R.string.queue_result__success, Snackbar.LENGTH_SHORT)
      .setText(message)
      .show()
  }
}
