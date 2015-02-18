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
import com.kelsos.mbrc.data.NavigationEntry;

import java.util.ArrayList;

public class DrawerAdapter extends ArrayAdapter<NavigationEntry> {
    private Context mContext;
    private int mResource;
    private ArrayList<NavigationEntry> mData;
    private Typeface robotoMedium;

    public DrawerAdapter(Context context, int resource, ArrayList<NavigationEntry> objects) {
        super(context, resource, objects);
        this.mResource = resource;
        this.mContext = context;
        this.mData = objects;
        robotoMedium = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_medium.ttf");
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        NavigationEntry current = mData.get(position);
        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
            row = layoutInflater.inflate(mResource, parent, false);

            holder = new Holder();
            holder.rowText = (TextView) row.findViewById(R.id.rowText);
            holder.rowText.setTypeface(robotoMedium);
            holder.rowIcon = (ImageView) row.findViewById(R.id.rowIcon);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        holder.rowText.setText(current.getLabel());
        holder.rowIcon.setImageResource(current.getDrawableId());

        return row;
    }

    static class Holder {
        TextView rowText;
        ImageView rowIcon;
    }
}