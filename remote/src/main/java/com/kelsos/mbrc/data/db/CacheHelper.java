package com.kelsos.mbrc.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.kelsos.mbrc.data.dbdata.LibraryAlbum;
import com.kelsos.mbrc.data.dbdata.LibraryArtist;
import com.kelsos.mbrc.data.dbdata.LibraryGenre;
import com.kelsos.mbrc.data.dbdata.LibraryTrack;
import roboguice.util.Ln;

import java.sql.SQLException;

public class CacheHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "mb-library.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<LibraryGenre, Integer> genreDao;
    private Dao<LibraryArtist, Integer> artistDao;
    private Dao<LibraryAlbum, Integer> albumDao;
    private Dao<LibraryTrack, Integer> trackDao;

    public CacheHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            createTables(connectionSource);
        } catch (SQLException e) {
            Ln.d(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        try {
            TableUtils.dropTable(connectionSource, LibraryTrack.class, true);
            TableUtils.dropTable(connectionSource, LibraryAlbum.class, true);
            TableUtils.dropTable(connectionSource, LibraryArtist.class, true);
            TableUtils.dropTable(connectionSource, LibraryGenre.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Ln.d(e);
        }
    }

    private void createTables(ConnectionSource connectionSource) throws SQLException{
        TableUtils.createTable(connectionSource, LibraryGenre.class);
        TableUtils.createTable(connectionSource, LibraryArtist.class);
        TableUtils.createTable(connectionSource, LibraryAlbum.class);
        TableUtils.createTable(connectionSource, LibraryTrack.class);
    }


    public Dao<LibraryGenre, Integer> getGenreDao() throws SQLException {
        if (genreDao == null) {
            genreDao = getDao(LibraryGenre.class);
        }
        return genreDao;
    }

    public Dao<LibraryArtist, Integer> getArtistDao() throws SQLException {
        if (artistDao == null) {
            artistDao = getDao(LibraryArtist.class);
        }
        return artistDao;
    }

    public Dao<LibraryAlbum, Integer> getAlbumDao() throws SQLException {
        if (albumDao == null) {
            albumDao = getDao(LibraryAlbum.class);
        }
        return albumDao;
    }

    public Dao<LibraryTrack, Integer> getTrackDao() throws SQLException {
        if (trackDao == null) {
            trackDao = getDao(LibraryTrack.class);
        }

        return trackDao;
    }

    @Override
    public void close() {
        super.close();
        genreDao = null;
        trackDao = null;
        albumDao = null;
        artistDao = null;
    }
}
