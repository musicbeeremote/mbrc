package com.kelsos.mbrc.ui.connection_manager

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.data.dao.ConnectionSettings
import java.util.*
import javax.inject.Inject

class ConnectionManagerAdapter
@Inject constructor(context: Context) : RecyclerView.Adapter<ConnectionManagerAdapter.ConnectionViewHolder>() {

  private val inflater: LayoutInflater
  private val data: MutableList<ConnectionSettings>
  private var defaultIndex: Int = 0
  private var listener: DeviceActionListener? = null

  init {
    this.data = ArrayList<ConnectionSettings>()
    this.defaultIndex = 0
    this.inflater = LayoutInflater.from(context)
  }

  fun setDefault(index: Int) {
    this.defaultIndex = index
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ConnectionViewHolder {
    val viewItem = inflater.inflate(R.layout.ui_list_connection_settings, viewGroup, false)
    val holder = ConnectionViewHolder(viewItem)
    holder.overflow.setOnClickListener { v ->
      val popupMenu = PopupMenu(v.context, v)
      popupMenu.menuInflater.inflate(R.menu.connection_popup, popupMenu.menu)
      popupMenu.setOnMenuItemClickListener listener@{ menuItem ->
        if (listener == null) {
          return@listener false
        }

        val settings = data[holder.adapterPosition]

        when (menuItem.itemId) {
          R.id.connection_default -> listener!!.onDefault(settings)
          R.id.connection_edit -> listener!!.onEdit(settings)
          R.id.connection_delete -> listener!!.onDelete(settings)
          else -> {
          }
        }
        true
      }
      popupMenu.show()
    }

    holder.itemView.setOnClickListener listener@{ v ->
      if (listener == null) {
        return@listener
      }

      val settings = data[holder.adapterPosition]
      listener!!.onDefault(settings)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
    val settings = data[position]
    holder.computerName.text = settings.name
    holder.hostname.text = settings.address
    holder.portNum.text = settings.port.toString()

    if (position == defaultIndex) {
      holder.defaultSettings.setImageResource(R.drawable.ic_check_black_24dp)
      val context = holder.itemView.context
      holder.defaultSettings.setColorFilter(ContextCompat.getColor(context, R.color.white))
    }
  }

  override fun getItemCount(): Int {
    return data.size
  }

  fun setDeviceActionListener(listener: DeviceActionListener) {
    this.listener = listener
  }

  fun updateDevices(list: List<ConnectionSettings>) {
    data.clear()
    data.addAll(list)
    notifyDataSetChanged()
  }

  interface DeviceActionListener {
    fun onDelete(settings: ConnectionSettings)

    fun onDefault(settings: ConnectionSettings)

    fun onEdit(settings: ConnectionSettings)
  }

  inner class ConnectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.cs_list_host) lateinit internal var hostname: TextView
    @BindView(R.id.cs_list_port) lateinit internal var portNum: TextView
    @BindView(R.id.cs_list_name) lateinit internal var computerName: TextView
    @BindView(R.id.cs_list_default) lateinit internal var defaultSettings: ImageView
    @BindView(R.id.cs_list_overflow) lateinit internal var overflow: View

    init {
      ButterKnife.bind(this, itemView)
    }
  }
}
