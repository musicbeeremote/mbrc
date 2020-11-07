package com.kelsos.mbrc.ui.connectionmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import kotterknife.bindView

class ConnectionAdapter : ListAdapter<ConnectionSettingsEntity,
  ConnectionAdapter.ConnectionViewHolder>(DIFF_CALLBACK) {

  private var selectionId: Long = 0
  private var changeListener: ConnectionChangeListener? = null

  fun setSelectionId(selectionId: Long) {
    this.selectionId = selectionId
    notifyDataSetChanged()
  }

  fun setChangeListener(changeListener: ConnectionChangeListener) {
    this.changeListener = changeListener
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ConnectionViewHolder {
    val holder = ConnectionViewHolder.create(viewGroup)

    holder.onOverflow {
      val adapterPosition = holder.adapterPosition
      val settings = getItem(adapterPosition)
      showPopup(settings, it)
    }

    holder.onClick {
      val adapterPosition = holder.adapterPosition
      val settings = getItem(adapterPosition)
      changeListener?.onDefault(settings)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
    val entity = getItem(holder.adapterPosition)
    holder.bind(entity, default?.id ?: -1)
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

  private var default: ConnectionSettingsEntity? = null

  fun setDefault(default: ConnectionSettingsEntity?) {
    this.default = default
  }

  class ConnectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val hostname: TextView by bindView(R.id.connection_settings__hostname_and_port)
    private val computerName: TextView by bindView(R.id.connection_settings__name)
    private val defaultSettings: ImageView by bindView(R.id.connection_settings__default_indicator)
    private val overflow: View by bindView(R.id.connection_settings__overflow)

    fun bind(entity: ConnectionSettingsEntity, selectionId: Long) {
      computerName.text = entity.name
      hostname.text = "${entity.address} : ${entity.port}"

      defaultSettings.isVisible = entity.id == selectionId
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
        val view = inflater.inflate(R.layout.listitem_settings, parent, false)
        return ConnectionViewHolder(view)
      }
    }
  }

  interface ConnectionChangeListener {
    fun onDelete(settings: ConnectionSettingsEntity)

    fun onEdit(settings: ConnectionSettingsEntity)

    fun onDefault(settings: ConnectionSettingsEntity)
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ConnectionSettingsEntity>() {
      override fun areItemsTheSame(
        oldItem: ConnectionSettingsEntity,
        newItem: ConnectionSettingsEntity
      ): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(
        oldItem: ConnectionSettingsEntity,
        newItem: ConnectionSettingsEntity
      ): Boolean {
        return oldItem == newItem
      }
    }
  }
}