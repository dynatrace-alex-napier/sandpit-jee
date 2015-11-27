package com.dynaTrace.es.pojo.util;

import com.dynaTrace.es.pojo.logging.POJOLogger;

public class BadDeveloper {

	private static java.util.logging.Logger log = POJOLogger.getLogger(BadDeveloper.class);
	
	// since strings are immutable this will cause a lot of effort
	// for big values of len
	public static String makeString(int len){
		long start = System.currentTimeMillis();
		String result = "";
		while(len-- > 0){
			result += "X";
		}
		log.info("Created String in "+(System.currentTimeMillis()-start)+" msecs");
		return result;
	}
	
	public static void sleep(long t){
		try {Thread.sleep(t);} catch (InterruptedException e) {}
	}
	
	// do at least one in the current thread
	public static long spinCPUs(long t, int nThreads){
		return spinCPUs(t, nThreads, false);
	}	
	
	// call with async=true to return immediately otherwise it blocks for t msecs.
	public static long spinCPUs(long t, int nThreads, boolean async){
		log.info("spinCPUs on "+nThreads+" for "+t+" msecs");
		final long duration = t;
		long start = System.currentTimeMillis();
		while( nThreads-- > (async?0:1) ){
			new Thread(new Runnable(){
				public void run(){
					spinCPU(duration);
				}
			}).start();
		}
		if( !async ){
			// do this one in the current thread so it blocks
			spinCPU(duration);
		}
		return System.currentTimeMillis()-start;
	}
	
	public static void spinCPU(long msecs){
		long end = System.currentTimeMillis() + msecs;
		while(System.currentTimeMillis() < end){
			int x=0;
			for(int i=0; i<100000; i++){
				x+=2;
			}
		}
	}		
}
