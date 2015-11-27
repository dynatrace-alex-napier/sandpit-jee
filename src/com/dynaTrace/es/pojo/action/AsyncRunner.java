package com.dynaTrace.es.pojo.action;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.JSON;

public class AsyncRunner extends POJOAction {

	protected static Logger log = POJOLogger.getLogger(AsyncRunner.class);
	long duration = 5000l;
	long respTime = 250l;
	int depth = 3;
	
	
	
	@Override
	public void runAction() throws Exception {
		// TODO Auto-generated method stub
		log.info("Start Async");
		new AsyncClass(depth, duration);
		Thread.sleep(respTime);
		log.info("End Async");
		
	}
	
	public void setup(JSON p)throws Exception{
		super.setup(p);
		duration = p.getInt("Duration", 2500);
		depth = p.getInt("Depth", 3);
		respTime = p.getInt("ResponseTime", 500);
		
	}
	
	public static void main(String args[]) throws Exception{
		AsyncRunner r = new AsyncRunner();
		r.depth = 5;
		r.duration = 1000;
		//r.runAction();
		
		double f = 0.11;
		long t = 100;
		Random rnd = new Random();
		long sum=0, max=Long.MIN_VALUE, min=Long.MAX_VALUE;
		int C = 1000;
		for(int i=0; i<C; i++){
			// vary t by +/- 10%
			long t2 = t+(long)(t*(rnd.nextDouble()*2*f-f));
			sum += t2;
			min = Math.min(min, t2);
			max = Math.max(max, t2);
		}
		log.info("Sum:"+sum+"\nMin:"+min+"\nMax:"+max+"\nAvg:"+(sum/C));
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}

class AsyncClass{
	protected static Logger log = POJOLogger.getLogger(AsyncClass.class);
	AsyncClass(int depth, long duration){
		final int fDepth = depth;
		final long fDuration = duration;
		final AsyncClass c = this;
		final Thread t = new Thread(new Runnable(){
			public void run(){
				log.info("Depth is: "+fDepth);
				if( fDepth <= 0){
					c.run(fDuration);
				}
				else{
					c.run(70);
					new AsyncClass(fDepth-1, fDuration);
				}
			}
		}, "AsyncRunner_"+this.hashCode()+"_"+depth+"_"+duration);
		t.start();		
	}
	
	private Random rnd = new Random();
	private void run(long t){
		double f = 0.1;
		// vary t by +/- f
		long t2 = t+(long)(t*(rnd.nextDouble()*2*f-f));
		try {
			log.info("sleeping for "+t2);
			Thread.sleep(t2);
		} catch (InterruptedException e) {}
		
	}
}
