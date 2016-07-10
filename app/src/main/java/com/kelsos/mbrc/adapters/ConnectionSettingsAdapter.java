package com.kelsos.mbrc.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectionSettingsAdapter
    extends RecyclerView.Adapter<ConnectionSettingsAdapter.ConnectionViewHolder>
    implements FlowCursorList.OnCursorRefreshListener<ConnectionSettings> {
  private FlowQueryList<ConnectionSettings> data;
  private long selectionId;
  private ConnectionChangeListener changeListener;

  public ConnectionSettingsAdapter() {
    data = SQLite.select().from(ConnectionSettings.class).flowQueryList();
    data.addOnCursorRefreshListener(this);
    setHasStableIds(true);
  }

  public void setSelectionId(long selectionId) {
    this.selectionId = selectionId;
    notifyDataSetChanged();
  }

  public void setChangeListener(ConnectionChangeListener changeListener) {
    this.changeListener = changeListener;
  }

  @Override
  public long getItemId(int position) {
    return data.get(0).getId();
  }

  @Override
  public ConnectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    final LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    final View view = inflater.inflate(R.layout.ui_list_connection_settings, viewGroup, false);
    final ConnectionViewHolder holder = new ConnectionViewHolder(view);

    holder.overflow.setOnClickListener(v -> {
      final int adapterPosition = holder.getAdapterPosition();
      final ConnectionSettings settings = data.get(adapterPosition);
      showPopup(settings, v);
    });

    holder.itemView.setOnClickListener(v -> {
      if (changeListener == null) {
        return;
      }
      final int adapterPosition = holder.getAdapterPosition();
      final ConnectionSettings settings = data.get(adapterPosition);
      changeListener.onDefault(settings);
    });
    return holder;
  }

  @Override
  public void onBindViewHolder(ConnectionViewHolder holder, final int position) {
    final ConnectionSettings settings = data.get(position);
    holder.computerName.setText(settings.getName());
    holder.hostname.setText(settings.getAddress());
    holder.portNum.setText(String.valueOf(settings.getPort()));

    if (settings.getId() == selectionId) {
      int grey = ContextCompat.getColor(holder.itemView.getContext(), R.color.button_dark);
      holder.defaultSettings.setImageResource(R.drawable.ic_check_black_24dp);
      holder.defaultSettings.setColorFilter(grey);
    }
  }

  private void showPopup(ConnectionSettings settings, View v) {
    final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
    popupMenu.getMenuInflater().inflate(R.menu.connection_popup, popupMenu.getMenu());
    popupMenu.setOnMenuItemClickListener(menuItem -> {
      if (changeListener == null) {
        return false;
      }

      switch (menuItem.getItemId()) {
        case R.id.connection_default:
          changeListener.onDefault(settings);
          break;
        case R.id.connection_edit:
          changeListener.onEdit(settings);
          break;
        case R.id.connection_delete:
          changeListener.onDelete(settings);
          break;
        default:
          break;
      }
      return true;
    });
    popupMenu.show();
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  public void refresh() {
    data.refreshAsync();
  }

  @Override
  public void onCursorRefreshed(FlowCursorList<ConnectionSettings> cursorList) {
    notifyDataSetChanged();
  }

  class ConnectionViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.cs_list_host)
    TextView hostname;
    @BindView(R.id.cs_list_port)
    TextView portNum;
    @BindView(R.id.cs_list_name)
    TextView computerName;
    @BindView(R.id.cs_list_default)
    ImageView defaultSettings;
    @BindView(R.id.cs_list_overflow)
    View overflow;

    ConnectionViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public interface ConnectionChangeListener {
    void onDelete(ConnectionSettings settings);

    void onEdit(ConnectionSettings settings);

    void onDefault(ConnectionSettings settings);
  }
}
