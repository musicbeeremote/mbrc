package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.GenreEntry;

import java.util.List;

public class GenreSearchResults {
    private List<GenreEntry> list;
    private boolean stored;

    public GenreSearchResults(List<GenreEntry> list, boolean stored) {
        this.list = list;
        this.stored = stored;
    }

    public List<GenreEntry> getList() {
        return this.list;
    }

    public boolean isStored() {
        return stored;
    }
}
