package com.palashbansal.popularmoviesnano;

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
import com.palashbansal.popularmoviesnano.helpers.DBConnector;
import com.palashbansal.popularmoviesnano.helpers.MovieItemAdapter;
import org.json.JSONObject;

public class MovieListActivity extends AppCompatActivity {

	public static boolean twoPane;
	private DBConnector.SortOrder order = DBConnector.SortOrder.POPULAR;
	private RecyclerView recyclerView;
	private MovieItemAdapter recyclerViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_list);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle(getTitle());

//		Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);  //TODO: Remove in Production

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
				builder.setTitle(R.string.sort_order)
						.setSingleChoiceItems(R.array.sort_array, order.ordinal(), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (which == 1) {
									order = DBConnector.SortOrder.TOP_RATED;
								} else {
									order = DBConnector.SortOrder.POPULAR;
								}
								refreshMovieList();
							}
						}).create().show();
			}
		});


		if (findViewById(R.id.movie_detail_container) != null) {
			twoPane = true;
		}

		DBConnector.movieList.clear();
		recyclerView = (RecyclerView) findViewById(R.id.movie_list);
		recyclerViewAdapter = new MovieItemAdapter(DBConnector.movieList, this);
		setupRecyclerView(recyclerViewAdapter);
	}


	private void refreshMovieList() {
		int temp_size = DBConnector.movieList.size();
		DBConnector.movieList.clear();
		recyclerViewAdapter.notifyItemRangeRemoved(0, temp_size);
		DBConnector.discover(order, this,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						DBConnector.generateMovieObjects(response, DBConnector.movieList, recyclerViewAdapter, new DBConnector.Listener() {
							@Override
							public void onFinished(int error) {
								if (twoPane && !DBConnector.movieList.isEmpty()) {
									loadContentInPane(0);
								}
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
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		order = DBConnector.SortOrder.values()[savedInstanceState.getInt("Sort_Order")];
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		refreshMovieList();
	}
}
