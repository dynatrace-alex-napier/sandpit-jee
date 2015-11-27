package com.dynaTrace.es.pojo.action.http;

import java.util.Vector;


public enum HttpMethod{
	get, post, put, delete, head, trace, options;

	private static final Vector<String> values = new Vector<String>();
	static{
		for(HttpMethod t: HttpMethod.values()){
			values.add(t.toString());	
		}			
	}
	
	public static boolean contains(String s){
		return values.contains(s);
	}
	
	public static HttpMethod get(String s, HttpMethod def){
		for(HttpMethod t: HttpMethod.values()){
			if( t.toString().equals(s) )
				return t;	
		}
		return def;
	}
	
	// defualt is get
	public static HttpMethod get(String s){
		return get(s, get);
	}
	
}
