package com.dynaTrace.es.pojo.action;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.DelayedQueue;
import com.dynaTrace.es.pojo.util.JSON;
import com.dynaTrace.es.pojo.util.PojoDelayed;

/**
 * 
 * Implementation creates a file from a string in a very inefficient manner
 * 
 * Builda string in a loop 
 *
 */
public class WriteFile extends POJOAction{

	public static String CLOSE_HANDLES = "CloseHandles";
	public static String ITERATIONS = "Iterations";
	public static String STR_LEN = "StringLength";
	private static Logger log = POJOLogger.getLogger(WriteFile.class);

	private boolean closeHandles = false;
	
	private int sz;

	private List<MyFileHandle> allFiles = new Vector<MyFileHandle>();
	
	public void setup(JSON p) throws Exception
	{
		super.setup(p);
		closeHandles = p.getBoolean(CLOSE_HANDLES, false);
		sz = p.getInt(STR_LEN, 1000);
		setName(getName()+" sz:"+sz+" interval:"+minInterval);
		
	}
	
	public void runAction()
	{
		final String fileName = UUID.randomUUID().toString() + "-" +
				UUID.randomUUID().toString() + ".tmp";
		long start = System.currentTimeMillis();
		String str = BadDeveloper.makeString(sz);
		log.info("String built in "+(System.currentTimeMillis()-start));
		
		final FileWriter fw;
		try {
			fw = new FileWriter(fileName);
			fw.write(str);
			
			final MyFileHandle myFileHandle = new MyFileHandle(fileName, fw);
			allFiles.add(myFileHandle);
			if( closeHandles ){
				fw.close();
			}  
			else{
				DelayedQueue.getInstance().add(new PojoDelayed(fw, ttl){
					public void run(){
						myFileHandle.cleanUp();
						synchronized(allFiles){ // do I need to sync this?
							allFiles.remove(myFileHandle);
						}						
					}
				});
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			

	}
	
	public void stopAction(){
		super.stopAction();
		synchronized(allFiles){
			for(MyFileHandle f : allFiles){
				f.cleanUp();
			}
		}
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}

class MyFileHandle{
	final File f;
	final FileWriter fw;
	final String fName;
	private static Logger log = POJOLogger.getLogger(MyFileHandle.class);
	
	MyFileHandle(String fName, FileWriter fw){
		this.fName = fName;
		this.f = new File(fName);
		this.fw = fw;
	}
	
	void cleanUp(){
		if( f.exists()){
			log.info("** Cleaning up "+fName);
			try {fw.close();} catch (IOException e) {}
			log.info("** Deleted ?? "+f.delete());
		}
	}
}
