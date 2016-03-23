package com.palashbansal.popularmoviesnano.helpers;

/**
 * Created by Palash on 3/6/2016.
 */
public class MovieItem {
	private int id;
	private String title;
	private String posterURL;
	private String backdropURL;
	private String overview;
	private int popularity;
	private String release_date;

	public MovieItem(int id, String title, String posterURL, String backdropURL, String overview, int popularity, String release_date) {
		this.id = id;
		this.title = title;
		this.posterURL = posterURL;
		this.backdropURL = backdropURL;
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

	public String getPosterURL() {
		return posterURL;
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

	public String getBackdropURL() {
		return backdropURL;
	}
}
