package com.kelsos.mbrc.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    private final int mResource;
    private final Context mContext;
    private final ArrayList<Playlist> availablePlaylists;
    private final Typeface robotoLight;

    public PlaylistAdapter(Context context, int resource, ArrayList<Playlist> objects) {
        super(context, resource, objects);
        this.mResource = resource;
        this.mContext = context;
        this.availablePlaylists = objects;
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_light.ttf");
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TrackHolder holder;

        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
            row = layoutInflater.inflate(mResource, parent, false);

            holder = new TrackHolder();
            holder.line_one = (TextView) row.findViewById(R.id.line_one);
            holder.line_two = (TextView) row.findViewById(R.id.line_two);
            holder.line_one.setTypeface(robotoLight);
            holder.line_two.setTypeface(robotoLight);

            row.setTag(holder);
        } else {
            holder = (TrackHolder) row.getTag();
        }

        Playlist list = availablePlaylists.get(position);
        holder.line_one.setText(list.getName());
        holder.line_two.setText(Integer.toString(list.getCount()));

        return row;
    }

    static class TrackHolder {
        TextView line_one;
        TextView line_two;
    }
}
