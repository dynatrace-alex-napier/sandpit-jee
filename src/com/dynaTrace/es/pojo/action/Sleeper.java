package com.dynaTrace.es.pojo.action;

import java.util.Random;

import com.dynaTrace.es.pojo.util.JSON;

/**
 * 
 * Acts like a blocking thread, nice to have some IO wait here on a socket instead
 * 
 *
 */

public class Sleeper extends POJOAction
{
	private Random rand = new Random();
	
	// these are milliseconds
	public static String CYCLE = "Cycle";
	public static String MICRO_CYCLE = "MicroCycle";
	
	// is the sleep broken into periods
	public static String IS_BROKEN = "IsBroken";
	
	// values to be set from the JSON 
	private long cycle;
	private long microCycle;
	private boolean isBroken = false;
	
	public void setup(JSON p) throws Exception
	{
		super.setup(p);
		
		cycle = p.getInt(CYCLE, 50);			// msecs
		microCycle = p.getInt(MICRO_CYCLE, 5);	// msecs, less than auto sensor time
		
		isBroken = p.getBoolean(IS_BROKEN);
		
		setName(getName() + " ["+cycle+"/"+microCycle+"/"+isBroken+"]");
		log.finer("created "+getName());
	}
	
	public void runAction(){
		log.finer("start");
		if( isBroken ){
			long startedAt = System.currentTimeMillis();
			do{
				log.finer("Broken Sleep for "+brokenSleep()+" msecs.");
			}while( System.currentTimeMillis() < startedAt+cycle );
		}
		else{
			log.finer("Deep Sleep for "+deepSleep()+" msecs.");
		}
		log.finer("end");
	}

	// all of this just creates a stack trace with some depth to see where the time goes
	private long brokenSleep(){
		return microSleep() + spindlesSleep();
	}
	
	private long spindlesSleep(){
		return microSleep() + deltaWaves();
	}
	
	private long deltaWaves(){
		return microSleep() + deltaSleep();
	}
	
	private long deltaSleep(){
		return microSleep() + rem();
	}

	private long rem(){
		return microSleep();
	}
	
	private long microSleep(){
		// if you pass a value too big then bad luck this will throw an exception, better than waiting 25 days and 20 hrs
		return sleep(microCycle+rand.nextInt((int)microCycle));
	}
	
	private long deepSleep(){
		return sleep(cycle);
	}

	private long sleep(long t){
		try {
			Thread.sleep(t);
		} 
		catch (InterruptedException e) {}						
		return t;
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
