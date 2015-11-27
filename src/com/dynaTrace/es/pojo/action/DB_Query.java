package com.dynaTrace.es.pojo.action;

import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.JSON;

public class DB_Query extends DB_Handler
{
	private static Logger log = POJOLogger.getLogger(DB_Query.class);
	private String props;
	private String where;
	
	public void setup(JSON p) throws Exception{
		super.setup(p);
		props = p.getString("Properties", "*");
		where = p.getString("Where", "");
		
		setName(getName()+ " props:"+props+" "+where+" "+super.toString());
	}
	
	public void runAction()
	{
		// tries to create a database table
		String sql = "select "+props+" from "+getTableName()+" "+where;
		log.info("Running SQL: "+sql);
		int sz =0;
		try
		{
			long start = System.currentTimeMillis();
			for(int i=0; i<iterations; i++)
				sz += query(sql);
			long end = System.currentTimeMillis();
			
			log.info("Total Sql returned "+sz+" in "+ (end-start)+" msecs.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
