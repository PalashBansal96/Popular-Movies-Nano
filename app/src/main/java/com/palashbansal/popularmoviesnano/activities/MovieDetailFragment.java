package com.palashbansal.popularmoviesnano.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.palashbansal.popularmoviesnano.R;
import com.palashbansal.popularmoviesnano.helpers.TMDBConnector;
import com.palashbansal.popularmoviesnano.models.MovieItem;
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
			movie = TMDBConnector.movieList.get(getArguments().getInt(ARG_ORDER_ID));

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
		TMDBConnector.getOtherDetails(movie, rootView.getContext(), new TMDBConnector.Listener() {
			@Override
			public void onFinished(int error) {
				if(this.count>3)return;
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
			((TextView) rootView.findViewById(R.id.release_date)).setText(movie.getRelease_date());
			((TextView) rootView.findViewById(R.id.vote_average)).setText(String.valueOf(movie.getPopularity()));
			Picasso.with(rootView.getContext()).load(movie.getPosterURL()).noFade().into((ImageView) rootView.findViewById(R.id.poster));
			((TextView) rootView.findViewById(R.id.overview)).setText(movie.getOverview());

			ListView trailersList = (ListView) rootView.findViewById(R.id.trailer_list);
			trailersList.setAdapter(new ArrayAdapter<MovieItem.TrailerItem>(rootView.getContext(), 0, movie.getTrailers()){
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					final MovieItem.TrailerItem trailer = getItem(position);
					if (convertView == null) {
						convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_item, parent, false);
					}
					TextView title = (TextView) convertView.findViewById(R.id.trailer_title);
					ImageView thumbnail = (ImageView) convertView.findViewById(R.id.trailer_thumbnail);
					title.setText(trailer.getName());
					Picasso.with(getContext()).load("http://img.youtube.com/vi/" + trailer.getKey() + "/1.jpg").into(thumbnail);
					convertView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
								startActivity(intent);
							} catch (ActivityNotFoundException ex) {
								Intent intent = new Intent(Intent.ACTION_VIEW,
										Uri.parse("http://www.youtube.com/watch?v=" + movie.getId()));
								startActivity(intent);
							}
						}
					});
					return convertView;
				}
			});
			setListViewHeightBasedOnChildren(trailersList, 3);

			final ListView reviewList = (ListView) rootView.findViewById(R.id.review_list);
			reviewList.setAdapter(new ArrayAdapter<MovieItem.ReviewItem>(rootView.getContext(), 0, movie.getReviews()){
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					final MovieItem.ReviewItem review = getItem(position);
					if (convertView == null) {
						convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);
					}
					TextView title = (TextView) convertView.findViewById(R.id.review_name);
					TextView content = (TextView) convertView.findViewById(R.id.review_content);
					title.setText(review.getAuthor());
					content.setText(review.getContent());
					convertView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(review.getUrl()));
							startActivity(intent);
						}
					});
					return convertView;
				}
			});
			setListViewHeightBasedOnChildren(reviewList, 3);
			getActivity().findViewById(R.id.movie_detail_container).scrollTo(0,0);
		}
	}
	public static void setListViewHeightBasedOnChildren(ListView listView, int limit) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < Math.min(listAdapter.getCount(),limit); i++) {
			view = listAdapter.getView(i, view, listView);
			view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
}
