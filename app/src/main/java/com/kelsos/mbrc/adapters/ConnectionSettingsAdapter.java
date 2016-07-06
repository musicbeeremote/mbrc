package com.kelsos.mbrc.adapters;

import android.support.v4.app.FragmentManager;
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
import com.kelsos.mbrc.enums.SettingsAction;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.ChangeSettings;
import com.kelsos.mbrc.ui.activities.ConnectionManagerActivity;
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectionSettingsAdapter
    extends RecyclerView.Adapter<ConnectionSettingsAdapter.ConnectionViewHolder> {
  private List<ConnectionSettings> data;
  private RxBus bus;
  private int defaultIndex;

  public ConnectionSettingsAdapter(List<ConnectionSettings> objects, RxBus bus) {
    this.data = objects;
    this.bus = bus;
  }

  public void setDefaultIndex(int defaultIndex) {
    this.defaultIndex = defaultIndex;
  }

  @Override public ConnectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View viewItem = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.ui_list_connection_settings, viewGroup, false);
    return new ConnectionViewHolder(viewItem);
  }

  @Override
  public void onBindViewHolder(ConnectionViewHolder connectionViewHolder, final int position) {
    final ConnectionSettings settings = data.get(position);
    connectionViewHolder.computerName.setText(settings.getName());
    connectionViewHolder.hostname.setText(settings.getAddress());
    connectionViewHolder.portNum.setText(String.valueOf(settings.getPort()));

    if (settings.getIndex() == defaultIndex) {
      int grey = ContextCompat.getColor(connectionViewHolder.itemView.getContext(), R.color.button_dark);
      connectionViewHolder.defaultSettings.setImageResource(R.drawable.ic_check_black_24dp);
      connectionViewHolder.defaultSettings.setColorFilter(grey);
    }

    connectionViewHolder.overflow.setOnClickListener(v -> {
      final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
      popupMenu.getMenuInflater().inflate(R.menu.connection_popup, popupMenu.getMenu());
      popupMenu.setOnMenuItemClickListener(menuItem -> {
        switch (menuItem.getItemId()) {
          case R.id.connection_default:
            bus.post(new ChangeSettings(SettingsAction.DEFAULT, settings));
            break;
          case R.id.connection_edit:
            SettingsDialogFragment settingsDialog =
                SettingsDialogFragment.newInstance(settings);
            FragmentManager fragmentManager =
                ((ConnectionManagerActivity) v.getContext()).getSupportFragmentManager();
            settingsDialog.show(fragmentManager, "settings_dialog");
            break;
          case R.id.connection_delete:
            bus.post(new ChangeSettings(SettingsAction.DELETE, settings));
            break;
          default:
            break;
        }
        return false;
      });
      popupMenu.show();
    });

    connectionViewHolder.itemView.setOnClickListener(
        v -> bus.post(new ChangeSettings(SettingsAction.DEFAULT, data.get(position))));
  }

  @Override public int getItemCount() {
    return data.size();
  }

  class ConnectionViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.cs_list_host) TextView hostname;
    @BindView(R.id.cs_list_port) TextView portNum;
    @BindView(R.id.cs_list_name) TextView computerName;
    @BindView(R.id.cs_list_default) ImageView defaultSettings;
    @BindView(R.id.cs_list_overflow) View overflow;

    ConnectionViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
