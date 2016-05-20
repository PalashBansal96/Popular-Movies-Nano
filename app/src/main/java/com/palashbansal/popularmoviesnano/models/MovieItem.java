package com.palashbansal.popularmoviesnano.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Palash on 3/6/2016.
 */
public class MovieItem {
	private static final String TRAILER_TYPE_TEXT = "Trailer";
	private int id;
	private String title;
	private String posterURL;
	private String backdropURL;
	private String overview;
	private int popularity;
	private String release_date;
	private List<TrailerItem> trailers;
	private List<ReviewItem> reviews;

	public MovieItem(int id, String title, String posterURL, String backdropURL, String overview, int popularity, String release_date) {
		this.id = id;
		this.title = title;
		this.posterURL = posterURL;
		this.backdropURL = backdropURL;
		this.overview = overview;
		this.popularity = popularity;
		this.release_date = release_date;
		trailers = new ArrayList<>();
		reviews = new ArrayList<>();
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

	public void setTrailers(JSONArray trailers) {
		this.trailers = TrailerItem.generateList(trailers);
	}

	public void setReviews(JSONArray reviews) {
		this.reviews = ReviewItem.generateList(reviews);
	}

	public List<TrailerItem> getTrailers() {
		return trailers;
	}

	public List<ReviewItem> getReviews() {
		return reviews;
	}

	public static class TrailerItem {
		private String id;
		private String key;
		private String name;

		public TrailerItem(String id, String key, String name) {
			this.id = id;
			this.key = key;
			this.name = name;
		}

		public static List<TrailerItem> generateList(JSONArray results){
			List<TrailerItem> trailers = new ArrayList<>();
			for(int i=0; i<results.length(); i++){
				try {
					JSONObject result = results.getJSONObject(i);
					if(result.getString("type").equals(TRAILER_TYPE_TEXT) && result.getString("site").equals("Youtube")) {
						trailers.add(new TrailerItem(result.getString("id"), result.getString("key"), result.getString("name")));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return trailers;
		}
	}

	public static class ReviewItem {
		private String id;
		private String author;
		private String content;

		public ReviewItem(String id, String author, String content) {
			this.id = id;
			this.author = author;
			this.content = content;
		}

		public static List<ReviewItem> generateList(JSONArray results){
			List<ReviewItem> reviews = new ArrayList<>();
			for(int i=0; i<results.length(); i++){
				try {
					JSONObject result = results.getJSONObject(i);
					reviews.add(new ReviewItem(result.getString("id"), result.getString("author"), result.getString("content")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return reviews;
		}
	}
}
