package com.palashbansal.popularmoviesnano.helpers;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Palash on 6/10/2016.
 */
public class AsyncTaskRunner extends AsyncTask<Runnable, Void, Runnable>{
	@Override
	protected Runnable doInBackground(Runnable... params) {
		for(int i=0;i<params.length-1;i++)
			params[i].run();
		return params[params.length-1];
	}

	@Override
	protected void onPostExecute(Runnable runnable) {
		if(runnable!=null)
			runnable.run();
	}
}
