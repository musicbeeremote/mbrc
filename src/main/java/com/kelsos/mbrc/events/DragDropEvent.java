package com.kelsos.mbrc.events;

import android.view.View;

public class DragDropEvent {
    private Drag type;
    private View item;
    public DragDropEvent(Drag type, View item){
        this.type = type;
        this.item = item;
    }

    public Drag getType(){
        return type;
    }

    public View getItem(){
        return item;
    }
}

