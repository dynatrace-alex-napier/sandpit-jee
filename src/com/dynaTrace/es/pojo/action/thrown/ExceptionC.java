package com.dynaTrace.es.pojo.action.thrown;

import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;

public class ExceptionC extends Exception{
	private static final long serialVersionUID = 0L;
	private static Logger log = POJOLogger.getLogger(ExceptionC.class);
	public ExceptionC(String s){
		super(s);
		log.severe("making new ExceptionC");
		throw new java.lang.RuntimeException("CCC: "+s);
	}
}