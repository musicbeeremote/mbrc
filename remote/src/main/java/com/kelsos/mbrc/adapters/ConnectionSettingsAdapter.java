package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
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
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.SettingsAction;
import com.kelsos.mbrc.domain.ConnectionSettings;
import com.kelsos.mbrc.events.ui.ChangeSettings;
import com.kelsos.mbrc.ui.activities.ConnectionManagerActivity;
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment;
import com.kelsos.mbrc.utilities.RxBus;
import java.util.List;

public class ConnectionSettingsAdapter
    extends RecyclerView.Adapter<ConnectionSettingsAdapter.ConnectionViewHolder> {

  private List<ConnectionSettings> mData;
  private RxBus bus;
  private int defaultIndex;

  public ConnectionSettingsAdapter(List<ConnectionSettings> objects, RxBus bus) {
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
    connectionViewHolder.portNum.setText(String.format("%d / %d", settings.getPort(), settings.getHttp()));

    if (settings.getIndex() == defaultIndex) {
      connectionViewHolder.defaultSettings.setImageResource(R.drawable.ic_check_black_24dp);
      Context context = connectionViewHolder.itemView.getContext();
      connectionViewHolder.defaultSettings.setColorFilter(ContextCompat.getColor(context, R.color.white));
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
            SettingsDialogFragment settingsDialog = SettingsDialogFragment.newInstance(settings);
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
