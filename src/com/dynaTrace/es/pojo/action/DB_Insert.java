package com.dynaTrace.es.pojo.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.JSON;



public class DB_Insert extends DB_Handler
{
	private static Logger log = POJOLogger.getLogger(DB_Insert.class);
	
	public void setup(JSON p) throws Exception{
		super.setup(p);
		
		setName(getName()+ " "+super.toString());
	}
	
	public void runAction()
	{
		String s1 = newStr(5+(int)(Math.random()*250));
		String s2 = newStr(5+(int)(Math.random()*250));
		String s3 = newStr(5+(int)(Math.random()*250));
		DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss.S");
		String dt = df.format(new Date());
		
		// tries to create a database table
		String sql = "insert into "+getTableName()+ " select max(number)+1 number, " +
				"'"+s1+"' string1, " +
				"'"+s2+"' string2, " +
				"'"+s3+"' string3 , " +
				"'"+dt+"' date from "+getTableName();
		
		log.info("Running SQL: "+sql);
		try
		{
			int res = execute(sql);
			log.info("Insert returned "+res);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
