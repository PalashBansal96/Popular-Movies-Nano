package com.palashbansal.popularmoviesnano.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.palashbansal.popularmoviesnano.R;
import com.palashbansal.popularmoviesnano.helpers.TMDBConnector;

public class MovieDetailActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_detail);
		Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "TODO: Added to favourite.", Snackbar.LENGTH_LONG).show();
			}
		});

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			String title = TMDBConnector.movieList.get(getIntent().getIntExtra(MovieDetailFragment.ARG_ORDER_ID, 0)).getTitle();
			actionBar.setTitle(title);
		}
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putInt(MovieDetailFragment.ARG_ORDER_ID,
					getIntent().getIntExtra(MovieDetailFragment.ARG_ORDER_ID, 0));
			MovieDetailFragment fragment = new MovieDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.movie_detail_container, fragment)
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpTo(this, new Intent(this, MovieListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
