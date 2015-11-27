package com.dynaTrace.es.pojo.action.transactions;

import com.dynaTrace.es.pojo.action.POJOAction;
import com.dynaTrace.es.pojo.util.JSON;

public class FilterTest extends POJOAction {

	
	String paramA="null";
	String paramB="null";
	String paramC="null";
	String paramD="null";
	public void setup(JSON p) throws Exception{
		super.setup(p);
		
		paramA = p.getString("methodA");
		paramB = p.getString("methodB");
		paramC = p.getString("methodC");
		paramD = p.getString("methodD");
	}
	
	JSON results = new JSON();
	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		results.put("Message", "Hi Adam");
		return results;
	}

	@Override
	public void runAction() throws Exception {
		// TODO Auto-generated method stub
		methodA("n/a");
	}
	
	private String methodA(String arg){
		return methodB(paramA);
	}

	private String methodB(String arg){
		return methodC(paramB);
		
	}
	private String methodC(String arg){
		return methodD(paramC);
		
	}
	private String methodD(String arg){
		return paramD;		
	}
}
