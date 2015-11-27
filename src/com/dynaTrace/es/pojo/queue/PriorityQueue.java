/**
 * 
 */
package com.dynaTrace.es.pojo.queue;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;


/**
 * @author alex
 *
 */
public class PriorityQueue {
	
	private static Logger log = POJOLogger.getLogger(PriorityQueue.class);
	
	// will be a key tuning parameter, maybe set to 1000's on high volume systems
	// to avoid memory re-allocations
	// this is the list of events at each levels
	private static int INIT_Q_SZ = 5;
	
	// this is the number of levels, 
	// ie queues = Vector<Vector<Runnable>[INIT_Q_SZ]()>[INIT_QS_SZ]()
	private static int INIT_QS_SZ = 5;
	
	private int minPriority = Integer.MAX_VALUE;
	private int maxPriority = 0;
	private int numItems = 0;

	// hold all the jobs to run
	private Vector<Vector<Runnable>> queues;
	
	// this is the actual feeder queue into the pool
	private BlockingQueue<Runnable> queue;
	protected ThreadPoolExecutor pool;
	private Thread thread;
	
	public PriorityQueue(int sz){
		this(sz, sz, 0, INIT_QS_SZ);
	}

	public PriorityQueue(int sz, int maxSz, int ttl, int maxPrio){

		queues = new Vector<Vector<Runnable>>(INIT_QS_SZ);
		reallocQueues(INIT_Q_SZ);
		queue = new LinkedBlockingQueue<Runnable>();
		pool = new ThreadPoolExecutor(sz, maxSz, ttl, TimeUnit.SECONDS, queue);
		pool.prestartAllCoreThreads();
		
		thread = new Thread(new Runnable(){
			public void run(){
				while( 1 != 2 ){
					log.finer(" main loop ...");
					queueEvents();
					
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}
	
	private void reallocQueues(int sz){
		int top = queues.capacity();
		log.finer("reallocQueues sz: "+sz+ " top: "+top);
		queues.setSize(sz);
		for(int i=0; i<sz; i++){
			Vector<Runnable> q = queues.get(i);
			if( q == null ){
				log.finer("realloc "+i);
				queues.setElementAt(new Vector<Runnable>(INIT_Q_SZ), i);
			}
		}
		int i=0;
		for(Vector<Runnable> q : queues){
			if( q != null ){
				log.finer(i+ " - q len: "+q.size());
			}
			else{
				log.finer(i+" - q is null ");
			}
			i++;
		}
	}
	
	private synchronized void queueEvents(){
		try {
			if( numItems == 0){
				log.finer(" priority queue is waiting");
				wait();
				log.info(" priority queue is awake");
			}
		} catch (InterruptedException e) {
			log.finer(" priority queue interupted..." + e);			
		}
		int qNum=0;
		for(Vector<Runnable> q : queues){
			if( qNum++ > maxPriority){
				// we are finished ...
				log.finer("max queue has been processed, breaking loop");
				break;
			}

			if( q == null || q.size()==0 ) continue;
			log.info("processing queue " + qNum + " with "+q.size()+" items");
			numItems-=q.size();
			queue.addAll(q);
			q.clear();
			log.finer("queuing items, "+numItems+" remain");
			long nanos = System.nanoTime();
			int x = 0;
			while(queue.size() > 0 ){
				try {
					Thread.sleep(0, 100);
				} catch (InterruptedException e) {}
				//buf.append(".");
				x++;
			}
			log.info(x + " Loop T : "+(System.nanoTime()-nanos));
		}
		// reset counters
		setCounters();
	}
	
	/**
	 * add this item at the correct priority
	 * 
	 */
	public synchronized void add(Runnable r, int priority){
		log.finer(" in add "+priority );
		if( queues.size() <= priority ){
			reallocQueues(priority*2);
			log.finer("Priority Queue increased to "+priority);
		}
		Vector<Runnable> items = queues.get(priority);
		items.add(r);
		numItems++;
		minPriority = Math.min(minPriority, priority);
		maxPriority = Math.max(maxPriority, priority);
		log.finer("minP: "+minPriority+"  maxP: "+maxPriority);
		notify();
	}
	
	private void setCounters(){
		numItems = 0;
		minPriority = Integer.MAX_VALUE;
		maxPriority = 0;
	}
	
}
