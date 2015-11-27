package com.dynaTrace.es.pojo.action;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.DelayedQueue;
import com.dynaTrace.es.pojo.util.JSON;
import com.dynaTrace.es.pojo.util.MemoryLeak;
import com.dynaTrace.es.pojo.util.PojoDelayed;

public class CircularMemory extends POJOAction{
	public static String LEAK_SIZE = "LeakSize";
	protected static Logger log = POJOLogger.getLogger(CircularMemory.class);

	private int obSz;
	private int numObjects;
	private CircluarMemoryNode head;
	
	public void setup(JSON p) throws Exception{
		super.setup(p);
		
		setName(getName()+ "MB every "+minInterval+" ");
		
		obSz = (int)(p.getDouble("ObjectSize", 0) * (1024*1024)); //  size in MB becomes bytes
		log.warning("Object Size "+obSz);
		numObjects = p.getInt("NumberOfObjects", 100);

	}
	
	
	private double freeMemory(){
		Runtime rt = Runtime.getRuntime();
		long max = rt.maxMemory();
		long free = rt.freeMemory();
		long total = rt.totalMemory();
		double pct = ((double)free + max - total)/(double)max * 100;
		return pct;
	}
	
	@Override
	public void runAction(){
		// less than 3pct free don't run this
		if( freeMemory() < 3.0 ){
			log.warning("Memory is too low ("+freeMemory()+"%) to execute memory leak: "+getName());
			return;
		}
		log.info("Free Mem runAction       :"+freeMemory());
		
		CircluarMemoryNode next=null, prev=null;
		head = new CircluarMemoryNode(obSz);
		prev = head;
		for(int i=0; i<numObjects; i++){
			next = new CircluarMemoryNode(obSz);			
			prev.next = next;
			next.prev = prev;
			prev = next;
		}
		// close the loop
		if( head != null)
			head.prev = next;
		if( next != null)
			next.next=head;
		
		
		// add all the references
		DelayedQueue.getInstance().add(new PojoDelayed(head, ttl));
		log.info("Free Mem After runAction :"+freeMemory());
		
	}


	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	
}

class CircluarMemoryNode {

	CircluarMemoryNode prev;
	CircluarMemoryNode next;
	MemoryLeak leak;
	List<CircluarMemoryNode> others;
	
	CircluarMemoryNode(int sz){		
		leak = new MemoryLeak(sz);
	}
		
	/**
	 * 
	 * @param list
	 */
	void reference(List<CircluarMemoryNode> list){
		others = new ArrayList<CircluarMemoryNode>();
		others.addAll(list);
		others.remove(this);
	}
	
}

