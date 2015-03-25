package com.webnerdtz.smsjanja.database;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JanjaDbHandler extends SQLiteOpenHelper {

	// static variables
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "messagetable.db";
	
	private static JanjaDbHandler sInstance = null;
	
	public static JanjaDbHandler getInstance(Context context) {

	    if (sInstance == null) {
	      sInstance = new JanjaDbHandler(context.getApplicationContext());
	    }
	    return sInstance;
	}
	
	private JanjaDbHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// creating tables
	@Override
	public void onCreate(SQLiteDatabase database){
		MessageTable.onCreate(database);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
		MessageTable.onUpgrade(database, oldVersion, newVersion);
	}
	
	// All CRUD Operations...
	
	// adding new message
	public void AddMessage(JanjaMessage message){
		SQLiteDatabase db = this.getWritableDatabase();
				
		ContentValues values = new ContentValues();
		values.put(MessageTable.COLUMN_STATUS, message.getStatus());
		values.put(MessageTable.COLUMN_SENDER, message.getSender());
		values.put(MessageTable.COLUMN_MESSAGE, message.getMessage());
		values.put(MessageTable.COLUMN_TIMESTAMP, message.getTimestamp());
		
		db.insert(MessageTable.TABLE_MESSAGES, null, values);
		db.close();
	}
	
	// getting the single message
	JanjaMessage getMessage(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(MessageTable.TABLE_MESSAGES, new String[] { MessageTable.COLUMN_ID, MessageTable.COLUMN_STATUS, MessageTable.COLUMN_SENDER, MessageTable.COLUMN_MESSAGE}, MessageTable.COLUMN_ID + "=?",
				new String[] {String.valueOf(id)}, null, null, null, null);
		if(cursor != null)
			cursor.moveToFirst();
		
		JanjaMessage message = new JanjaMessage(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
				cursor.getString(2), cursor.getString(3), cursor.getString(4));
		return message;
	}
	
	// Getting All Messages
    public List<JanjaMessage> getAllMessages() {
        List<JanjaMessage> messageList = new ArrayList<JanjaMessage>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + MessageTable.TABLE_MESSAGES + " ORDER BY _id DESC";
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                JanjaMessage message = new JanjaMessage();
                message.setID(Integer.parseInt(cursor.getString(0)));
                message.setStatus(Integer.parseInt(cursor.getString(1)));
                message.setSender(cursor.getString(2));
                message.setMessage(cursor.getString(3));
                message.setTimestamp(cursor.getString(4));
                // Adding message to list
                messageList.add(message);
            } while (cursor.moveToNext());
        }
 
        // return message list
        return messageList;
    }
 
    public List<JanjaMessage> getAllPendingMessages() {
        List<JanjaMessage> messageList = new ArrayList<JanjaMessage>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + MessageTable.TABLE_MESSAGES + " WHERE status = 0";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                JanjaMessage message = new JanjaMessage();
                message.setID(Integer.parseInt(cursor.getString(0)));
                message.setStatus(Integer.parseInt(cursor.getString(1)));
                message.setSender(cursor.getString(2));
                message.setMessage(cursor.getString(3));
                message.setTimestamp(cursor.getString(4));
                // Adding message to list
                messageList.add(message);
            } while (cursor.moveToNext());
        }
 
        // return message list
        return messageList;
    }
    
    public JanjaMessage getPendingMessage(){
    		JanjaMessage message = new JanjaMessage();
    		
    		String selectQuery = "SELECT  * FROM " + MessageTable.TABLE_MESSAGES + " WHERE status = 0";
    		 
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            
            if (cursor.moveToFirst()) {
                
                    message.setID(Integer.parseInt(cursor.getString(0)));
                    message.setStatus(Integer.parseInt(cursor.getString(1)));
                    message.setSender(cursor.getString(2));
                    message.setMessage(cursor.getString(3));
                    message.setTimestamp(cursor.getString(4));
            }
            
    	return message;
    }
    
    // Updating single message
    public int updateMessage(JanjaMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(MessageTable.COLUMN_STATUS, message.getStatus());
 
        // updating row
        return db.update(MessageTable.TABLE_MESSAGES, values, MessageTable.COLUMN_ID + " = ?",
                new String[] { String.valueOf(message.getID()) });
    }
 
    // Deleting single Message
    public void deleteMessage(JanjaMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MessageTable.TABLE_MESSAGES, MessageTable.COLUMN_ID + " = ?",
                new String[] { String.valueOf(message.getID()) });
        db.close();
    }
 
 
    // Getting Messages Count
    public int getMessagesCount() {
        String countQuery = "SELECT  * FROM " + MessageTable.TABLE_MESSAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
 
}
