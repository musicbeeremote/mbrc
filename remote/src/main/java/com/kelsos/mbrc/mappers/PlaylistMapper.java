package com.kelsos.mbrc.mappers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.dao.PlaylistDao;
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

  public static List<Playlist> map(List<PlaylistDto> collection) {
    return Stream.of(collection).map(PlaylistMapper::map).collect(Collectors.toList());
  }

  public static List<Playlist> mapData(List<PlaylistDao> collection) {
    return Stream.of(collection).map(PlaylistMapper::mapData).collect(Collectors.toList());
  }

  private static Playlist mapData(PlaylistDao playlistDao) {
    Playlist playlist = new Playlist();
    playlist.setTracks(playlistDao.getTracks());
    playlist.setReadOnly(playlistDao.getReadOnly());
    playlist.setPath(playlistDao.getPath());
    playlist.setName(playlistDao.getName());
    playlist.setId(playlistDao.getId());
    return playlist;
  }

  private static PlaylistDao mapDto(PlaylistDto object) {
    PlaylistDao data = new PlaylistDao();
    data.setId(object.getId());
    data.setName(object.getName());
    data.setPath(object.getPath());
    data.setReadOnly(object.getReadOnly());
    data.setDateAdded(object.getDateAdded());
    data.setDateDeleted(object.getDateDeleted());
    data.setDateUpdated(object.getDateUpdated());
    return data;
  }

  public static List<PlaylistDao> mapDto(List<PlaylistDto> objects) {
    return Stream.of(objects).map(PlaylistMapper::mapDto).collect(Collectors.toList());
  }

}
