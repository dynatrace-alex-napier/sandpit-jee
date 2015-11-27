package com.dynaTrace.es.pojo.action;

import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.JSON;

public class AutoSensorTest extends POJOAction {

	private int one = 100;
	private int two = 100;
	private int three = 100;
	private int four = 100;
	
	public void setup(JSON p) throws Exception{
		super.setup(p);
		one = p.getInt("One", one);
		two = p.getInt("Two", two);
		three = p.getInt("Three", three);
		four = p.getInt("Four", one);
	}
	@Override
	public void runAction() {
		// TODO Auto-generated method stub
		methodOne();
	}
	
	public void methodOne(){
		methodOneSub();	
		methodTwo();
	}

	public void methodTwo(){
		methodTwoSub();			
		methodThree();
	}
	public void methodThree(){
		methodThreeSub();			
		methodFour();
	}
	
	public void methodFour(){
		methodFourSub();		
	}
	
	private void methodOneSub(){
		// place sensor here so we get this
		BadDeveloper.spinCPUs(one, 4);
	}
	private void methodTwoSub(){
		BadDeveloper.spinCPUs(two, 4);
	}
	private void methodThreeSub(){
		BadDeveloper.spinCPUs(three, 4);
	}
	private void methodFourSub(){
		BadDeveloper.spinCPUs(four, 4);
	}
	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
