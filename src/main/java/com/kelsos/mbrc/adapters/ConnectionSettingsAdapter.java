package com.kelsos.mbrc.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;

import java.util.ArrayList;

public class ConnectionSettingsAdapter extends ArrayAdapter<ConnectionSettings> {

    private ArrayList<ConnectionSettings> mData;
    private Context mContext;
    private int mResource;
    private Typeface robotoLight;
    private int defaultIndex;

    public ConnectionSettingsAdapter(Context context, int textViewResourceId, ArrayList<ConnectionSettings> objects) {
        super(context, textViewResourceId, objects);
        this.mData = objects;
        this.mContext = context;
        this.mResource = textViewResourceId;
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_light.ttf");
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        ConnectionSettings current = mData.get(position);
        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
            row = layoutInflater.inflate(mResource, parent, false);

            holder = new Holder();
            holder.hostname = (TextView) row.findViewById(R.id.cs_list_host);
            holder.portNum = (TextView) row.findViewById(R.id.cs_list_port);
            holder.computerName = (TextView) row.findViewById(R.id.cs_list_name);
            holder.defaultIcon = (ImageView) row.findViewById(R.id.cs_list_default);
            holder.computerName.setTypeface(robotoLight);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        holder.hostname.setText(current.getAddress());
        holder.portNum.setText(Integer.toString(current.getPort()));
        holder.computerName.setText(current.getName());
        if (position == defaultIndex) {
            holder.defaultIcon.setImageResource(R.drawable.ic_selection_default);
        }
        return row;
    }

    public void setDefaultIndex(int defaultIndex) {
        this.defaultIndex = defaultIndex;
    }


    static class Holder {
        TextView hostname;
        TextView portNum;
        TextView computerName;
        ImageView defaultIcon;
    }
}
