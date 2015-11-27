package com.dynaTrace.es.pojo.action;

import java.util.Date;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.JSON;

public class SpinCPU extends POJOAction{

	protected static Logger log = POJOLogger.getLogger(SpinCPU.class);
	public static String DURATION = "Duration";
	public static String NUM_THREADS = "NumThreads";

	protected int duration = 1000;
	protected int numThreads = 8;
	
	public void setup(JSON p) throws Exception
	{
		super.setup(p);
		duration = p.getInt(DURATION, duration);
		numThreads = p.getInt(NUM_THREADS, numThreads);
		
		setName(getName()+" on "+numThreads+" threads for "+duration+" msecs.");
	}
	
	public void runAction()
	{
		new SpinCPULevelOne(this).methodA();
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}


}

// this complexity just creates some stack depth
class SpinCPULevelOne{
	Logger log = SpinCPU.log;
	SpinCPU action;
	
	SpinCPULevelOne(SpinCPU action){
		this.action = action;
	}
	void methodA(){
		methodB();
	}
	
	void methodB(){
		SpinCPULevelTwo class2 = new SpinCPULevelTwo(action);
		class2.methodOne();
	}
}

class SpinCPULevelTwo{		

	Logger log = SpinCPU.log;
	int runningThreads = 0;
	int duration;
	int nThreads;
	SpinCPU action;
	
	SpinCPULevelTwo(SpinCPU action){
		this.duration = action.duration;
		this.nThreads = action.numThreads;
		this.action = action;
	}

	void  methodOne(){
	
		runningThreads = nThreads;
		while( nThreads-- > 0){
			Thread t = new Thread(new Runnable(){
						public void run(){
							methodTwo();
							log.finer("spin starts "+runningThreads);
							BadDeveloper.spinCPU(duration);		
							log.finer("spin complete "+runningThreads);
							runningThreads--;
						}
					}, "SpinCPU_Thread_"+nThreads+"_"+new Date());
			t.start();
		}
		while(runningThreads > 0 && !action.isStopped()){
			try {
				Thread.sleep(2000);
				log.finer("waiting for complete "+runningThreads);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	void methodTwo(){
	}
}
