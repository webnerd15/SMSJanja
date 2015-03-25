package com.webnerdtz.smsjanja.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessageTable {

	// Database table
	  public static final String TABLE_MESSAGES = "messages";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_STATUS = "status";
	  public static final String COLUMN_SENDER = "sender";
	  public static final String COLUMN_MESSAGE = "message";
	  public static final String COLUMN_TIMESTAMP = "timestamp";

	  // Database creation SQL statement
	  private static final String DATABASE_CREATE = "create table " 
	      + TABLE_MESSAGES
	      + "(" 
	      + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_STATUS + " integer not null, " 
	      + COLUMN_SENDER + " text not null," 
	      + COLUMN_MESSAGE + " text not null,"
	      + COLUMN_TIMESTAMP + " text not null" 
	      + ");";

	  public static void onCreate(SQLiteDatabase database) {
		  database.execSQL(DATABASE_CREATE);
	  }

	  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
	      int newVersion) {
		    Log.w(MessageTable.class.getName(), "Upgrading database from version "
		        + oldVersion + " to " + newVersion
		        + ", which will destroy all old data");
		    database.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		    onCreate(database);
	  }
	  
}
