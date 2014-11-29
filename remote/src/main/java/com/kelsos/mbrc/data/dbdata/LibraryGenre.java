package com.kelsos.mbrc.data.dbdata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = LibraryGenre.TABLE_NAME)
public class LibraryGenre {

    public static final String TABLE_NAME = "genres";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String name;

    public LibraryGenre() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
