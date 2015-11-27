package com.dynaTrace.es.pojo.util;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;




import com.dynaTrace.es.pojo.PojoException;
import com.dynaTrace.es.pojo.logging.POJOLogger;


/**
 * 
 * Read a properties file to get the required arguments
 * 
 * @author alex
 *
 * @TODO: replace with java.util.Properties when time permits
 */
public class PropertiesFile {

	private static java.util.logging.Logger log = POJOLogger.getLogger(Logger.class);
	/**
	 * @param args
	 * @throws VSMDemoException 
	 */
	public static void main(String[] args) throws PojoException 
	{
		PropertiesFile cfg = new PropertiesFile("D:/test.properties");
		cfg.dump();
		
	}
	
	public void dump()
	{
		System.out.println("#########  Dump ##########");
		for(Iterator<Object> i= props.keySet().iterator();i.hasNext();)
		{
			String key=i.next().toString();
			
			System.out.println(key + " --- "+props.getProperty(key));
		}
		System.out.println("#########  Dump Done ##########");
		
		HashMap<String, String> map = getSet("cc.1");
		System.out.println("cc.1 size is "+map.size());
		System.out.println("Contents; "+map);
	}
	
//	private HashMap<String, String> map = new HashMap<String, String>();
	private Properties props;
	
	public String getProperty(String propName)
	{
		String v = props.getProperty(propName);
		log.info("getProperty(prop="+propName+" returned "+v);
		return v;
	}
	
	public String getProperty(String propName, String defVal)
	{
		String val = "";
		
		if( props.containsKey(propName))
			val = props.getProperty(propName, defVal);
		
		log.info("getProperty(prop="+propName+", def="+defVal+") Returned "+val);
		return val;
	}
	
	public int getInteger(String propName) throws PojoException
	{
		String strVal = props.getProperty(propName);
		int val;
		try
		{
			val = Integer.parseInt(strVal);
		}
		catch(NumberFormatException ne)
		{
			log.info("Failed to parse the Integer value for "+propName+" value was "+strVal);
			throw new PojoException("Failed to parse the Integer value for "+propName+" value was "+strVal);
		}
		log.info("getInt(prop="+propName+") Returned "+val);
		return val;
		
	}
	
	public int getInteger(String propName, int defVal)
	{
		int val = defVal;
		try
		{
			val = getInteger(propName);
		}
		catch(PojoException ve){}
		log.info("getInteger(prop="+propName+", def="+defVal+") Returned "+val);
		return val;
	}
	
	public double getDouble(String propName, double defVal)
	{
		double val = defVal;
		String strVal = props.getProperty(propName);
		try
		{
			val = Double.parseDouble(strVal);
		}
		catch(NumberFormatException ne)
		{
			log.info("Failed to parse the Double value for "+propName+" value was "+strVal);
		}
		log.info("getDouble Returned "+val+" for property "+propName);
		return val;
	}

	public long getLong(String propName, long defVal)
	{
		long val = defVal;
		String strVal = props.getProperty(propName);
		try
		{
			val = Long.parseLong(strVal);
		}
		catch(NumberFormatException ne)
		{
			log.info("Failed to parse the Long value for "+propName+" value was "+strVal);
		}
		log.info("getLong(prop="+propName+", def="+defVal+") Returned "+val);
		return val;
	}
	
	public boolean getBoolean(String propName, boolean defVal)
	{
		String trueBoolValues = "yes true ok on";
		String strVal = props.getProperty(propName);
		if(strVal == null )
			return defVal;
		
		strVal = strVal.toLowerCase();
		log.info("getBoolean(prop="+propName+", def="+defVal+") Returned "+trueBoolValues.contains(strVal.toLowerCase()));
		return trueBoolValues.contains(strVal.toLowerCase());
	}
	
	/**
	 * Get all properties that start with a given string.
	 * @param tag
	 * @return HashMap of 
	 */
	public HashMap<String, String> getSet(String tag)
	{
		HashMap<String, String> tmap = new HashMap<String, String>();
		//Set<Object> keys = ;
		for(Iterator<Object> it = props.keySet().iterator(); it.hasNext();)
		{
			String name = it.next().toString();
			if(name.startsWith(tag))
			{
				//System.out.println((name + " Starts with "+tag));
				String nameEnd = name.substring(tag.length()+1, name.length());
				tmap.put(nameEnd, props.getProperty(name));
			}
			//else
				//System.out.println((name + " Not matched "+tag));
				
		}
		return tmap;
	}
	
	
	public PropertiesFile(String fileName) throws PojoException
	{
		props = new Properties();
		try
		{
			props.load(new FileInputStream(fileName));
		}
		catch(Exception e)
		{
			throw new PojoException(e.getMessage());
		}
	}

}
