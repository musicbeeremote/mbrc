package com.kelsos.mbrc.mappers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.dto.playlist.PlaylistDto;
import java.util.List;

public class PlaylistMapper {

  public static Playlist map(PlaylistDto playlistDto) {
    Playlist playlist = new Playlist();
    playlist.setId(playlistDto.getId());
    playlist.setName(playlistDto.getName());
    playlist.setPath(playlistDto.getPath());
    playlist.setReadOnly(playlistDto.getReadOnly());
    playlist.setTracks(playlistDto.getTracks());
    return playlist;
  }

  public static List<Playlist> map (List<PlaylistDto> collection) {
    return Stream.of(collection).map(PlaylistMapper::map).collect(Collectors.toList());
  }
}
