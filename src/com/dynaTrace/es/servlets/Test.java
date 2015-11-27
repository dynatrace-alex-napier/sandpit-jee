package com.dynaTrace.es.servlets;

import java.awt.Robot;
import java.util.Random;
import java.util.Vector;

public class Test {

	public enum Tasks{
		browse, view, run, add, delete, modify;

		public boolean is(String s){
			return this.toString().equals(s);
		}
		private static final Vector<String> values = new Vector<String>();
		static{
			for(Tasks t: Tasks.values()){
				values.add(t.toString());	
			}			
		}
		public static boolean contains(String s){
			return values.contains(s);
		}
	}
	
	public Test() throws Exception{

		
		Robot r = new Robot();
		Random rn = new Random();
		int x=0, y=0;
		while( x < 1000 ){
			while( y++ < 800 ){
				r.mouseMove(x, y);
				Thread.sleep(rn.nextInt(1000)+10000);
			}
		}
	}

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		//new Test();
	}

}
