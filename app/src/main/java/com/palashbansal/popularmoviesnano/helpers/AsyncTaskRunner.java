package com.palashbansal.popularmoviesnano.helpers;

import android.os.AsyncTask;

/**
 * Created by Palash on 6/10/2016.
 */
public class AsyncTaskRunner extends AsyncTask<Runnable, Void, Runnable>{

	@Override
	protected Runnable doInBackground(Runnable... params) {
		params[0].run();
		return params[1];
	}

	@Override
	protected void onPostExecute(Runnable runnable) {
		if(runnable!=null)
			runnable.run();
	}
}
