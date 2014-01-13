package com.kelsos.mbrc.adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LyricsAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int mResource;
    private List<String> mData;
    private Typeface robotoLight;

    public LyricsAdapter(Context context, int resource, List<String> objects) {
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
            holder.title = (TextView) row.findViewById(android.R.id.text1);
            holder.title.setTypeface(robotoLight);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        String str = mData.get(position);
        holder.title.setText(str);

        return row;
    }

    static class Holder {
        TextView title;
    }

}