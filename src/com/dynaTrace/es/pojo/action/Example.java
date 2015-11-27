/**
 * 
 */
package com.dynaTrace.es.pojo.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.POJOServer;
import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.JSON;

/**
 * @author cwuk-anapier
 *
 */
public class Example extends POJOAction {

	private static Logger log = POJOLogger.getLogger(Example.class);
	public Example(){
		log.setLevel(Level.ALL);
	}
	
	/* (non-Javadoc)
	 * @see com.compuware.dynaTrace.es.pojo.action.POJOAction#runAction()
	 */
	@Override
	public void runAction() {
		// TODO Auto-generated method stub
		log.warning("WARN Message - " + level1());
	}

	public String level1(){
		BadDeveloper.spinCPUs(100, 8);
		return level2();
	}
	
	public String level2(){
		BadDeveloper.spinCPUs(200, 4);
		return level3();
	}

	public String level3(){
		BadDeveloper.spinCPUs(400, 6);
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

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
