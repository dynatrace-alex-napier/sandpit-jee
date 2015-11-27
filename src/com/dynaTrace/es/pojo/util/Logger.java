/**
 * 
 */
package com.dynaTrace.es.pojo.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dynaTrace.es.pojo.POJOServer;

/**
 * @author alex
 *
 * @todo - replace with java.util.logging
 * 
 * @depricated - using java.util.logging instead
 */
@SuppressWarnings("rawtypes")
public class Logger {
	
	static long start = System.currentTimeMillis();
	
	private String clsName;
	private String shortClsName;
	private DateFormat df = new SimpleDateFormat("HH:mm:ss.S");
	public static LogLevel LEVEL;

	private LogLevel level;
	static {
		LogLevel lvl = LogLevel.WARN;
		try{
			LEVEL = LogLevel.byValue(Integer.parseInt(System.getProperty(POJOServer.LOG_LVL)));
		}
		catch(NumberFormatException e){}
		setGlobalLevel(lvl);
	}
	
//	public void setup(JSON j){
//		setLevel(LogLevel.byString(j.getString(POJOServer.LOG_LVL, Logger.LEVEL.toString())));
//	}
	
	public boolean isInfo(){
		if( level.compareTo(LogLevel.INFO) <= 0 )
			return true;
		return false;
	}
	
	public boolean isDebug(){
		if( level.compareTo(LogLevel.DEBUG) <= 0 )
			return true;
		return false;
	}

	public boolean isWarn(){
		if( level.compareTo(LogLevel.WARN) <= 0 )
			return true;
		return false;
	}

	public static void setGlobalLevel(LogLevel lvl){
		LEVEL = lvl;
		System.err.println("Global Log Level set to "+LEVEL);
	}

//	public static void setGlobalLevel(String logLevel){
//		setGlobalLevel(LogLevel.byString(logLevel));
//	}
	
	
	public Logger(Class cls){
		this(cls, LEVEL);
		
	}
	
	
	public Logger(Class cls, LogLevel lvl){
		clsName = cls.getName();
		level = lvl;
		String pkg[] = clsName.split("\\.");
		shortClsName = pkg[pkg.length-1];
		
		System.err.println(clsName+" set to "+lvl);
			
	}
	

	public void setLevel(LogLevel lvl){
		level = lvl;
		
		System.err.println(" ## "+clsName+" LogLevel set to "+level);
	}	
	

	private boolean canWrite(LogLevel minLevel){
		return level.canWrite(minLevel);
	}
	public void err(String s, Exception e){
		if( canWrite(LogLevel.ERR) ) return;	
		writeErr(prefix() + " ERROR " + s);
		e.printStackTrace();
	}
	public void err(String s){
		if( canWrite(LogLevel.ERR) ) return;	
		writeErr(prefix() + " ERROR " + s);
	}

	public void warning(String s, Exception e){
		if( canWrite(LogLevel.WARN) ) return;	
		writeErr(prefix() + " WARN " + s);
		e.printStackTrace();
	}
	public void warning(String s){
		if( canWrite(LogLevel.WARN) ) return;	
		writeErr(prefix() + " WARN " + s);
	}
	
	public void info(String s, Exception e){
		if( canWrite(LogLevel.INFO) ) return;	
		write(prefix() + " INFO " + s);
		e.printStackTrace();
	}
	public void info(String s){
		if( canWrite(LogLevel.INFO) ) return;	
		write(prefix() + " INFO " + s);
	}
	
	public void finer(String s, Exception e){
		if( canWrite(LogLevel.DEBUG) ) return;	
		write(prefix() + " DEBUG " + s);
		e.printStackTrace();
	}
	public void finer(String s){
		if( canWrite(LogLevel.DEBUG) ) return;	
		write(prefix() + " DEBUG " + s);
	}

	private String prefix(){
		String TAB = "\t";
		return padRight(df.format(new Date()), 12)+TAB+
				padRight(Thread.currentThread().getName(), 16) +TAB +
					padRight(shortClsName, 14) + TAB; 
	}
	
	private String padRight(String s, int len){
		StringBuilder buf = new StringBuilder(len);
		int end = Math.min(len, s.length());
		buf.append(s.substring(0, end));
		while(buf.length() <= len){
			buf.append(" ");
		}
//		System.out.println(s + " len: "+len+ "  >"+buf.toString()+"<");
		return buf.toString();
	}

	// get the end of the string
	private String padLeft(String s, int len){
		StringBuilder buf = new StringBuilder(len);
		int start, end=s.length();
		if( len > end )
			start = 0;
		else
			start = end-len;
		buf.append(s.substring(start, end));
		while(buf.length() < len){
			buf.insert(0, " ");
		}
//		System.out.println(s + " len: "+len+ "  >"+buf.toString()+"<");
		return buf.toString();
	}
	
	private void write(String s){
		System.out.println(s);
	}
	private void writeErr(String s){
		System.out.println(s);
	}
	
	public static void main(String args[]){
		Logger l = new Logger(String.class);
		l.setLevel(LogLevel.byString("DEBUG"));
		l.finer("################### Don't write this");
		l.info("write this");
		l.warning("write this");
		l.err("write this");
	}

}
