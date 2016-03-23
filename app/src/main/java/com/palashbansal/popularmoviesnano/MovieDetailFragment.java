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

	private MovieItem mItem;

	public MovieDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ORDER_ID)) {
			mItem = DBConnector.movieList.get(getArguments().getInt(ARG_ORDER_ID));

			Activity activity = this.getActivity();
			CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
			if (appBarLayout != null) {
				appBarLayout.setTitle(mItem.getTitle());
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.movie_detail, container, false);

		populateDetails(rootView);
		DBConnector.getOtherDetails(mItem, rootView.getContext(), new DBConnector.Listener() {
			@Override
			public void onFinished(int error) {
				if (error == 1) {
					mItem.setRelease_date("Sometime after 1896.");
				}
				populateDetails(rootView);
			}
		});
		return rootView;
	}

	private void populateDetails(View rootView) {
		if (mItem != null) {
			if (MovieListActivity.mTwoPane) {
				rootView.findViewById(R.id.title_card).setVisibility(View.VISIBLE);
				((TextView) rootView.findViewById(R.id.movie_title)).setText(mItem.getTitle());
			}
			((TextView) rootView.findViewById(R.id.overview)).setText(mItem.getOverview());
			((TextView) rootView.findViewById(R.id.release_date)).setText(mItem.getRelease_date());
			((TextView) rootView.findViewById(R.id.vote_average)).setText(String.valueOf(mItem.getPopularity()));
			Picasso.with(rootView.getContext()).load(mItem.getPosterURL()).into((ImageView) rootView.findViewById(R.id.poster));
		}
	}

}
