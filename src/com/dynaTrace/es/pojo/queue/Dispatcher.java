/**
 * 
 */
package com.dynaTrace.es.pojo.queue;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

/**
 * @author alex
 *
 */
public class Dispatcher {
	
	
	private static Dispatcher instance;
	private Dispatcher(){};
	public static Dispatcher getInstance(){
		if(instance==null){
			instance = new Dispatcher();
		}
		return instance;
	}
	
	private HashMap<String, BasicQueue> pools = new HashMap<String, BasicQueue>();
	
	/**
	 * 
	 * @param name - string name to reference the pool with
	 * @param sz - core size of the pool
	 * @param maxSz - maximum pool size
	 * @param ttl - time to live in seconds
	 */
	public void createQ(String name, int sz, int maxSz, int ttl){
		pools.put(name, new BasicQueue(sz, maxSz, ttl));
	}

	/**
	 * 
	 * @param name - name of the pool for refrence
	 * @param sz - core size of the pool
	 * 
	 * max pool size defaults to core size, ttl is 0
	 */
	public void createQ(String name, int sz){
		pools.put(name, new BasicQueue(sz, sz, 0));
	}

	public void addJobToQ(String name, Runnable r){
		pools.get(name).queue.add(r);
	}

	public BlockingQueue<Runnable> getQ(String name){
		return pools.get(name).queue;
	}
}

