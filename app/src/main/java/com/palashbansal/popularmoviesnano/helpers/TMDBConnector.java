package com.palashbansal.popularmoviesnano.helpers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.palashbansal.popularmoviesnano.models.MovieItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Palash on 3/8/2016.
 */

public class TMDBConnector {
	public static final List<MovieItem> movieList = new ArrayList<>();
	public static Set<Integer> favouriteList = new HashSet<>();
	private static final String BASE_URL = "https://api.themoviedb.org/3/";
	private static final String MOVIE_PARAM = "movie/";
	private static final String VIDEO_PARAM = "/videos";
	private static final String REVIEW_PARAM = "/reviews";
	private static final String KEY_PARAM = "?api_key=" + APIKeys.TMDB_KEY;
	private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
	private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

	public static void getMovie(int id, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
		String url = BASE_URL + MOVIE_PARAM + id + KEY_PARAM;
		getJSONFromGet(url, context, listener, errorListener);
	}

	public static void getTrailers(int id, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
		String url = BASE_URL + MOVIE_PARAM + id + VIDEO_PARAM + KEY_PARAM;
		getJSONFromGet(url, context, listener, errorListener);
	}

	public static void getReviews(int id, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
		String url = BASE_URL + MOVIE_PARAM + id + REVIEW_PARAM + KEY_PARAM;
		getJSONFromGet(url, context, listener, errorListener);
	}


	public static void discover(SortOrder order, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
		String url = BASE_URL + MOVIE_PARAM + order.toString().toLowerCase() + KEY_PARAM;
		getJSONFromGet(url, context, listener, errorListener);
	}

	private static String generateImagePath(String posterName) {
		return IMAGE_BASE_URL + posterName + KEY_PARAM;
	}

	public static void generateMovieObjects(JSONObject json, List<MovieItem> movieList, MovieItemAdapter recyclerViewAdapter, Listener listener) {
		generateMovieObjects(json, movieList, recyclerViewAdapter);
		listener.onFinished(0);
	}

	public static void generateMovieObjects(JSONObject json, List<MovieItem> movieList, MovieItemAdapter recyclerViewAdapter) {
		try {
			JSONArray results = json.getJSONArray("results");
			for(int i=0; i<results.length();i++){
				JSONObject obj = results.getJSONObject(i);
				MovieItem m = (new MovieItem(obj.getInt("id"), obj.getString("original_title"), generateImagePath(obj.getString("poster_path")),
						generateImagePath(obj.getString("backdrop_path")), obj.getString("overview"), obj.getInt("vote_average"), "Sometime after 1896."));
				if(favouriteList.contains(m.getId())) m.setFavourite(true);
				movieList.add(m);
				recyclerViewAdapter.notifyItemInserted(i);
			}
		} catch (JSONException ignored) {
		}
	}

	public static void getOtherDetails(ArrayList<MovieItem> movieList, Context context){
		for(final MovieItem movie: movieList){
			getOtherDetails(movie, context, null);
		}
	}

	public static void getOtherDetails(final MovieItem movie, Context context, @Nullable final Listener listener) { //Release Date, Trailers, Reviews
		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (listener != null) {
					listener.onFinished(1);
				}
			}
		};
		getMovie(movie.getId(), context,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							movie.setRelease_date(response.getString("release_date"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (listener != null) {
							listener.count++;
							listener.onFinished(0);
						}
					}
				},
				errorListener
		);
		getTrailers(movie.getId(), context,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							movie.setTrailers(response.getJSONArray("results"));
							Log.d("Trailers", response.getJSONArray("results").toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (listener != null) {
							listener.count++;
							listener.onFinished(0);
						}
					}
				},
				errorListener
		);
		getReviews(movie.getId(), context,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							movie.setReviews(response.getJSONArray("results"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (listener != null) {
							listener.count++;
							listener.onFinished(0);
						}
					}
				},
				errorListener
		);
	}

	private static void getJSONFromGet(String url, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		VolleyRequestQueue.addToRequestQueue(
				new JsonObjectRequest(Request.Method.GET, url, "",
						listener, errorListener
				),
				context);
	}

	public enum SortOrder {POPULAR, TOP_RATED, FAVOURITES}

	public static abstract class Listener {
		protected int count=0;
		public abstract void onFinished(int error);
	}
}
