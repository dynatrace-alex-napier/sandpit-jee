package com.dynaTrace.es.pojo;

public class PojoException extends Exception{

	private static final long serialVersionUID = 7791887653858155727L;

	public PojoException(String desc){
		super(desc);
	}

	public PojoException(String desc, Exception ex){
		super(desc, ex);
	}
}
