package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.GenreEntry;

import java.util.ArrayList;

public class GenreSearchResults {
    private ArrayList<GenreEntry> list;

    public GenreSearchResults(ArrayList<GenreEntry> list){
        this.list = list;
    }

    public ArrayList<GenreEntry> getList() {
        return this.list;
    }
}
