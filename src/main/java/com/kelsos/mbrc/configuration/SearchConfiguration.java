package com.kelsos.mbrc.configuration;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.visual.ShowAlbumSearchResults;
import com.kelsos.mbrc.commands.visual.ShowArtistSearchResults;
import com.kelsos.mbrc.commands.visual.ShowGenreSearchResults;
import com.kelsos.mbrc.commands.visual.ShowTrackSearchResults;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.others.Protocol;

public class SearchConfiguration {

    @Inject
    public static void register(Controller controller){
        controller.register(Protocol.LibrarySearchArtist, ShowArtistSearchResults.class);
        controller.register(Protocol.LibrarySearchAlbum, ShowAlbumSearchResults.class);
        controller.register(Protocol.LibrarySearchGenre, ShowGenreSearchResults.class);
        controller.register(Protocol.LibrarySearchTitle, ShowTrackSearchResults.class);
    }

    @Inject
    public static void unRegister(Controller controller){
        controller.unregister(Protocol.LibrarySearchArtist, ShowArtistSearchResults.class);
        controller.unregister(Protocol.LibrarySearchAlbum, ShowAlbumSearchResults.class);
        controller.unregister(Protocol.LibrarySearchGenre, ShowGenreSearchResults.class);
        controller.unregister(Protocol.LibrarySearchTitle, ShowTrackSearchResults.class);
    }
}
