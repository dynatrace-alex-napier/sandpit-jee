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
public class Breakdown extends POJOAction{

	public static String CPU 		= "cpu";
	public static String WAIT 		= "wait";
	public static String BLOCKED 	= "blocked";
	public static String IO 		= "io";

	private int cpu 	= 1000;
	private int io 		= 1000;
	private int wait 	= 1000;
	private int blocked = 1000;
	
	public void setup(JSON p) throws Exception{
		super.setup(p);
		cpu 	= p.getInt(Breakdown.CPU, cpu);
		io 		= p.getInt(Breakdown.IO, io);
		wait 	= p.getInt(Breakdown.WAIT, wait);
		blocked = p.getInt(Breakdown.BLOCKED, blocked);
	}
	
	
	public void runAction(){
		
	}
	
	public void stopAction(){
		super.stopAction();
	}


	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
