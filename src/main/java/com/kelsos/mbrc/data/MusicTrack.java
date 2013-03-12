package com.kelsos.mbrc.data;

public class MusicTrack {
    private String title;
    private String artist;
    private int position;

    public MusicTrack(String artist, String title) {
        this.artist = artist;
        this.title = title;
        position = 0;
    }

    public MusicTrack(String artist, String title, int position) {
        super();
        this.title = title;
        this.artist = artist;
        this.position = position;
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

    public int getPosition() {
        return position;
    }

	@Override
	public boolean equals(Object o)
	{
		boolean rValue = false;
		if(o instanceof MusicTrack)
		{
			MusicTrack track = (MusicTrack)o;
			if(track.getTitle().equals(this.title)&&track.getArtist().equals(this.artist))
				rValue=true;
		}
		return rValue;
	}
}
