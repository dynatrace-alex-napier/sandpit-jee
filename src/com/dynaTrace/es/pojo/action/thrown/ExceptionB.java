package com.dynaTrace.es.pojo.action.thrown;

import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;

public class ExceptionB extends Exception{
	private static final long serialVersionUID = 0L;
	private static Logger log = POJOLogger.getLogger(ExceptionB.class);
	public ExceptionB(String s){
		super(s);
		log.severe("making new ExceptionB");
		throw new java.lang.RuntimeException("BBB: "+s);
	}
}