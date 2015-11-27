package com.dynaTrace.es.pojo.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.JSON;

public class JavaUtilLogger extends POJOAction {

	private String FINEST = "Finest log level  ";
	private String FINER = "Finer log level  ";
	private String FINE = "Fine log level  ";
	private String INFO = "* Info log level  ";
	private String WARN = "** Warn log level  ";
	private String SEVERE = "*** Severe log level  ";
	
	private Logger log = POJOLogger.getLogger(this);
	
	public void setup(JSON js) throws Exception{
		super.setup(js);
		log.setLevel(js.getLogLevel());
		setName("JavaUtilLogger::"+hashCode());
	}
	
	@Override
	public void runAction() {
		//log.severe("Logger is "+log.hashCode()+"  object is "+this.hashCode());
		log.finest (FINEST+getName());
		log.finer  (FINER+getName());
		log.fine   (FINE+getName());
		log.info   (INFO+getName());
		log.warning(WARN+getName());
		log.severe (SEVERE+getName());
		
//		log.log(Level.FINEST, 	FINEST, new Exception(FINEST));
//		log.log(Level.FINER, 	FINER, 	new Exception(FINER));
//		log.log(Level.FINE, 	FINE, 	new Exception(FINE));
//		log.log(Level.INFO, 	INFO, 	new Exception(INFO));
//		log.log(Level.WARNING, 	WARN, 	new Exception(WARN));
//		log.log(Level.SEVERE, 	SEVERE, new Exception(SEVERE));
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
