package com.palashbansal.popularmoviesnano.helpers;

/**
 * Created by Palash on 3/6/2016.
 */
public class MovieItem {
	private String title;
	private String imageURL;
	private String overview;
	private String top_rated;
	private String release_date;

	public MovieItem(String title, String imageURL, String overview, String top_rated, String release_date) {
		this.title = title;
		this.imageURL = imageURL;
		this.overview = overview;
		this.top_rated = top_rated;
		this.release_date = release_date;
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

	public String getTop_rated() {
		return top_rated;
	}

	public String getRelease_date() {
		return release_date;
	}
}
