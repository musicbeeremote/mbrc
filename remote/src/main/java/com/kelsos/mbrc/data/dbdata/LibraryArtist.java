package com.kelsos.mbrc.data.dbdata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = LibraryArtist.TABLE_NAME)
public class LibraryArtist {
    public static final String TABLE_NAME = "artists";

    @DatabaseField(unique = true)
    private String name;

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String imageUrl;


    public LibraryArtist() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
