package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.dto.NowPlayingTrack
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class QueueTrackMapperTest {

    private var tracks: MutableList<NowPlayingTrack>? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        tracks = ArrayList<NowPlayingTrack>()
        val track = NowPlayingTrack()
        track.title = TRACK_TITLE_1
        track.artist = TRACK_ARIST_1
        track.path = TRACK_PATH_1
        track.position = 1
        tracks!!.add(track)

        val track2 = NowPlayingTrack()
        track2.title = TRACK_TITLE_2
        track2.artist = TRACK_ARIST_2
        track2.path = TRACK_PATH_2
        track2.position = 2
        tracks!!.add(track2)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        tracks!!.clear()
    }

    @Test
    @Throws(Exception::class)
    fun testMapSingle() {
        val track = QueueTrackMapper.map(tracks!![0])

        Assert.assertEquals(TRACK_TITLE_1, track.title)
        Assert.assertEquals(TRACK_ARIST_1, track.artist)
        Assert.assertEquals(TRACK_PATH_1, track.path)
        Assert.assertEquals(1, track.position)
    }

    @Test
    @Throws(Exception::class)
    fun testMapMultiple() {
        val tracks = QueueTrackMapper.map(this.tracks!!)

        val track = tracks[1]
        Assert.assertEquals(2, tracks.size)
        Assert.assertEquals(TRACK_ARIST_2, track.artist)
        Assert.assertEquals(TRACK_TITLE_2, track.title)
        Assert.assertEquals(TRACK_PATH_2, track.path)
        Assert.assertEquals(2, track.position)
    }

    companion object {
        val TRACK_TITLE_1 = "Dialogue With the Stars"
        val TRACK_ARIST_1 = "In Flames"
        val TRACK_PATH_1 = "K:\\media\\Melodic Death Metal\\In Flames\\1997 - Whoracle\\09 - Episode 666.mp3"
        private val TRACK_TITLE_2 = "At the Gates of Sleep"
        val TRACK_ARIST_2 = "Insomnium"
        val TRACK_PATH_2 = "K:\\media\\Melodic Death Metal\\Insomnium\\2006-10-17 - Above the Weeping World\\05 - At the Gates of Sleep.mp3"
    }
}
