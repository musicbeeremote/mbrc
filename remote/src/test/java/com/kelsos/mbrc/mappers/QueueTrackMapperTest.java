package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.domain.QueueTrack;
import com.kelsos.mbrc.dto.NowPlayingTrack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class QueueTrackMapperTest {
  public static final String TRACK_TITLE_1 = "Dialogue With the Stars";
  public static final String TRACK_ARIST_1 = "In Flames";
  public static final String TRACK_PATH_1 = "K:\\media\\Melodic Death Metal\\In Flames\\1997 - Whoracle\\09 - Episode 666.mp3";
  private static final String TRACK_TITLE_2 = "At the Gates of Sleep";
  public static final String TRACK_ARIST_2 = "Insomnium";
  public static final String TRACK_PATH_2 = "K:\\media\\Melodic Death Metal\\Insomnium\\2006-10-17 - Above the Weeping World\\05 - At the Gates of Sleep.mp3";


  private List<NowPlayingTrack> tracks;

  @Before
  public void setUp() throws Exception {
    tracks = new ArrayList<>();
    final NowPlayingTrack track = new NowPlayingTrack();
    track.setTitle(TRACK_TITLE_1);
    track.setArtist(TRACK_ARIST_1);
    track.setPath(TRACK_PATH_1);
    track.setPosition(1);
    tracks.add(track);

    final NowPlayingTrack track2 = new NowPlayingTrack();
    track2.setTitle(TRACK_TITLE_2);
    track2.setArtist(TRACK_ARIST_2);
    track2.setPath(TRACK_PATH_2);
    track2.setPosition(2);
    tracks.add(track2);
  }

  @After
  public void tearDown() throws Exception {
    tracks.clear();
  }

  @Test
  public void testMapSingle() throws Exception {
    final QueueTrack track = QueueTrackMapper.map(tracks.get(0));

    assertEquals(TRACK_TITLE_1, track.getTitle());
    assertEquals(TRACK_ARIST_1, track.getArtist());
    assertEquals(TRACK_PATH_1, track.getPath());
    assertEquals(1, track.getPosition());
  }

  @Test
  public void testMapMultiple() throws Exception {
    final List<QueueTrack> tracks = QueueTrackMapper.map(this.tracks);

    final QueueTrack track = tracks.get(1);
    assertEquals(2, tracks.size());
    assertEquals(TRACK_ARIST_2, track.getArtist());
    assertEquals(TRACK_TITLE_2, track.getTitle());
    assertEquals(TRACK_PATH_2, track.getPath());
    assertEquals(2, track.getPosition());
  }
}
