package com.kelsos.mbrc;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class Generator {

    public static final String NAME = "name";

    public static void main(String[] args) throws Exception{
        Schema schema = new Schema(1, "com.kelsos.mbrc.dao");


        Entity cover = schema.addEntity("Cover");
        cover.addIdProperty();
        cover.addStringProperty("hash");

        Entity genre = schema.addEntity("Genre");
        genre.addIdProperty();
        genre.addStringProperty(NAME);

        Entity artist = schema.addEntity("Artist");
        artist.addIdProperty();
        artist.addStringProperty(NAME);

        Entity album = schema.addEntity("Album");
        album.addIdProperty();
        album.addStringProperty(NAME);

        Property artistId = album.addLongProperty("artistId").getProperty();
        album.addToOne(artist, artistId);
        album.addStringProperty("albumId");

        Property coverId = album.addLongProperty("coverId").getProperty();
        album.addToOne(cover, coverId);


        Entity track = schema.addEntity("Track");
        track.addIdProperty();
        track.addStringProperty("title");
        track.addIntProperty("index");

        Property genreId = track.addLongProperty("genreId").getProperty();
        track.addToOne(genre, genreId);

        Property trackArtistId = track.addLongProperty("artistId").getProperty();
        track.addToOne(artist, trackArtistId);
        Property albumArtistId = track.addLongProperty("albumArtistId").getProperty();
        track.addToOne(artist, albumArtistId, "albumArtist");
        Property albumId = track.addLongProperty("albumId").getProperty();
        track.addToOne(album, albumId);
        track.addStringProperty("year");
        track.addStringProperty("path");


        new DaoGenerator().generateAll(schema, "../remote/src-gen/");
    }
}