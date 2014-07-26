package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.dao.Album;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.Genre;
import com.kelsos.mbrc.dao.Track;
import com.kelsos.mbrc.data.SyncHandler;
import com.kelsos.mbrc.data.dbdata.Cover;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

public class HandleLibrarySync implements ICommand {
    private SyncHandler handler;

    @Inject public HandleLibrarySync(SyncHandler handler) {
        this.handler = handler;
    }

    @Override public void execute(IEvent e) {
        JsonNode node = (JsonNode) e.getData();
        String type = node.path("type").asText();

        if (type.equals("cover")) {
            int limit = node.path("limit").asInt(5);
            int offset = node.path("offset").asInt(0);
            int total = node.path("total").asInt(0);

            ArrayNode coverNode = (ArrayNode) node.path("data");
            final int size = coverNode.size();
            List<Cover> list = new ArrayList<>();

            for(int i = 0; i < size; i++) {
                JsonNode jNode = coverNode.get(i);
                String image = jNode.path("image").asText();
                String hash = jNode.path("coverhash").asText();
                String albumId = jNode.path("album_id").asText();
                handler.updateCover(image, hash);
                list.add(new Cover(albumId, hash));
            }

            handler.setCovers(list);

            if (offset < total) {
                handler.requestNextBatch(total,offset,limit);
            }

        } else if (type.equals("meta")) {
            int limit = node.path("limit").asInt(50);
            int offset = node.path("offset").asInt(0);
            int total = node.path("total").asInt(0);

            ArrayNode trackNode = (ArrayNode) node.path("data");
            List<Track> list = new ArrayList<>();
            final int size = trackNode.size();
            for (int i = 0; i < size; i++) {
                JsonNode jNode = trackNode.get(i);
                Track track = new Track();
                track.setHash(jNode.path("hash").asText());
                track.setTitle(jNode.path("title").asText());
                Genre genre = new Genre();
                genre.setGenreName(jNode.path("genre").asText());
                track.setGenre(genre);
                Album album = new Album();
                album.setAlbumName(jNode.path("album").asText());
                Artist artist = new Artist();
                artist.setArtistName(jNode.path("artist").asText());
                Artist albumArtist = new Artist();
                albumArtist.setArtistName(jNode.path("album_artist").asText());
                album.setArtist(albumArtist);
                track.setArtist(artist);
                track.setAlbumArtist(albumArtist);
                track.setYear(jNode.path("year").asText());
                track.setTrackNo(jNode.path("track_no").asInt());
                list.add(track);
            }

            handler.processBatch(list);
            handler.getNextBatch(total, offset, limit);
        }
    }
}
