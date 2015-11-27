package com.dynaTrace.es.pojo.util;

public enum LogLevel {
	DEBUG(10),
	INFO(20),
	WARN(30),
	ERR(40),
	NONE(50);
	
	private final int index;
	LogLevel(int index){
		this.index = index;
	}
//	private int index(){return index;}
	
	public boolean canWrite(LogLevel other){
		return other.index < this.index;
	}
	
	public int compare2(LogLevel other){
		if( this.index == other.index )
			return 0;
		else if( this.index > other.index )
			return 1;
		return -1;
	}
	
	public static LogLevel byString(String s){
		if("INFO".equalsIgnoreCase(s))
			return INFO;
		else if("WARN".equalsIgnoreCase(s))
			return WARN;
		else if("DEBUG".equalsIgnoreCase(s))
			return DEBUG;
		else if("ERR".equalsIgnoreCase(s))
			return ERR;
		else if("ERROR".equalsIgnoreCase(s))
			return ERR;
		else if("NONE".equalsIgnoreCase(s))
			return NONE;
		return INFO;
	}

	public static LogLevel byValue(int v){
		if(v <= 10)
			return DEBUG;
		else if(v <= 20)
			return INFO;
		else if(v <= 30)
			return WARN;
		else if(v <= 40)
			return ERR;
		return NONE;
	}
	
}
