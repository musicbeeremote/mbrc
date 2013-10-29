package com.kelsos.mbrc.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.model.AlbumEntry;

import java.util.ArrayList;

public class AlbumEntryAdapter extends ArrayAdapter<AlbumEntry> {
    private Context mContext;
    private int mResource;
    private ArrayList<AlbumEntry> mData;
    private Typeface robotoLight;

    public AlbumEntryAdapter(Context context, int resource, ArrayList<AlbumEntry> objects) {
        super(context, resource, objects);
        this.mResource = resource;
        this.mContext = context;
        this.mData = objects;
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_light.ttf");
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
            row = layoutInflater.inflate(mResource, parent, false);

            holder = new Holder();
            holder.album = (TextView) row.findViewById(R.id.line_one);
            holder.album.setTypeface(robotoLight);
            holder.artist = (TextView) row.findViewById(R.id.line_two);
            holder.artist.setTypeface(robotoLight);

            holder.indicator = (LinearLayout) row.findViewById(R.id.ui_item_context_indicator);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        AlbumEntry entry = mData.get(position);
        holder.album.setText(entry.getAlbum());
        holder.artist.setText(entry.getArtist());

        holder.indicator.setOnClickListener(showContextMenu);

        return row;
    }

    private final View.OnClickListener showContextMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            view.showContextMenu();
        }
    };

    static class Holder {
        TextView artist;
        TextView album;
        LinearLayout indicator;
    }
}
