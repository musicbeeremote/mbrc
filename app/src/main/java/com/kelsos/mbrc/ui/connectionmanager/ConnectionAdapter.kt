package com.kelsos.mbrc.ui.connectionmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.ListitemSettingsBinding
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

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
    holder.bind(entity, selectionId)
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

  class ConnectionViewHolder(binding: ListitemSettingsBinding) : RecyclerView.ViewHolder(
    binding.root
  ) {
    private val hostname: TextView = binding.connectionSettingsHostnameAndPort
    private val computerName: TextView = binding.connectionSettingsName
    private val defaultSettings: ImageView = binding.connectionSettingsDefaultIndicator
    private val overflow: ImageView = binding.connectionSettingsOverflow

    fun bind(entity: ConnectionSettingsEntity, selectionId: Long) {
      computerName.text = entity.name
      hostname.text = itemView.context.getString(
        R.string.connection_manager__host,
        entity.address,
        entity.port
      )

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
        val binding: ListitemSettingsBinding = DataBindingUtil.inflate(
          inflater,
          R.layout.listitem_settings,
          parent,
          false
        )
        return ConnectionViewHolder(binding)
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
