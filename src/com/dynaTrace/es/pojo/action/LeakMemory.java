package com.dynaTrace.es.pojo.action;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.JSON;
import com.dynaTrace.es.pojo.util.MemoryLeak;

public class LeakMemory extends POJOAction{

	public static String NUM_OBS = "NumObjects";
	public static String OBJ_SZ = "ObjectSize";
	private int numObjects;
	private double objectSize;
	private JSON output = new JSON();
	
	protected static Logger log = POJOLogger.getLogger(LeakMemory.class);
	static int MB = 1024*1024;

	double amountToLeak;

	HashMap<Object, Object> theLeak = new HashMap<Object, Object>();
	LeakInnerClassOne leakOne;
	public void setup(JSON p) throws Exception{
		super.setup(p);
		objectSize = p.getDouble(OBJ_SZ, 0.125) * MB;
		numObjects = p.getInt(NUM_OBS, 1000);
		
		setName(getName()+ "MB every "+minInterval+" ");
		output.put("Params", p);
	}
	public void runAction(){
		
		Runtime r = Runtime.getRuntime();
		long max = r.maxMemory();
		long total = r.totalMemory();
		if( total/max > .95 ){
			log.warning("Memory is too low to execute memory leak: "+getName());
			output.put("Warning", "Memory is too low to execute memory leak: "+getName());
			return;
		}
		
		
		leakOne = new LeakInnerClassOne();
		leakOne.callTwo(objectSize, numObjects, this);
	}
	
	public void addLeak(Object key, Object item){
		theLeak.put(key, item);
	}
	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return output;
	}
}

class LeakInnerClassOne
{
	LeakInnerClassTwo two;
	
	void callTwo(double objectSize, int numObjects, LeakMemory leak)
	{
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {}
		two = new LeakInnerClassTwo();
		two.callThree(objectSize, numObjects, leak);
	}
}

class LeakInnerClassTwo
{

	LeakInnerClassThree three;
	void callThree(double objectSize, int numObjects, LeakMemory leak)
	{
		three = new LeakInnerClassThree(leak);
		three.leak(objectSize, numObjects);
	}
}

class LeakInnerClassThree
{
	private static Logger log = LeakMemory.log;
	HashMap<Object, Object> xxx = new HashMap<Object, Object>();

	LeakMemory leak;
	LeakInnerClassThree(LeakMemory memLeak){
		leak = memLeak;
	}

	void leak(double objectSize, int numObjects)
	{
		Vector<Object> vct = new Vector<Object>();
		for(int i=0; i<numObjects; i++){
			vct.add(new MemoryLeak((int)objectSize));
		}

		// create a unique id for the map element
		xxx.put("x_"+System.currentTimeMillis(), vct);
				
		Runtime r = Runtime.getRuntime();
		double freeMem = (double)r.freeMemory()/1024.0;
		double maxMem = (double)r.maxMemory()/1024.0;
		double total = (double)r.totalMemory()/1024.0;
		NumberFormat nf = NumberFormat.getPercentInstance();

		log.info("Free: "+freeMem+"  Total: "+total+"  Max: "+maxMem);
		log.info("Pct of Max : "+ nf.format((freeMem/maxMem)));
		log.info("Pct of Total : "+ nf.format((freeMem/total)));
	}
}

