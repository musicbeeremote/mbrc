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
import com.kelsos.mbrc.model.GenreEntry;

import java.util.ArrayList;

public class GenreEntryAdapter extends ArrayAdapter<GenreEntry> {
    private Context mContext;
    private int mResource;
    private ArrayList<GenreEntry> mData;
    private Typeface robotoLight;

    public GenreEntryAdapter(Context context, int resource, ArrayList<GenreEntry> objects) {
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
            holder.title = (TextView) row.findViewById(R.id.line_one);
            holder.title.setTypeface(robotoLight);

            holder.indicator = (LinearLayout) row.findViewById(R.id.ui_item_context_indicator);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        GenreEntry entry = mData.get(position);
        holder.title.setText(entry.getName());

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
        TextView title;
        LinearLayout indicator;
    }
}
