package com.kelsos.mbrc.interfaces;

import android.view.View;
import android.widget.ListView;

public interface IDrag {

    void onStartDrag(View item);

    void onDrag(int x, int y, ListView listView);

    void onStopDrag(View item);

    void onDrop(int from, int to);

    void onRemove(int position);

}
