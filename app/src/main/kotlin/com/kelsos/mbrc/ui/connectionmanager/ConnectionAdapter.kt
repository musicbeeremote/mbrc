package com.kelsos.mbrc.ui.connectionmanager

import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.extensions.gone
import com.kelsos.mbrc.extensions.show
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import kotterknife.bindView
import java.util.*

class ConnectionAdapter : RecyclerView.Adapter<ConnectionAdapter.ConnectionViewHolder>() {
  private val data: MutableList<ConnectionSettingsEntity>
  private var selectionId: Long = 0
  private var changeListener: ConnectionChangeListener? = null

  init {
    data = ArrayList()
    setHasStableIds(true)
  }

  fun setSelectionId(selectionId: Long) {
    this.selectionId = selectionId
    notifyDataSetChanged()
  }

  fun setChangeListener(changeListener: ConnectionChangeListener) {
    this.changeListener = changeListener
  }

  override fun getItemId(position: Int): Long = data[0].id

  override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ConnectionViewHolder {
    val holder = ConnectionViewHolder.create(viewGroup)

    holder.onOverflow {
      val adapterPosition = holder.adapterPosition
      val settings = data[adapterPosition]
      showPopup(settings, it)
    }

    holder.onClick {
      val adapterPosition = holder.adapterPosition
      val settings = data[adapterPosition]
      changeListener?.onDefault(settings)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
    holder.bind(data[position], selectionId)
  }

  private fun showPopup(settings: ConnectionSettingsEntity, v: View) {
    val popupMenu = PopupMenu(v.context, v)
    popupMenu.menuInflater.inflate(R.menu.connection_popup, popupMenu.menu)
    popupMenu.setOnMenuItemClickListener {
      if (changeListener == null) {
        return@setOnMenuItemClickListener false
      }

      when (it.itemId) {
        R.id.connection_default -> changeListener?.onDefault(settings)
        R.id.connection_edit -> changeListener?.onEdit(settings)
        R.id.connection_delete -> changeListener?.onDelete(settings)
        else -> {
        }
      }
      true
    }
    popupMenu.show()
  }

  override fun getItemCount(): Int = data.size

  fun updateData(data: List<ConnectionSettingsEntity>) {
    this.data.clear()
    this.data.addAll(data)
    notifyDataSetChanged()
  }

  class ConnectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val hostname: TextView by bindView(R.id.connection_settings__hostname_and_port)
    private val computerName: TextView by bindView(R.id.connection_settings__name)
    private val defaultSettings: ImageView by bindView(R.id.connection_settings__default_indicator)
    private val overflow: View by bindView(R.id.connection_settings__overflow)

    fun bind(entity: ConnectionSettingsEntity, selectionId: Long) {
      computerName.text = entity.name
      hostname.text = "${entity.address} : ${entity.port}"

      if (entity.id == selectionId) {
        defaultSettings.show()
      } else {
        defaultSettings.gone()
      }
    }

    fun onOverflow(action: (view: View) -> Unit) {
      overflow.setOnClickListener { action(it) }
    }

    fun onClick(action: () -> Unit) {
      itemView.setOnClickListener { action() }
    }

    companion object {
      fun create(parent: ViewGroup): ConnectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.ui_list_connection_settings, parent, false)
        return ConnectionViewHolder(view)
      }
    }
  }

  interface ConnectionChangeListener {
    fun onDelete(settings: ConnectionSettingsEntity)

    fun onEdit(settings: ConnectionSettingsEntity)

    fun onDefault(settings: ConnectionSettingsEntity)
  }
}