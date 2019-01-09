package com.kelsos.mbrc.ui.connectionmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.UiListConnectionSettingsBinding
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.ui.connectionmanager.ConnectionAdapter.ConnectionViewHolder

class ConnectionAdapter : ListAdapter<ConnectionSettingsEntity, ConnectionViewHolder>(
  DIFF_CALLBACK
) {

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
      val adapterPosition = holder.bindingAdapterPosition
      val settings = getItem(adapterPosition)
      showPopup(settings, it)
    }

    holder.onClick {
      val adapterPosition = holder.bindingAdapterPosition
      val settings = getItem(adapterPosition)
      changeListener?.onDefault(settings)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
    holder.bind(getItem(holder.bindingAdapterPosition), selectionId)
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

  class ConnectionViewHolder(
    binding: UiListConnectionSettingsBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    private val hostname: TextView = binding.connectionSettingsHostnameAndPort
    private val computerName: TextView = binding.connectionSettingsName
    private val defaultSettings: ImageView = binding.connectionSettingsDefaultIndicator
    private val overflow: View = binding.connectionSettingsOverflow

    fun bind(settings: ConnectionSettingsEntity, selectionId: Long) {
      computerName.text = settings.name
      hostname.text = "${settings.address} : ${settings.port}"
      defaultSettings.isGone = settings.id != selectionId
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
        val binding = UiListConnectionSettingsBinding.bind(view)
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
