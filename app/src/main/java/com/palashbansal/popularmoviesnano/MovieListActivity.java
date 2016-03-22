package com.palashbansal.popularmoviesnano;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.palashbansal.popularmoviesnano.helpers.DBConnector;
import com.palashbansal.popularmoviesnano.helpers.MovieItem;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private List<MovieItem> movies = new ArrayList<>();
	private DBConnector.SortOrder order = DBConnector.SortOrder.POPULAR;
	private RecyclerView recyclerView;
	private SimpleItemRecyclerViewAdapter recyclerViewAdapter;
	private GridLayoutManager gridLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_list);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle(getTitle());

		Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);  //TODO: Remove in Production

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(order == DBConnector.SortOrder.POPULAR) {
					order = DBConnector.SortOrder.TOP_RATED;
				}else {
					order = DBConnector.SortOrder.POPULAR;
				}
				int temp_size = movies.size();
				movies.clear();
				recyclerViewAdapter.notifyItemRangeRemoved(0, temp_size);
				refreshMovieList();
			}
		});

		if (findViewById(R.id.movie_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-w900dp).
			// If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;
		}

		if(mTwoPane) {
			gridLayoutManager = new GridLayoutManager(this, 3);
		}else{
			gridLayoutManager = new GridLayoutManager(this, 2);
		}
		recyclerView = (RecyclerView) findViewById(R.id.movie_list);
		recyclerViewAdapter = new SimpleItemRecyclerViewAdapter(movies);
		setupRecyclerView(recyclerViewAdapter);

		refreshMovieList();

	}

	private void refreshMovieList() {
		DBConnector.discover(order, this,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						DBConnector.generateMovieObjects(response, movies, recyclerViewAdapter);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
	}

	private void setupRecyclerView(SimpleItemRecyclerViewAdapter recyclerViewAdapter) {
		recyclerView.setLayoutManager(gridLayoutManager);
		recyclerView.setAdapter(recyclerViewAdapter);
	}

	public class SimpleItemRecyclerViewAdapter
			extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

		private final List<MovieItem> mValues;

		public SimpleItemRecyclerViewAdapter(List<MovieItem> items) {
			mValues = items;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.movie_list_content, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			holder.mItem = mValues.get(position);
			Picasso.with(getApplicationContext()).load(holder.mItem.getImageURL()).into(holder.mImageView);

			holder.mView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mTwoPane) {
						Bundle arguments = new Bundle();
						arguments.putString(MovieDetailFragment.ARG_ITEM_ID, String.valueOf(holder.mItem.getId()));
						MovieDetailFragment fragment = new MovieDetailFragment();
						fragment.setArguments(arguments);
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.movie_detail_container, fragment)
								.commit();
					} else {
						Context context = v.getContext();
						Intent intent = new Intent(context, MovieDetailActivity.class);
						intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, holder.mItem.getId());

						context.startActivity(intent);
					}
				}
			});
		}

		@Override
		public int getItemCount() {
			return mValues.size();
		}

		public class ViewHolder extends RecyclerView.ViewHolder {
			public final View mView;
			public final ImageView mImageView;
			public MovieItem mItem;

			public ViewHolder(View view) {
				super(view);
				mView = view;
				mImageView = (ImageView) view.findViewById(R.id.content);
			}

			@Override
			public String toString() {
				return super.toString() + " '" + mItem.getTitle() + "'";
			}
		}
	}
}
