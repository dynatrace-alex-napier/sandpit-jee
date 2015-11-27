package com.dynaTrace.es.servlets;

// better than a null pointer exception
public class SandpitException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7462236152410931942L;

	public SandpitException(String msg, Exception parent){
		super(msg, parent);
		
	}
	
	public SandpitException(String msg){
		super(msg);
		
	}
}