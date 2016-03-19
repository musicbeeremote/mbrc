package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.domain.DeviceSettings;
import java.util.ArrayList;
import java.util.List;

public class DeviceManagerAdapter extends RecyclerView.Adapter<DeviceManagerAdapter.ConnectionViewHolder> {

  private final LayoutInflater inflater;
  private List<DeviceSettings> data;
  private int defaultIndex;
  private DeviceActionListener listener;

  @Inject public DeviceManagerAdapter(Context context) {
    this.data = new ArrayList<>();
    this.defaultIndex = 0;
    this.inflater = LayoutInflater.from(context);
  }

  public void setDefault(int index) {
    this.defaultIndex = index;
  }

  @Override public ConnectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View viewItem = inflater.inflate(R.layout.ui_list_connection_settings, viewGroup, false);
    ConnectionViewHolder holder = new ConnectionViewHolder(viewItem);
    holder.overflow.setOnClickListener(v -> {
      final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
      popupMenu.getMenuInflater().inflate(R.menu.connection_popup, popupMenu.getMenu());
      popupMenu.setOnMenuItemClickListener(menuItem -> {
        if (listener == null) {
          return false;
        }

        DeviceSettings settings = data.get(holder.getAdapterPosition());

        switch (menuItem.getItemId()) {
          case R.id.connection_default:
            listener.onDefault(settings);
            break;
          case R.id.connection_edit:
            listener.onEdit(settings);
            break;
          case R.id.connection_delete:
            listener.onDelete(settings);
            break;
          default:
            break;
        }
        return true;
      });
      popupMenu.show();
    });

    holder.itemView.setOnClickListener(v -> {
      if (listener == null) {
        return;
      }

      DeviceSettings settings = data.get(holder.getAdapterPosition());
      listener.onDefault(settings);
    });
    return holder;
  }

  @Override public void onBindViewHolder(ConnectionViewHolder holder, final int position) {
    final DeviceSettings settings = data.get(position);
    holder.computerName.setText(settings.getName());
    holder.hostname.setText(settings.getAddress());
    holder.portNum.setText(String.format("%d / %d", settings.getPort(), settings.getHttp()));

    if (position == defaultIndex) {
      holder.defaultSettings.setImageResource(R.drawable.ic_check_black_24dp);
      Context context = holder.itemView.getContext();
      holder.defaultSettings.setColorFilter(ContextCompat.getColor(context, R.color.white));
    }
  }

  @Override public int getItemCount() {
    return data.size();
  }

  public void setDeviceActionListener(DeviceActionListener listener) {
    this.listener = listener;
  }

  public interface DeviceActionListener {
    void onDelete(DeviceSettings settings);

    void onDefault(DeviceSettings settings);

    void onEdit(DeviceSettings settings);
  }

  public class ConnectionViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.cs_list_host) TextView hostname;
    @Bind(R.id.cs_list_port) TextView portNum;
    @Bind(R.id.cs_list_name) TextView computerName;
    @Bind(R.id.cs_list_default) ImageView defaultSettings;
    @Bind(R.id.cs_list_overflow) View overflow;

    public ConnectionViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
