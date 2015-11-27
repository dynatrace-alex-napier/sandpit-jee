package com.dynaTrace.es.pojo.action;

import java.util.Date;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.JSON;

/**
 * This class will never return it just takes the thread from the pool and locks it
 * @author cwuk-anapier
 *
 */


public class Deadlock extends POJOAction
{	
	public static String ITERATIONS = "Iterations";
	private static Logger log = POJOLogger.getLogger(Deadlock.class);
	
	private int maxIter, count=1;
	public void setup(JSON p) throws Exception{
		super.setup(p);
		maxIter = p.getInt(ITERATIONS, 1);
	}
	
	public void runAction()	{
		// see http://examples.oreilly.com/jenut/Deadlock.java
		if( maxIter-- <= 0 ){
			log.info("stopping deadlocker after "+ json.getInt(ITERATIONS)+ " iterations");
			this.stopAction();
			return;
		}
		log.info("Deadlocking "+count++ +" thread(s)");
	    final Object lockA = new Object();
	    final Object lockB = new Object();
	    
	    // Lock A then B
		Thread t1 = new Thread("Deadlocker_"+new Date()) {
			public void run(){
				synchronized(lockA){
					log.info("Thread 1: locked A");
					try { Thread.sleep(50); } catch (InterruptedException e) {}
					synchronized(lockB){
						log.info("Thread 1: locked B");
					}
				}
			}
		};
	    
		// Lock B then A
		Thread t2 = new Thread("Thread_2_"+new Date()){
			public void run() {
				synchronized(lockB) {
					log.info("Thread 2: locked B");
					try { Thread.sleep(50); } catch (InterruptedException e) {}
					synchronized(lockA) {
						log.info("Thread 2: locked A");
					}
				}
			}
		};
		
		t1.start(); 
		t2.start();
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
