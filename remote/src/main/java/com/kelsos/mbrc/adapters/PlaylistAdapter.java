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
import com.kelsos.mbrc.data.dbdata.Playlist;

import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    private final int mResource;
    private final Context mContext;
    private final List<Playlist> availablePlaylists;
    private final Typeface robotoLight;

    public PlaylistAdapter(Context context, int resource, List<Playlist> objects) {
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
            holder.lineOne = (TextView) row.findViewById(R.id.line_one);
            holder.lineTwo = (TextView) row.findViewById(R.id.line_two);
            holder.lineOne.setTypeface(robotoLight);
            holder.lineTwo.setTypeface(robotoLight);

            row.setTag(holder);
        } else {
            holder = (TrackHolder) row.getTag();
        }

        Playlist list = availablePlaylists.get(position);
        holder.lineOne.setText(list.getName());
        holder.lineTwo.setText(Integer.toString(list.getTracks()));

        return row;
    }

    static class TrackHolder {
        TextView lineOne;
        TextView lineTwo;
    }
}
