package com.palashbansal.popularmoviesnano.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import com.palashbansal.popularmoviesnano.models.MovieItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Palash on 6/10/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Movies.db";
	private static DatabaseHelper instance;
	private SQLiteDatabase dbWrite;
	private SQLiteDatabase dbRead;

	protected DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MovieItem.CREATE_TABLE_STRING);
		db.execSQL(MovieItem.TrailerItem.CREATE_TABLE_STRING);
		db.execSQL(MovieItem.ReviewItem.CREATE_TABLE_STRING);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MovieItem.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MovieItem.ReviewItem.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MovieItem.TrailerItem.TABLE_NAME);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	public static void initialize(Context context){
		if(instance!=null)instance.close();
		instance = new DatabaseHelper(context);
	}

	public static void getWriteDB() {
		if(instance.dbWrite!=null) instance.dbWrite.close();
		instance.dbWrite = instance.getWritableDatabase();
	}

	public static void closeWriteDB(){
		instance.dbWrite.close();
		instance.dbWrite = null;
	}
	public static void getReadDB() {
		if(instance.dbRead!=null) instance.dbRead.close();
		instance.dbRead = instance.getReadableDatabase();
	}

	public static void closeReadDB(){
		instance.dbRead.close();
		instance.dbRead = null;
	}

	public static void closeHelper() {
		instance.close();
		instance=null;
	}

	public static void updateMovie(MovieItem movie){
		if(instance.dbWrite==null) getWriteDB();
		instance.dbWrite.execSQL("REPLACE INTO " + movie.getDBString() + ";");
		for (MovieItem.TrailerItem trailer: movie.getTrailers()) {
			instance.dbWrite.execSQL("REPLACE INTO " + trailer.getDBString(movie.getId()) + ";");
		}
		for (MovieItem.ReviewItem review: movie.getReviews()) {
			instance.dbWrite.execSQL("REPLACE INTO " + review.getDBString(movie.getId()) + ";");
		}
	}

	public static ArrayList<MovieItem> getAllMovie(@Nullable String where){
		if(where == null) where = "1";
		if(instance.dbRead==null) getReadDB();
		Cursor movieCursor = instance.dbRead.rawQuery("SELECT * FROM " + MovieItem.TABLE_NAME + " WHERE " + where + ";", new String[]{});
		movieCursor.moveToFirst();
		ArrayList<MovieItem> movies = new ArrayList<>();
		while(!movieCursor.isAfterLast()){
			MovieItem movie = new MovieItem(movieCursor);
			Cursor trailerCursor = instance.dbRead.rawQuery("SELECT * FROM " + MovieItem.TrailerItem.TABLE_NAME + " WHERE " + MovieItem.TrailerItem.COLUMN_MOVIE_ID + "=" + movie.getId(), new String[]{});
			movie.setTrailers(trailerCursor);
			trailerCursor.close();
			Cursor reviewCursor = instance.dbRead.rawQuery("SELECT * FROM " + MovieItem.ReviewItem.TABLE_NAME + " WHERE " + MovieItem.TrailerItem.COLUMN_MOVIE_ID + "=" + movie.getId(), new String[]{});
			movie.setReviews(reviewCursor);
			reviewCursor.close();
			movies.add(movie);
			movieCursor.moveToNext();
		}
		movieCursor.close();
		return movies;
	}

	public static ArrayList<MovieItem> getFavouriteMovies(){
		return getAllMovie(MovieItem.COLUMN_FAVOURITE + "=1");
	}

	public static Set<Integer> getFavouriteIDs(){
		Cursor movieCursor = instance.dbRead.rawQuery("SELECT " + MovieItem.COLUMN_ID + " FROM " + MovieItem.TABLE_NAME + " WHERE " + MovieItem.COLUMN_FAVOURITE + "=1;", new String[]{});
		movieCursor.moveToFirst();
		Set<Integer> movies = new HashSet<>();
		while(!movieCursor.isAfterLast()) {
			movies.add(movieCursor.getInt(0));
			movieCursor.moveToNext();
		}
		movieCursor.close();
		return movies;
	}
}
