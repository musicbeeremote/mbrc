package com.kelsos.mbrc.data;

public class MusicTrack {
    private String title;
    private String artist;

    public MusicTrack(String artist, String title) {
        super();
        this.title = title;
        this.artist = artist;
	}

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof MusicTrack)
		{
			MusicTrack track = (MusicTrack)o;
			if(track.getTitle().equals(this.title)&&track.getArtist().equals(this.artist))
				return true;
		}
		return false;
	}
}
