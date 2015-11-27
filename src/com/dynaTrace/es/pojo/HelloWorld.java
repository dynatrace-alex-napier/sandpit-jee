package com.dynaTrace.es.pojo;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.dynaTrace.es.pojo.util.JSON;



public class HelloWorld 
{

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		
		new HelloWorld();
	}
	
	HelloWorld() throws Exception{
		JSON json = new JSON(new File("C:\\JavaSrc\\easyWebServer\\tasks.js"));
		
		System.out.println(json.toString());
	}
	
	void x() throws IOException{
		String KEEP_ALIVE_FILE = "delete_this_to_stop_"+this.getClass().getName();
		File runningFile = new File(KEEP_ALIVE_FILE);
		runningFile.createNewFile();
		Random r = new Random();
		while(runningFile.exists()){

			level1();
			sleep(r.nextInt(2500)+500);
		}
		log("Finished");
	}
	
	public String level1(){
		spinCPUs(100, 8);
		return level2();
	}
	
	public String level2(){
		spinCPUs(200, 4);
		return level3();
	}

	public String level3(){
		spinCPUs(400, 6);
		return ""+new Date();
	}

	void sleep(long t){
		log("Sleeping for "+t+" msecs");
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			log("sleep interrupted");
		}
	}
	
	private DateFormat df = new SimpleDateFormat("HH:mm:ss.S");
	void log(String s){
		System.out.println(df.format(new Date())+" - "+ s);
	}

	private long spinCPUs(long t, int nThreads){
		log("spinCPUs on "+nThreads+" for "+t+" msecs");
		final long duration = t;
		long start = System.currentTimeMillis();
		while( nThreads-- > 0){
			new Thread(new Runnable(){
				public void run(){
					spinCPU(duration);
				}
			}).start();
		}
		return System.currentTimeMillis()-start;
	}
	
	public void spinCPU(long msecs){
		long end = System.currentTimeMillis() + msecs;
		while(System.currentTimeMillis() < end){
			int x=0;
			for(int i=0; i<100000; i++){
				x+=2;
			}
		}
	}	
	
}