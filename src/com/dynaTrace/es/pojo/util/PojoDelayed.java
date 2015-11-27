package com.dynaTrace.es.pojo.util;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.dynaTrace.es.pojo.action.IPOJOAction;
import com.dynaTrace.es.pojo.logging.POJOLogger;

public class PojoDelayed implements IPOJOAction{
	private static java.util.logging.Logger log = POJOLogger.getLogger(PojoDelayed.class);

	long nextExec;
	protected Object ob;
	
	public PojoDelayed(Object o, int ttl){
		nextExec = System.currentTimeMillis() + ttl*1000;
		ob = o;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		log.info(" run called "+ob);
	}

	public long getDelay(TimeUnit unit){
		return nextExec - System.currentTimeMillis();
	}
	
	public int compareTo(Delayed o){
		PojoDelayed other = (PojoDelayed)o;
		if(other.nextExec < nextExec)
			return 1;
		else if(other.nextExec > nextExec)
			return -1;
		return 0;
	}
	
}
