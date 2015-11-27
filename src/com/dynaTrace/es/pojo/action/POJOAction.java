package com.dynaTrace.es.pojo.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.POJOServer;
import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.DelayedQueue;
import com.dynaTrace.es.pojo.util.JSON;
import com.dynaTrace.es.pojo.util.PropertiesFile;


public abstract class POJOAction implements IPOJOAction{
	
	public static String START_DELAY = "StartDelay";
	
	protected Logger log = POJOLogger.getLogger(POJOAction.class);
	protected int minInterval;
	protected int maxInterval;

	protected JSON json;
//	protected JSON result = new JSON();
	protected String name="Untitled";
	protected PropertiesFile propFile;
	protected int ttl;

	private int numCalls = 0;
	private long nextExec = 0;
	private boolean stopped = false;
	private boolean active;
	private DateFormat df = new SimpleDateFormat("HH:mm:ss.S");
	
	// use this for anything that needs random
	protected Random rand = new Random();
	
	
	private BlockingQueue<Runnable> queue;
	
	public int compareTo(Delayed o){
		POJOAction other = (POJOAction)o;
		if(other.nextExec < nextExec)
			return 1;
		else if(other.nextExec > nextExec)
			return -1;
		return 0;
	}

	public String getName(){
		return name;
	}

	public void setName(String n){		
		name = n;
	}

	public void stopAction(){
		stopped = true;
	}
	
	public boolean isStopped(){
		return stopped;
	}
	
	protected boolean isAsync = false;
	public void setAsync(){
		isAsync = true;
	}
	

	public void setup(JSON p) throws Exception{
		log = POJOLogger.getLogger(this.getClass()); // make it use the concrete implementation class name
		log.setLevel(POJOServer.getLogLevel());

		json = p;

		minInterval = json.getInt(POJOServer.MIN_EXEC_INTERVAL, POJOServer.DEF_EXEC_INTERVAL);		
		maxInterval = json.getInt(POJOServer.MAX_EXEC_INTERVAL, minInterval);		
		
		active = json.getBoolean("Active", true);		
		log.info("Processing Interval min: "+minInterval+" to max: "+maxInterval);
		
		// TTL is time the reference is kept for, after this its released and should be GC'd
		// set to 0 to keep forever
		ttl = p.getInt("TTL", 300); 		

		String clsName = getClass().getName();
		clsName = clsName.substring(clsName.lastIndexOf(".")+1, clsName.length());
		setName(clsName);
	}
	
	// override to do any cleanup
	public void teardown(){
		
	};
	
		
	public void setActive(boolean isActive){
		active = isActive;
	}
	
	// hide the implementation from the callers  
	protected abstract JSON getResult();
	
	// return the output created but this job, if any
	// most actions have none because i only just added this feature, see HttpClient for example
	public JSON getResultJSON(){
		JSON j = getResult();
		if( j == null ){
			j = new JSON();
			log.warning("Created blank JSON for action return, please fix this...");
		}
		return j; 
	}	
	
	public void setQueue(BlockingQueue<Runnable> q){
		queue = q;
		this.nextExec = System.currentTimeMillis() + json.getInt(START_DELAY, 0) * 1000;
		queue.add(this);
	}
	
	
	public POJOAction dispatchIn(){
		log.finer("Max "+maxInterval+"  Min: "+minInterval);
		if( maxInterval <= minInterval ){
			nextExec = System.currentTimeMillis() + minInterval*1000;
		}
		else{ // max is bigger
			int period = rand.nextInt(maxInterval - minInterval) + minInterval;
			nextExec = System.currentTimeMillis() + period*1000;
		}
		long diff = nextExec - System.currentTimeMillis();
		log.info("Next run at "+df.format(new Date(nextExec))+" in "+diff+"msecs");
		return this;
	}
	

	public long getDelay(TimeUnit unit){
		return nextExec - System.currentTimeMillis();
	}
	
	public void run(){		
		if( !active ){
			log.info(getName() + " is not active");
			return;
		}
		double start = System.currentTimeMillis();

		try{
			runAction();
		}
		catch(Exception e){
			log.warning("Caught exception: "+e);
		}
		
		numCalls++;
		double end = System.currentTimeMillis();
		log.warning("Action complete in ("+(end-start)/1000.0+" secs).  Invoked "+numCalls+" times");
		
		// add back into the queue
		if(!stopped)
			queue.add(dispatchIn());
	}
	
	
	
	public abstract void runAction() throws Exception;
	
}
