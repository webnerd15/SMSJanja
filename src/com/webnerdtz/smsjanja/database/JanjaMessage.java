package com.webnerdtz.smsjanja.database;

public class JanjaMessage {
	
	// private vars
	int _id;
	int _status;
	String _sender;
	String _message;
	String _timestamp;
	
	public JanjaMessage(){
		
	}
	
	public JanjaMessage(int id, int status, String sender, String message, String timestamp){
		this._id = id;
		this._status = status;
		this._sender = sender;
		this._message = message;
		this._timestamp = timestamp;
	}
	
	public JanjaMessage(int status, String sender, String message, String timestamp){
		this._status = status;
		this._sender = sender;
		this._message = message;
		this._timestamp = timestamp;
	}
	
	public int getID(){
		return this._id;
	}
	
	public void setID(int id){
		this._id = id;
	}
	
	public int getStatus(){
		return this._status;
	}
	
	public void setStatus(int status){
		this._status = status;
	}
	
	public String getSender(){
		return this._sender;
	}
	
	public void setSender(String sender){
		this._sender = sender;
	}

	public String getMessage(){
		return this._message;
	}
	
	public void setMessage(String message){
		this._message = message;
	}
	
	public String getTimestamp(){
		return this._timestamp;
	}
	
	public void setTimestamp(String timestamp){
		this._timestamp = timestamp;
	}
}
