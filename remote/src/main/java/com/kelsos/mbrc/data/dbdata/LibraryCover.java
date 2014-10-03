package com.kelsos.mbrc.data.dbdata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = LibraryCover.TABLE_NAME)
public class LibraryCover {

    public static final String TABLE_NAME = "covers";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String hash;

    private String base64;

    public LibraryCover() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}



