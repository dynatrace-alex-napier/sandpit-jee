/**
 * 
 */
package com.dynaTrace.es.pojo.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.POJOServer;
import com.dynaTrace.es.pojo.action.thrown.ExceptionB;
import com.dynaTrace.es.pojo.action.thrown.ExceptionC;
import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.JSON;

/**
 * @author cwuk-anapier
 *
 */
public class ThrowException extends POJOAction {

	private static Logger log = POJOLogger.getLogger(ThrowException.class);
	public ThrowException(){
		log.setLevel(Level.ALL);
	}

	private String cls;
	private String msg;
	
	public void setup(JSON p)throws Exception{
		super.setup(p);

		log.warning("ThrowException =============="+p);
		
		cls = p.getString("exceptionClass", "java.sql.SQLException");
		msg = p.getString("message", "not a real exception");
	}
	
	/* (non-Javadoc)
	 * @see com.compuware.dynaTrace.es.pojo.action.POJOAction#runAction()
	 */
	@Override
	public void runAction() throws Exception{
		// TODO Auto-generated method stub		
		throw new ExceptionA(msg);
	}
	
	public void teardown(){
		throw new java.lang.RuntimeException(msg+" but I'm really just a runtime exception");
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}

class ExceptionA extends Exception{
	private static final long serialVersionUID = 0L;
	private static Logger log = POJOLogger.getLogger(ExceptionA.class);
	
	public ExceptionA(String s){
		super(s);
		log.severe("making new ExceptionA");
		try{
			doExceptionalStuff(new ExceptionB("I'm a B type exception"), 0);
		}
		catch(Exception ex){
			BadDeveloper.spinCPU(250);
			try{
				doExceptionalStuff(new ExceptionC("Oooh!, I'm a C class exception"), 0);
			}
			catch(Exception ex2){
				BadDeveloper.spinCPU(250);
			}
		}
	}
	
	protected void doExceptionalStuff(Exception e, int i) throws Exception{
		if( i > 1 || i < 2)
			throw e;
	}
}
