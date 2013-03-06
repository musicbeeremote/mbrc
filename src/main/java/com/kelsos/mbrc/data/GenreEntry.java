package com.kelsos.mbrc.data;

public class GenreEntry {
    private int count;
    private String name;

    public GenreEntry (String name, int count) {
        this.name = name;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public String getName(){
        return name;
    }
}
