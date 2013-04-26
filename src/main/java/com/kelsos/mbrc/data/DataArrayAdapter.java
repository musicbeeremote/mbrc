package com.kelsos.mbrc.data;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.kelsos.mbrc.R;

import java.util.ArrayList;

public class DataArrayAdapter extends ArrayAdapter<ArtistEntry> {

    private Context mContext;
    private int mResource;
    private ArrayList<ArtistEntry> mData;

    public DataArrayAdapter(Context context, int resource, ArrayList<ArtistEntry> objects) {
        super(context, resource, objects);
        this.mResource = resource;
        this.mContext = context;
        this.mData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
            row = layoutInflater.inflate(mResource, parent, false);

            holder = new Holder();
            holder.title = (TextView) row.findViewById(R.id.trackTitle);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        ArtistEntry str = mData.get(position);
        holder.title.setText(str.getArtist());

        //holder.trackPlaying.setOnClickListener(showContextMenu);

        return row;
    }

    private final View.OnClickListener showContextMenu = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            view.showContextMenu();
        }
    };

    static class Holder {
        TextView title;
        //ImageView trackPlaying;

    }

}
