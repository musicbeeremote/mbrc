package com.kelsos.mbrc.rest.responses;

public class TrackResponse {
    private String artist;
    private String title;
    private String album;
    private String year;
	private String path;

    @SuppressWarnings("UnusedDeclaration")
	public TrackResponse(String artist, String title, String album, String year, String path) {
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.year = year;
		this.path = path;
    }

    @SuppressWarnings("UnusedDeclaration")
	public TrackResponse() { }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getYear() {
        return year;
    }

	public String getPath() {
		return path;
	}
}
