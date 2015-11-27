package com.dynaTrace.es.pojo.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.dynaTrace.es.pojo.action.IPOJOAction;
import com.dynaTrace.es.pojo.action.POJOAction;
import com.dynaTrace.es.pojo.logging.POJOLogger;

/**
 * This singleton holds a reference to any object for a specified time
 * Override  Delayed.run to perform some action when the reference is released
 * @author cwuk-anapier
 *
 */
public class DelayedQueue {
	static java.util.logging.Logger log = POJOLogger.getLogger(DelayedQueue.class);
	private SortedMap<Long, Delayed> items;
	private static DelayedQueue instance = new DelayedQueue();

	private BlockingQueue<Runnable> queue;
	private ThreadPoolExecutor pool;
	
	private boolean running = true;
	private DelayedQueue(){
		queue = new DelayQueue();
		pool = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, queue);
		pool.prestartAllCoreThreads();
	}
	
	public synchronized boolean isRunning(){
		return running;
	}

	
	public synchronized void shutdown(){
		pool.shutdownNow().clear();
		queue.clear();
	}
	
	public static DelayedQueue getInstance(){
		return instance;
	}
	
	/**
	 * 
	 * @param object the reference to hold
	 * @param ttl the time in seconds to keep the reference
	 */
	public synchronized void add(PojoDelayed item){
		queue.add(item);
	}
	
	public static void main(String args[]) throws InterruptedException{
		DelayedQueue q = DelayedQueue.getInstance();
		log.setLevel(Level.ALL);
		log.info("STarted");
		q.add(new PojoDelayed("A 3", 3));
		q.add(new PojoDelayed("A 2", 2));
		q.add(new PojoDelayed("A 4", 4));
		
		Thread.sleep(6000);
		q.shutdown();
		log.info("done");
	}
	
		
}


