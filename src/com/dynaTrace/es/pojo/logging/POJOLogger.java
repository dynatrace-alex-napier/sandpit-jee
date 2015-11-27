package com.dynaTrace.es.pojo.logging;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@SuppressWarnings("rawtypes")
public class POJOLogger {

	private static HashMap<String, Handler> handlers = new HashMap<String, Handler>();
	private static Level baseLevel = Level.INFO;
	
	public static Logger getLogger(String className, Level lvl){
		Logger log = java.util.logging.Logger.getLogger(className);
		log.setLevel(lvl);
		// I don't think any handers are defined, not sure where these are set
		for(Handler h : log.getHandlers()){
			log.removeHandler(h);
		}
		try {
			String fileName = getLogFile(className);
			Handler fh = handlers.get(fileName);
			if( fh == null ){
				fh = new FileHandler(fileName, true);
				fh.setFormatter(new PlainFormatter());
				handlers.put(fileName, fh);
			}
			log.addHandler(fh);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return log;
	}

	public static Logger getLogger(Class c){
		
		return getLogger(c.getName(), getLevel(c));
	}

	public static Logger getLogger(Object o){
		Class c = o.getClass();
		return getLogger(c.getName()+o.hashCode(), getLevel(c));
	}
	
	public static void setLevel(String lvl){
		try{
			baseLevel = Level.parse(lvl);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		baseLevel = Level.WARNING;
	}

	// check the class to see what we should do with it
	private static Level getLevel(Class c){
		return baseLevel;
	}
	
	// maybe change the log file on class
	public static String getLogFile(String clsName){
		return "pojo-server.log";
	}

	
	private POJOLogger(){}
}

class PlainFormatter extends SimpleFormatter{

	private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	public String format(LogRecord rec) {
		StringBuffer buf = new StringBuffer(1000);

		buf.append(df.format(new Date(rec.getMillis())));
		buf.append('\t');
		buf.append(rec.getLevel());
		buf.append('\t');
		buf.append(rec.getSourceClassName());
		buf.append(':');
		buf.append(':');
		buf.append(rec.getSourceMethodName()); 
		buf.append("()\t");
		buf.append(formatMessage(rec));
		buf.append('\n');
		return buf.toString();
	}

	public String getHead(Handler h) {
		return "  << New log file started ... >> \n";
	}

	public String getTail(Handler h) {
		return " << Log file closing... >>\n";
	}

}
