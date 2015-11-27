/**
 * 
 */
package com.dynaTrace.es.pojo.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author alex
 *
 */
public class BasicQueue {
	protected BlockingQueue<Runnable> queue;
	protected ThreadPoolExecutor pool;

	BasicQueue(int sz, int maxSz, int ttl){
		queue = new LinkedBlockingQueue<Runnable>();
		pool = new ThreadPoolExecutor(sz, maxSz, ttl, TimeUnit.SECONDS, queue);
		pool.prestartAllCoreThreads();		
	}
}
