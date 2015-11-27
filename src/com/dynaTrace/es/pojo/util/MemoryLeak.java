package com.dynaTrace.es.pojo.util;

public class MemoryLeak {

	private StringBuilder buf;
	public MemoryLeak(int sz){
		buf = new StringBuilder(sz/2);
	}
}
