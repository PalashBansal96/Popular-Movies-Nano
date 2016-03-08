package com.palashbansal.popularmoviesnano.helpers;

/**
 * Created by Palash on 3/6/2016.
 */
public class MovieItem {
	private int id;
	private String title;
	private String imageURL;
	private String overview;
	private int popularity;
	private String release_date;

	public MovieItem(int id, String title, String imageURL, String overview, int popularity, String release_date) {
		this.id = id;
		this.title = title;
		this.imageURL = imageURL;
		this.overview = overview;
		this.popularity = popularity;
		this.release_date = release_date;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getImageURL() {
		return imageURL;
	}

	public String getOverview() {
		return overview;
	}

	public int getPopularity() {
		return popularity;
	}

	public String getRelease_date() {
		return release_date;
	}

	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}
}
