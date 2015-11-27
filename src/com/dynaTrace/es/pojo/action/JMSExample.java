package com.dynaTrace.es.pojo.action;


import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.JSON;


public class JMSExample extends POJOAction {

	protected static Logger log = POJOLogger.getLogger(JMSExample.class);
	long duration = 5000l;
	long respTime = 250l;
	int depth = 3;
	
	
	
	@Override
	public void runAction() throws Exception {
		// TODO Auto-generated method stub
		log.info("Start JMS Example");
		BadDeveloper.sleep(1200);
		log.info("End JMS Example");
		
	}
	
	public void setup(JSON p)throws Exception{
		super.setup(p);
	}
	
	public static void main(String args[]) throws Exception{
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		JSON json = new JSON();
		json.put("Message", "Hi Flo!");
		return json;
	}

}

