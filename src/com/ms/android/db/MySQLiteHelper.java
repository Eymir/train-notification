package com.ms.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper 
{
	
	private static final String DATABASE_NAME = "trainNotification";
	private static final int DATABASE_VERSION = 1;
	
	//Table name
	public static final String TABLE_TRAIN_NOTIFICATION = "train_notification";
	public static final String TABLE_RECENT_SEARCH = "recent_search";
	
	//Column names
	public static final String TRAIN_NUMBER = "train_number";
	public static final String TRAIN_NAME = "train_name";
	public static final String STATION_CODE = "stationcode";
	public static final String STATUS_BAR_NOTIF = "status_bar_notif";
	public static final String SMS_NOTIF = "sms_notif";
	public static final String EMAIL_NOTIF = "email_notif";
	
	
		
	
	public MySQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		String createTableQuery = "CREATE TABLE " + TABLE_TRAIN_NOTIFICATION + " ( " +
									TRAIN_NUMBER + " INTEGER NOT NULL, " +
									TRAIN_NAME + " TEXT NOT NULL, " +
									STATION_CODE + " TEXT NOT NULL, " +
									STATUS_BAR_NOTIF + " INTEGER, " +
									SMS_NOTIF + " INTEGER, " +
									EMAIL_NOTIF + " INTEGER, " +
									"PRIMARY KEY(" + TRAIN_NUMBER + "," + TRAIN_NAME + "," +  
									STATION_CODE + "))";
		
		String createRecentSearchTableQuery = "CREATE TABLE " + TABLE_RECENT_SEARCH + " ( " +
												TRAIN_NUMBER + " INTEGER NOT NULL, " +
												TRAIN_NAME + " TEXT NOT NULL, " +
												"PRIMARY KEY(" + TRAIN_NUMBER + ", " + TRAIN_NAME + "))";
		db.execSQL(createTableQuery);
		db.execSQL(createRecentSearchTableQuery);
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		String dropNotificationTableQuery = "DROP TABLE IF EXISTS " + TABLE_TRAIN_NOTIFICATION ;
		String dropSearchTableQuery = "DROP TABLE IF EXISTS " + TABLE_RECENT_SEARCH;
		db.execSQL(dropNotificationTableQuery);
		db.execSQL(dropSearchTableQuery);
		onCreate(db);
	}
	
	

}
