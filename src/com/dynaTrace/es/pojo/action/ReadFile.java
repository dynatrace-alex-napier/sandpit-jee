package com.dynaTrace.es.pojo.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.DelayedQueue;
import com.dynaTrace.es.pojo.util.JSON;
import com.dynaTrace.es.pojo.util.PojoDelayed;

/**
 * Read a file and potentially leak the file handles
 * File may be large and require some IO time
 *
 */
public class ReadFile extends POJOAction{

	public static String CLOSE_HANDLES = "CloseHandles";
	public static String FILE_SIZE = "FileSize";
	public static String ITERATIONS = "Iterations";
	
	private boolean closeHandles = false;
	private String fileName;
	private int iterations;
	
	public void setup(JSON p) throws Exception
	{
		super.setup(p);
		closeHandles = p.getBoolean(CLOSE_HANDLES, false);
		int fileSz = p.getInt(FILE_SIZE, 50);
		iterations = p.getInt(ITERATIONS, 1);
		fileName = "ReadFile_size-"+fileSz+"-KB.tmp";
		if( !new File(fileName).exists() ){
			FileWriter fw=null;
			try {
				fw = new FileWriter(fileName);
				String oneKBstr = BadDeveloper.makeString(1023);
				while(fileSz-- >0){
					fw.write(oneKBstr+"\n");
				}
			}
			finally{
				if( fw != null )
					fw.close();
			}
		}
		
		setName(getName()+ " file:"+fileName+" "+iterations+" "+(closeHandles?"Leaky":""));
	}
	
	
	public void runAction()
	{
		BufferedReader br=null;
		log.info("Read a file: "+fileName);
		for(int i=0; i<iterations; i++){
			try{
				br = new BufferedReader(new FileReader(fileName));
				StringBuilder buf = new StringBuilder();
				String line = br.readLine();
				while(line!= null){
					buf.append(line);
					line = br.readLine();
				}
			}
			catch(Exception e){
				e.printStackTrace();	
			}
			finally{
				if( closeHandles ){
					try {
						br.close();
						br = null;
					} catch (IOException e1) {}
				}
				else{
					DelayedQueue.getInstance().add(new PojoDelayed(br, ttl){
						public void run(){
							super.run();
							try{
								((BufferedReader)ob).close();
							}catch(Exception e){
								log.info("Tried to close the handle, failed: "+e);								
							}
						}
					});
				}		
			}
		}
		log.info("Read the file: "+fileName+ " "+iterations+" times");
	}
	
	public void stopAction(){
		super.stopAction();
		new File(fileName).delete();
	}


	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
