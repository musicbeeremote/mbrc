package com.kelsos.mbrc.adapters;

import android.support.v4.app.FragmentManager;
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
import com.kelsos.mbrc.events.ui.ChangeSettings;
import com.kelsos.mbrc.ui.activities.ConnectionManagerActivity;
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment;
import com.squareup.otto.Bus;
import java.util.List;

public class ConnectionSettingsAdapter
    extends RecyclerView.Adapter<ConnectionSettingsAdapter.ConnectionViewHolder> {

  private List<ConnectionSettings> mData;
  private Bus bus;
  private int defaultIndex;

  public ConnectionSettingsAdapter(List<ConnectionSettings> objects, Bus bus) {
    this.mData = objects;
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
    final ConnectionSettings settings = mData.get(position);
    connectionViewHolder.computerName.setText(settings.getName());
    connectionViewHolder.hostname.setText(settings.getAddress());
    connectionViewHolder.portNum.setText(String.format("%d", settings.getPort()));

    if (settings.getIndex() == defaultIndex) {
      connectionViewHolder.defaultSettings.setImageResource(R.drawable.ic_selection_default);
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
        v -> bus.post(new ChangeSettings(SettingsAction.DEFAULT, mData.get(position))));
  }

  @Override public int getItemCount() {
    return mData.size();
  }

  public class ConnectionViewHolder extends RecyclerView.ViewHolder {

    TextView hostname;
    TextView portNum;
    TextView computerName;
    ImageView defaultSettings;
    View overflow;

    public ConnectionViewHolder(View itemView) {
      super(itemView);
      hostname = (TextView) itemView.findViewById(R.id.cs_list_host);
      portNum = (TextView) itemView.findViewById(R.id.cs_list_port);
      computerName = (TextView) itemView.findViewById(R.id.cs_list_name);
      defaultSettings = (ImageView) itemView.findViewById(R.id.cs_list_default);
      overflow = itemView.findViewById(R.id.cs_list_overflow);
    }
  }
}
