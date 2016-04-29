package com.kelsos.mbrc.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.domain.Playlist
import java.util.*

class PlaylistDialogAdapter
@Inject constructor(context: Context) : RecyclerView.Adapter<PlaylistDialogAdapter.ViewHolder>() {

  private val inflater: LayoutInflater
  private val data: MutableList<Playlist>
  private var selection: Int = 0

  init {
    inflater = LayoutInflater.from(context)
    data = ArrayList<Playlist>()
  }

  fun update(data: List<Playlist>) {
    this.data.clear()
    this.data.addAll(data)
    notifyDataSetChanged()
  }

  val selectedPlaylist: Playlist
    get() = this.data[selection]

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = inflater.inflate(R.layout.listitem_single_text_only, parent, false)
    val holder = ViewHolder(view)
    holder.itemView.setOnClickListener { v -> selection = holder.adapterPosition }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val playlist = data[holder.adapterPosition]
    holder.text.text = playlist.name
    holder.itemView.isSelected = selection == holder.adapterPosition
  }

  override fun getItemCount(): Int {
    return data.size
  }

  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    @Bind(R.id.line_one) lateinit var text: TextView

    init {
      ButterKnife.bind(this, view)
    }
  }
}
