package com.palashbansal.popularmoviesnano.helpers;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.palashbansal.popularmoviesnano.MovieDetailActivity;
import com.palashbansal.popularmoviesnano.MovieDetailFragment;
import com.palashbansal.popularmoviesnano.MovieListActivity;
import com.palashbansal.popularmoviesnano.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Palash on 3/23/2016.
 */
public class MovieItemAdapter
		extends RecyclerView.Adapter<MovieItemAdapter.ViewHolder> {

	private final List<MovieItem> movies;
	private MovieListActivity movieListActivity;

	public MovieItemAdapter(List<MovieItem> items, MovieListActivity movieListActivity) {
		movies = items;
		this.movieListActivity = movieListActivity;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.movie_list_content, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		holder.item = movies.get(position);
		Picasso.with(movieListActivity).load(holder.item.getPosterURL()).into(holder.imageView);
		if(MovieListActivity.twoPane){
			holder.imageFrame.setForeground(movieListActivity.getResources().getDrawable(R.drawable.selected_foreground));
		}
		holder.view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MovieListActivity.twoPane) {
					movieListActivity.loadContentInPane(holder.getAdapterPosition());
				} else {
					final Context context = v.getContext();
					final Intent intent = new Intent(context, MovieDetailActivity.class);
					intent.putExtra(MovieDetailFragment.ARG_ORDER_ID, holder.getAdapterPosition());
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						new Handler().postDelayed(new Runnable() {
							@SuppressLint("NewApi")
							@Override
							public void run() {
								//noinspection unchecked
								context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(movieListActivity, holder.imageView, "PosterImage").toBundle());
							}
						}, 200);
					}else{
						context.startActivity(intent);
					}
				}
			}
		});
		holder.imageFrame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					v.performClick();
			}
		});
	}

	@Override
	public int getItemCount() {
		return movies.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView imageView;
		public FrameLayout imageFrame;
		public MovieItem item;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.view.setClickable(true);
			imageFrame = (FrameLayout) view.findViewById(R.id.image_frame);
			imageView = (ImageView) view.findViewById(R.id.content);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + item.getTitle() + "'";
		}
	}
}
