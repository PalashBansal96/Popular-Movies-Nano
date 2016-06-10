package com.palashbansal.popularmoviesnano.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Palash on 3/6/2016.
 */
public class MovieItem {
	public static final String TABLE_NAME = "movie";
	public static final String COLUMN_ID = "_ID";
	public static final String COLUMN_TITLE = "Title";
	public static final String COLUMN_POSTER_URL = "Poster_URL";
	public static final String COLUMN_BACKDROP_URL = "Backdrop_URL";
	public static final String COLUMN_OVERVIEW = "Overview";
	public static final String COLUMN_POPULARITY = "Popularity";
	public static final String COLUMN_FAVOURITE = "Favourite";
	public static final String COLUMN_RELEASE_DATE = "Release_Date";
	public static final String CREATE_TABLE_STRING = "CREATE TABLE " + TABLE_NAME + " (" +
			COLUMN_ID + " INTEGER PRIMARY KEY," +
			COLUMN_TITLE + " TEXT," +
			COLUMN_POSTER_URL + " TEXT," +
			COLUMN_BACKDROP_URL + " TEXT," +
			COLUMN_OVERVIEW + " TEXT," +
			COLUMN_POPULARITY + " INTEGER," +
			COLUMN_RELEASE_DATE + " TEXT," +
			COLUMN_FAVOURITE + " INTEGER," +
			" )";
	private static final String TRAILER_TYPE_TEXT = "Trailer";
	private int id;
	private String title;
	private String posterURL;
	private String backdropURL;
	private String overview;
	private int popularity;
	private String release_date;
	private boolean favourite;
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
		favourite = false;
		trailers = new ArrayList<>();
		reviews = new ArrayList<>();
	}
	public MovieItem(Cursor c) {
		this.id = c.getInt(0);
		this.title = c.getString(1);
		this.posterURL = c.getString(2);
		this.backdropURL = c.getString(3);
		this.overview = c.getString(4);
		this.popularity = c.getInt(5);
		this.release_date = c.getString(6);
		favourite = c.getInt(7) == 1;
		trailers = new ArrayList<>();
		reviews = new ArrayList<>();
	}


	public String getDBString(){
		return String.format(Locale.ENGLISH, "%s VALUES (%d, %s, %s, %s, %s, %d, %s, %d)", TABLE_NAME, id, title, posterURL, backdropURL, overview, popularity, release_date, favourite?1:0);
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

	public void setTrailers(Cursor cursor) {
		this.trailers = TrailerItem.generateList(cursor);
	}


	public void setReviews(JSONArray reviews) {
		this.reviews = ReviewItem.generateList(reviews);
	}

	public void setReviews(Cursor cursor) {
		this.reviews = ReviewItem.generateList(cursor);
	}

	public List<TrailerItem> getTrailers() {
		return trailers;
	}

	public List<ReviewItem> getReviews() {
		return reviews;
	}

	public boolean isFavourite() {
		return favourite;
	}

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

	public static class TrailerItem {
		public static final String TABLE_NAME = "trailer";
		public static final String COLUMN_KEY = "Key";
		public static final String COLUMN_NAME = "Name";
		public static final String COLUMN_ID = "_ID";
		public static final String COLUMN_MOVIE_ID = "Movie_ID";
		public static final String CREATE_TABLE_STRING = "CREATE TABLE " + TABLE_NAME + " (" +
				COLUMN_ID + " TEXT PRIMARY KEY," +
				COLUMN_KEY + " TEXT," +
				COLUMN_NAME + " TEXT," +
				COLUMN_MOVIE_ID + " INTEGER," +
				" )";

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
					if(result.getString("type").equals(TRAILER_TYPE_TEXT) && result.getString("site").toLowerCase().equals("youtube")) {
						trailers.add(new TrailerItem(result.getString("id"), result.getString("key"), result.getString("name")));
					}
					Log.d("Trailer", String.valueOf(result.getString("site")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return trailers;
		}

		public static List<TrailerItem> generateList(Cursor c){
			List<TrailerItem> trailers = new ArrayList<>();
			c.moveToFirst();
			while(!c.isAfterLast()){
				trailers.add(new TrailerItem(c.getString(0), c.getString(1), c.getString(2)));
				c.moveToNext();
			}
			return trailers;
		}

		public String getDBString(int movie_id){
			return String.format(Locale.ENGLISH, "%s VALUES (%s, %s, %s, %d)", TABLE_NAME, id, key, name, movie_id);
		}


		public String getId() {
			return id;
		}

		public String getKey() {
			return key;
		}

		public String getName() {
			return name;
		}
	}

	public static class ReviewItem {
		public static final String TABLE_NAME = "review";
		public static final String COLUMN_CONTENT = "Content";
		public static final String COLUMN_URL = "URL";
		public static final String COLUMN_AUTHOR = "Author";
		public static final String COLUMN_ID = "_ID";
		public static final String COLUMN_MOVIE_ID = "Movie_ID";
		public static final String CREATE_TABLE_STRING = "CREATE TABLE " + TABLE_NAME + " (" +
				COLUMN_ID + " TEXT PRIMARY KEY," +
				COLUMN_CONTENT + " TEXT," +
				COLUMN_URL + " TEXT," +
				COLUMN_AUTHOR + " TEXT," +
				COLUMN_MOVIE_ID + " INTEGER," +
				" )";

		private String id;
		private String author;
		private String content;
		private String url;

		public ReviewItem(String id, String author, String content, String url) {
			this.id = id;
			this.author = author;
			if(content.length()>200)
				this.content = content.substring(0, 200) + " ...";
			else this.content = content;
			this.url = url;
		}

		public static List<ReviewItem> generateList(JSONArray results){
			List<ReviewItem> reviews = new ArrayList<>();
			for(int i=0; i<results.length(); i++){
				try {
					JSONObject result = results.getJSONObject(i);
					reviews.add(new ReviewItem(result.getString("id"), result.getString("author"), result.getString("content"), result.getString("url")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return reviews;
		}

		public static List<ReviewItem> generateList(Cursor c){
			List<ReviewItem> reviews = new ArrayList<>();
			c.moveToFirst();
			while(!c.isAfterLast()){
				reviews.add(new ReviewItem(c.getString(0), c.getString(3), c.getString(1), c.getString(2)));
				c.moveToNext();
			}
			return reviews;
		}

		public String getDBString(int movie_id){
			return String.format(Locale.ENGLISH, "%s VALUES (%s, %s, %s, %s, %d)", TABLE_NAME, id, author, url, content, movie_id);
		}

		public String getId() {
			return id;
		}

		public String getAuthor() {
			return author;
		}

		public String getContent() {
			return content;
		}

		public String getUrl() {
			return url;
		}
	}
}
