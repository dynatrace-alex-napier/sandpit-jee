package com.dynaTrace.es.servlets.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.dynaTrace.es.pojo.action.POJOAction;
import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.JSON;
import com.dynaTrace.es.servlets.SandpitException;
import com.dynaTrace.es.servlets.Task;

/*
 * @todo : add class level defaults, see comments in tasks.js
 */
public class TaskManager{
	
	private static Logger log = Logger.getLogger(TaskManager.class.getName());

	private JSON jobs;
	private JSON defs;
	
	static String fileName = System.getProperty("JSONJobFile");
	static{
		if( fileName == null ){
			fileName = "./jobFile.json";
		}
	}
	
	// for when the singleton gets made, and I'm not doing it now so
	// its thread safe and can be updated and executed at the same time
	// ie what happens if two requests hit this at the same time saying update the same job etc..
	public static TaskManager getInstance(){
		return new TaskManager();
	}
		
	// reads the file of jobs
	private TaskManager() {
		log.warning("Somebody, make me a singleton!");
		try{
			jobs = new JSON(new File(fileName));
			defs = jobs.getSub("DEFAULTS");			
		}
		catch(Exception e){
			log.warning("Cant read file "+new File(fileName).getAbsolutePath());
			throw new SandpitException(e.getMessage(), e);
		}
	}

	public JSON run(String jobName, HttpServletRequest req){
		log.info("running "+jobName);
		JSON job 	= getJob(jobName);
		JSON out 	= new JSON();
		JSON params = new JSON();
		if( job == null ){
			out.put("Error", "No task called: "+jobName);
		}
		else{
			Map<String, String[]> map = req.getParameterMap();
			for(String s : map.keySet() ){
				params.put(s, map.get(s)[0]);
			}
			
			
			// execute it
			out.put("TopJob", run(job, params));
			// execute the job list if one is present
			JSON jobList = job.getSub("jobs");
			if( jobList != null){
				JSON subRes = new JSON();
				out.put("SubJobs", subRes);
				for(String name: jobList.keySet()){
					log.info("running subJob: "+name);
					JSON nextJob = jobList.getSub(name);
					subRes.put(name, run(nextJob, params));
				}
			}				
		}
		
		return out;
	}
	
	private JSON run(JSON origJob, JSON params){
		
		JSON job = new JSON();
		log.info("enter run "+job.hashCode());
		// add all the 
		job.putAll(origJob);
		job.putAll(params);
		//long start = System.currentTimeMillis();

		String clsName = job.getString(Task.CLS_NAME);
		boolean async = job.getBoolean("async", false);
		boolean isDaemon = job.getBoolean("daemon", false);
		String jobName = clsName + "-" + job.hashCode();
		
		final JSON result = new JSON();
		//result.putAll(job);
		Runnable r = new Runnable(){
			private final long start = System.currentTimeMillis();
			public void run(){
				POJOAction act = null;
				try{
					log.info("Job "+jobName+" started");
					act = (POJOAction) Class.forName(clsName).newInstance();
					if( async ){
						log.info("set action as async");
						act.setAsync();
					}
					act.setup(job);
					act.runAction();
					
					result.putAll(act.getResultJSON());

				}
				catch(ClassNotFoundException clsex){
					log.severe("No class available for: "+clsName);
				}
				catch(Exception e){
					log.severe("Crap, its not working!  Don't know why, any ideas?");
					e.printStackTrace();
				}				
				log.info("Job "+jobName+" finished in "+(System.currentTimeMillis()-start));
				
				if( act != null ){
					act.teardown();
				}
			}
		};
		if( async ){
			result.put("Started", "Async job started at "+new Date());
			Thread t = new Thread(r);
			t.setDaemon(isDaemon);				
			t.start();
		}
		else{
			result.put("Started", "Sync job started at "+new Date());
			r.run();
			result.put("Completed", "Sync job ended at "+new Date());
		}
		log.info("exit run for "+job.hashCode());
		result.put("Async", async);
		result.put("Daemon", isDaemon);
		return result;		
	}
	

	public void create(String name, JSON job){
		log.info("create "+name+" JOB: "+job);
	}
	
	// returns a copy
	public JSON getJob(String name){
		JSON j = jobs.getSub(name);
		
		return new JSON(defs).mixin(j);
	}
	
	// returns a copy
	public JSON getJobs(){
		return new JSON(jobs);
	}
	
}

//class Trace extends ArrayList<String>{
//
//	private static final long serialVersionUID = 8949095161964568982L;
//
//	Trace(){};
//	
//	public boolean add(String s){
//		return super.add(new Date()+ " :: "+s);
//	}
//}