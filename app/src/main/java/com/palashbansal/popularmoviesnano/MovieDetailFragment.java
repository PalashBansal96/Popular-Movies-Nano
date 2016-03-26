package com.palashbansal.popularmoviesnano;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.palashbansal.popularmoviesnano.helpers.DBConnector;
import com.palashbansal.popularmoviesnano.helpers.MovieItem;
import com.squareup.picasso.Picasso;

public class MovieDetailFragment extends Fragment {
	public static final String ARG_ORDER_ID = "order_id";

	private MovieItem movie;

	public MovieDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ORDER_ID)) {
			movie = DBConnector.movieList.get(getArguments().getInt(ARG_ORDER_ID));

			Activity activity = this.getActivity();
			CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
			if (appBarLayout != null) {
				appBarLayout.setTitle(movie.getTitle());
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.movie_detail, container, false);

		populateDetails(rootView);
		DBConnector.getOtherDetails(movie, rootView.getContext(), new DBConnector.Listener() {
			@Override
			public void onFinished(int error) {
				if (error == 1) {
					movie.setRelease_date("Sometime after 1896.");
				}
				populateDetails(rootView);
			}
		});
		return rootView;
	}

	private void populateDetails(View rootView) {
		if (movie != null) {
			if (MovieListActivity.twoPane) {
				rootView.findViewById(R.id.title_card).setVisibility(View.VISIBLE);
				((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.getTitle());
			}
			((TextView) rootView.findViewById(R.id.overview)).setText(movie.getOverview());
			((TextView) rootView.findViewById(R.id.release_date)).setText(movie.getRelease_date());
			((TextView) rootView.findViewById(R.id.vote_average)).setText(String.valueOf(movie.getPopularity()));
			Picasso.with(rootView.getContext()).load(movie.getPosterURL()).noFade().into((ImageView) rootView.findViewById(R.id.poster));
		}
	}

}
