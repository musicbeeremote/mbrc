package com.kelsos.mbrc.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;

import java.util.List;

public class ConnectionSettingsAdapter extends RecyclerView.Adapter<ConnectionSettingsAdapter.ConnectionViewHolder> {

    private List<ConnectionSettings> mData;
    private Typeface robotoLight;
    private int defaultIndex;

    public ConnectionSettingsAdapter(List<ConnectionSettings> objects) {
        this.mData = objects;
    }

	public void setDefaultIndex(int defaultIndex) {
        this.defaultIndex = defaultIndex;
    }

	@Override
	public ConnectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
		View viewItem = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.ui_list_connection_settings, viewGroup, false);
		return new ConnectionViewHolder(viewItem);

	}

	@Override
	public void onBindViewHolder(ConnectionViewHolder connectionViewHolder, int position) {
		final ConnectionSettings settings = mData.get(position);
		connectionViewHolder.computerName.setText(settings.getName());
		connectionViewHolder.hostname.setText(settings.getAddress());
		connectionViewHolder.portNum.setText(String.format("%d / %d", settings.getPort(), settings.getHttpPort()));
		if (settings.getIndex() == defaultIndex) {
			connectionViewHolder.defaultSettings.setImageResource(R.drawable.ic_selection_default);
		}
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}


	public static final class ConnectionViewHolder extends RecyclerView.ViewHolder {
        TextView hostname;
        TextView portNum;
        TextView computerName;
        ImageView defaultSettings;

		public ConnectionViewHolder(View itemView) {
			super(itemView);
			hostname = (TextView) itemView.findViewById(R.id.cs_list_host);
			portNum = (TextView) itemView.findViewById(R.id.cs_list_port);
			computerName = (TextView) itemView.findViewById(R.id.cs_list_name);
			defaultSettings = (ImageView) itemView.findViewById(R.id.cs_list_default);
			itemView.setOnClickListener(v -> {

            });
		}
	}
}
