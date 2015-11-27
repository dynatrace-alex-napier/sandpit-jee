package com.dynaTrace.es.pojo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.action.POJOAction;
import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.DelayedQueue;
import com.dynaTrace.es.pojo.util.JSON;

public class POJOServer {
	// class that is being loaded
	public static String ACTION_TYPE = "className";
	public static String JOBS = "Jobs";
	public static String LOG_LVL = "LogLevel";
	// applies to the whole server, set to false to suspend activity
	public static String ALL_ACTIVE = "AllActive";
	// used globally and then on each action
	public static String ACTIVE = "Active";

	public static String CORE_SZ = "CoreSize";
	public static String MAX_SZ = "MaxSize";
	public static String TTL = "TTL";
	public static String QUEUE_INFO = "Queues";
	
	public static String MIN_EXEC_INTERVAL = "MinExecInterval";
	public static String MAX_EXEC_INTERVAL = "MaxExecInterval";
	public static String EXEC_INTERVAL = "ExecInterval"; 

	public static int DEF_EXEC_INTERVAL = 59; // use an odd number close to 1 minute so we know its the default being applied 

	public static String KEEP_ALIVE_FILE = "delete_this_to_stop_pojo_server";
	private static Logger log = POJOLogger.getLogger(POJOServer.class);
	private static Level rootLogLevel = Level.FINEST;

	private JSON json;
	private BlockingQueue<Runnable> queue;
	private ThreadPoolExecutor pool;

	private Vector<POJOAction> actions = new Vector<POJOAction>();

	public static void main(String[] args) {
		log.severe("POJO Server starting...");
		if (args.length == 0) {
			usage();
		}
		String configFile = args[0];
		log.warning("Config File is " + configFile);

		File cfgFile = new File(configFile);
		if (!cfgFile.exists()) {
			try {
				log.severe("Config File is missing: " + cfgFile.getCanonicalPath());
			} catch (IOException e) {
			}
			usage();
		}

		// did this while looking for the host name bug
		new Runner(configFile).run();

		log.warning("------------------------------------------------------");
		log.severe("POJO Server Finshed...");
		log.warning("------------------------------------------------------");
		System.exit(-1);
	}
	
	public static Level getLogLevel(){
		return rootLogLevel;
	}

	@SuppressWarnings("unchecked")
	public POJOServer(String fileName) {

		try {
			json = new JSON(new File(fileName));
			
			// get the global logging settings, any existing loggers will not be affected
			POJOLogger.setLevel(json.getString(LOG_LVL, "WARNING"));
			log = POJOLogger.getLogger(this.getClass());
			
			// if not active do nothing for now
			if (!json.getBoolean(ALL_ACTIVE)){
				log.warning("AllActive is false, no actions being created");
				return;
			}
			boolean globalActive = json.getBoolean(ACTIVE);
			List<Map<String, Object>> jobs = (List<Map<String, Object>>) json.get(JOBS);

			JSON queueSize = json.getSub(QUEUE_INFO);
			queue = new DelayQueue();
			pool = new ThreadPoolExecutor(queueSize.getInt(CORE_SZ, 20),
					queueSize.getInt(MAX_SZ, 20), queueSize.getInt(TTL, 60),
					TimeUnit.SECONDS, queue);
			pool.prestartAllCoreThreads();

			JSON defProps = new JSON(json);
			defProps.put(QUEUE_INFO, null);
			defProps.put(JOBS, null);
			
			for (Map<String, Object> jobMap : jobs) {

				// job is a new object based on defProps with job copied over it
				JSON job = new JSON(defProps.mixin(jobMap));
				// if there was no max in the jobMap then delete it so the default isn't used
				if( jobMap.get(MAX_EXEC_INTERVAL)==null)
					job.remove(MAX_EXEC_INTERVAL);
				
				String className = job.getString(ACTION_TYPE);
				if (className != null && job.getBoolean(ACTIVE, globalActive)) {
					log.warning("Loading action "+className);
					try {
						POJOAction act = (POJOAction) Class.forName(className)
								.newInstance();
						act.setup(job);
						act.setQueue(queue);
						actions.add(act);
						if (className.equals("Analyser")) {
							act.run();
						}
					} catch (Exception e) {
						log.severe("Can't load POJO Action " + className);
						log.severe("Reason : " + e.getMessage());
						e.printStackTrace();
					}
				}
				else{
					if( log.getLevel().intValue() <= Level.INFO.intValue() ){
						log.info("Not loading action "+className+" Details: "+job);
					}
					else{
						log.warning("Not loading action "+className);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			//
		}
	}

	public void shutdown() {
		log.warning("Shutting down Pojo Server Instance");
		// if not active do nothing for now
		if (json != null && json.getBoolean(ALL_ACTIVE)) {
			pool.shutdownNow().clear();
			log.info("pool shutdown");
			queue.clear();
			log.info("queue cleared");
			for (POJOAction action : actions) {
				action.stopAction();
				log.info("Action stopped " + action);
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		} else {
			log.info("Pojo not active, nothing to shutdown");
		}
		log.warning("Shutdown done");
	}

	public static void usage() {
		log.severe("Usage: POJOServer requires a JSON properties file like this ... ");
		log.severe("todo: create JSON ha ha...");
		System.exit(-1);
	}
}

class Runner {
	static Logger log = POJOLogger.getLogger(Runner.class);

	String configFile;

	Runner(String configFile) {
		this.configFile = configFile;
	}

	void run() {
		File runningFile = new File(POJOServer.KEEP_ALIVE_FILE);
		File cfgFile;
		long lastMod = 0;
		POJOServer server = null;
		try {
			runningFile.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (runningFile.exists()) {
			cfgFile = new File(configFile);
			if (lastMod < cfgFile.lastModified()) {
				lastMod = cfgFile.lastModified();
				if (server != null)
					server.shutdown();
				log.warning("------------------------------------------------------");
				log.warning("Properties file change detected, new config loading...");
				log.warning("------------------------------------------------------");
				try {
					server = new POJOServer(configFile);
				} catch (Exception e) {
					log.severe("Error creating new server, check the JSON file: " + e);
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (server != null)
			server.shutdown();
		DelayedQueue.getInstance().shutdown();

	}
}
