package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.kelsos.mbrc.dao");

        Entity album = schema.addEntity("Album");
        album.addIdProperty();
        album.addStringProperty("AlbumName");
        album.addStringProperty("CoverHash");

        Entity artist = schema.addEntity("Artist");
        artist.addIdProperty();
        artist.addStringProperty("ArtistName");

        Entity genre = schema.addEntity("Genre");
        genre.addIdProperty();
        genre.addStringProperty("GenreName");

        Entity track = schema.addEntity("Track");
        track.addIdProperty();
        track.addStringProperty("Hash");
        track.addStringProperty("Title");
        Property trackArtistId = track.addLongProperty("ArtistId").getProperty();
        track.addToOne(artist,trackArtistId, "Artist");
        Property trackAlbumArtistId = track.addLongProperty("AlbumArtistId")
                .getProperty();
        track.addToOne(artist, trackAlbumArtistId, "AlbumArtist");
        Property genreId = track.addLongProperty("GenreId").getProperty();
        track.addToOne(genre, genreId);
        track.addStringProperty("Year");
        track.addIntProperty("TrackNo");
        track.addDateProperty("Updated");

        Entity queueTrack = schema.addEntity("QueueTrack");
        queueTrack.addIdProperty();
        queueTrack.addStringProperty("Artist");
        queueTrack.addStringProperty("Title");
        queueTrack.addStringProperty("Src");
        queueTrack.addIntProperty("Index");

        Entity playlist = schema.addEntity("Playlist");
        playlist.addIdProperty();
        playlist.addStringProperty("Name");
        playlist.addStringProperty("Hash");

        Entity playlistTrack = schema.addEntity("PlaylistTrack");
        playlistTrack.addIdProperty();
        playlistTrack.addStringProperty("Artist");
        playlistTrack.addStringProperty("Title");
        playlistTrack.addStringProperty("Hash");
        playlistTrack.addIntProperty("Index");
        Property playlistId = playlistTrack.addLongProperty("PlaylistId").getProperty();
        playlistTrack.addToOne(playlist, playlistId);

        Property albumArtistId = album.addLongProperty("ArtistId").getProperty();
        album.addToOne(artist, albumArtistId);

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
