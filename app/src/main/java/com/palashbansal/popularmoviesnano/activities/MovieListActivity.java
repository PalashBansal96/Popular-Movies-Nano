package com.palashbansal.popularmoviesnano.activities;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.palashbansal.popularmoviesnano.R;
import com.palashbansal.popularmoviesnano.data.DatabaseHelper;
import com.palashbansal.popularmoviesnano.helpers.AsyncTaskRunner;
import com.palashbansal.popularmoviesnano.helpers.TMDBConnector;
import com.palashbansal.popularmoviesnano.helpers.MovieItemAdapter;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieListActivity extends AppCompatActivity {

	public static boolean twoPane;
	private TMDBConnector.SortOrder order = TMDBConnector.SortOrder.POPULAR;
	private RecyclerView recyclerView;
	private MovieItemAdapter recyclerViewAdapter;
	private String latestResponse = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_list);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle(getTitle());

//		Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);  //TODO: Remove later

		DatabaseHelper.initialize(this);
		if (findViewById(R.id.movie_detail_container) != null) {
			twoPane = true;
		}

		TMDBConnector.movieList.clear();
		recyclerView = (RecyclerView) findViewById(R.id.movie_list);
		recyclerViewAdapter = new MovieItemAdapter(TMDBConnector.movieList, this);
		setupRecyclerView(recyclerViewAdapter);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
				builder.setTitle(R.string.sort_order)
						.setSingleChoiceItems(R.array.sort_array, order.ordinal(), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (which == 2){
									order = TMDBConnector.SortOrder.FAVOURITES;
								} else if (which == 1) {
									order = TMDBConnector.SortOrder.TOP_RATED;
								} else {
									order = TMDBConnector.SortOrder.POPULAR;
								}
								refreshMovieList();
							}
						}).create().show();
			}
		});

		refreshMovieList();
	}


	private void refreshMovieList() {
		int temp_size = TMDBConnector.movieList.size();
		TMDBConnector.movieList.clear();
		recyclerViewAdapter.notifyItemRangeRemoved(0, temp_size);
		if(order== TMDBConnector.SortOrder.FAVOURITES){
			new AsyncTaskRunner().execute(new Runnable() {
				@Override
				public void run() {
					DatabaseHelper.getFavouriteMovies(TMDBConnector.movieList);
				}
			}, new Runnable() {
				@Override
				public void run() {
					recyclerViewAdapter.notifyItemRangeInserted(0, TMDBConnector.movieList.size());
				}
			});
		}else
			TMDBConnector.discover(order, this,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							latestResponse = response.toString();
							TMDBConnector.generateMovieObjects(response, TMDBConnector.movieList, recyclerViewAdapter, new TMDBConnector.Listener() {
								@Override
								public void onFinished(int error) {
									if (twoPane && !TMDBConnector.movieList.isEmpty()) loadContentInPane(0);
								}
							});
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
						}
					});
	}

	public void loadContentInPane(int position) {
		if (!twoPane) return;
		Bundle arguments = new Bundle();
		arguments.putInt(MovieDetailFragment.ARG_ORDER_ID, position);
		MovieDetailFragment fragment = new MovieDetailFragment();
		fragment.setArguments(arguments);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.movie_detail_container, fragment)
				.commit();
	}

	private void setupRecyclerView(MovieItemAdapter recyclerViewAdapter) {
		GridLayoutManager gridLayoutManager;
		if (twoPane) {
			gridLayoutManager = new GridLayoutManager(this, 3);
		} else {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

				gridLayoutManager = new GridLayoutManager(this, 2);
			} else
				gridLayoutManager = new GridLayoutManager(this, 4);
		}
		recyclerView.setLayoutManager(gridLayoutManager);
		recyclerView.setAdapter(recyclerViewAdapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("Sort_Order", order.ordinal());
		outState.putString("Latest_Data", latestResponse);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		order = TMDBConnector.SortOrder.values()[savedInstanceState.getInt("Sort_Order")];
		try {
			latestResponse = savedInstanceState.getString("Latest_Data");
			TMDBConnector.generateMovieObjects(new JSONObject(latestResponse), TMDBConnector.movieList, recyclerViewAdapter, new TMDBConnector.Listener() {
				@Override
				public void onFinished(int error) {
					if (twoPane && !TMDBConnector.movieList.isEmpty()) loadContentInPane(0);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
		refreshMovieList();
	}
}