package com.kelsos.mbrc.ui.connectionmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isGone
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.ListitemSettingsBinding
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import com.kelsos.mbrc.ui.connectionmanager.ConnectionAdapter.ConnectionViewHolder

class ConnectionAdapter : PagingDataAdapter<ConnectionSettings, ConnectionViewHolder>(
  CONNECTION_COMPARATOR
) {
  private var changeListener: ConnectionChangeListener? = null

  fun setChangeListener(changeListener: ConnectionChangeListener) {
    this.changeListener = changeListener
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ConnectionViewHolder {
    val holder = ConnectionViewHolder.create(viewGroup)

    holder.onOverflow { view ->
      val adapterPosition = holder.bindingAdapterPosition
      getItem(adapterPosition)?.let {
        showPopup(it, view)
      }
    }

    holder.onClick {
      val adapterPosition = holder.bindingAdapterPosition
      getItem(adapterPosition)?.let {
        changeListener?.onDefault(it)
      }
    }
    return holder
  }

  override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
    getItem(holder.bindingAdapterPosition)?.let {
      holder.bind(it)
    }
  }

  private fun showPopup(settings: ConnectionSettings, v: View) {
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
    binding: ListitemSettingsBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    private val hostname: TextView = binding.connectionSettingsHostnameAndPort
    private val computerName: TextView = binding.connectionSettingsName
    private val defaultSettings: ImageView = binding.connectionSettingsDefaultIndicator
    private val overflow: View = binding.connectionSettingsOverflow

    fun bind(settings: ConnectionSettings) {
      computerName.text = settings.name
      hostname.text = "${settings.address} : ${settings.port}"
      defaultSettings.isGone = !settings.isDefault
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
        val binding = ListitemSettingsBinding.inflate(inflater, parent, false)
        return ConnectionViewHolder(binding)
      }
    }
  }

  interface ConnectionChangeListener {
    fun onDelete(settings: ConnectionSettings)

    fun onEdit(settings: ConnectionSettings)

    fun onDefault(settings: ConnectionSettings)
  }

  companion object {
    val CONNECTION_COMPARATOR = object : DiffUtil.ItemCallback<ConnectionSettings>() {
      override fun areItemsTheSame(
        oldItem: ConnectionSettings,
        newItem: ConnectionSettings
      ): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(
        oldItem: ConnectionSettings,
        newItem: ConnectionSettings
      ): Boolean {
        return oldItem.address == newItem.address &&
          oldItem.name == newItem.name &&
          oldItem.port == newItem.port
      }
    }
  }
}
