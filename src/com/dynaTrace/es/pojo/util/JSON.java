package com.dynaTrace.es.pojo.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.dynaTrace.es.pojo.POJOServer;
import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSON extends HashMap<String, Object>{

	private static java.util.logging.Logger log = POJOLogger.getLogger(JSON.class);
	private static final long serialVersionUID = -6921874185788427637L;

	public JSON(){}
	
	public JSON(String jsonStr)throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);		
		Map<String, Object> map = mapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
		this.putAll(map);		
	}
	

	public JSON(File file) throws JsonParseException, JsonMappingException, IOException{

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);		
		Map<String, Object> map = mapper.readValue(file, new TypeReference<Map<String, Object>>() {});
		this.putAll(map);
	}
	
	public JSON(Map<String, Object> json){
		this.putAll(json);
	}
	
	// create a new JSON object from this & that, that is another JSON object (to be sure, to be sure)
	public JSON mixin(Map<String, Object> that){
		JSON newJson = new JSON(this);
		newJson.putAll(that);
		return newJson;
	}
	
	// defaults to false
	public boolean getBoolean(String tag){
		return getBoolean(tag, false);
	}
	
	
	public boolean getBoolean(String tag, boolean def){
		try{
			return ((Boolean)this.get(tag)).booleanValue();
		}
		catch(Exception e){
			return def;
		}
	}	
	
	public double getDouble(String tag){
		return getDouble(tag, 0);
	}

	public double getDouble(String tag, double def){
		Object ob = this.get(tag);
		try{
			return ((Double)ob).doubleValue();
		}
		catch(Exception e){
			try{
				return Double.parseDouble(ob.toString());
			}
			catch(Exception e2){}
		}
		return def;
	}

	public int getInt(String tag){
		return getInt(tag, 0);
	}

	public int getInt(String tag, int def){
		Object ob = this.get(tag);
		try{
			return ((Integer)ob).intValue();
		}
		catch(Exception e){
			try{
				return Integer.parseInt(ob.toString(), 10);
			}
			catch(Exception e2){}
		}
		return def;
	}

	public String getString(String tag){
		return getString(tag, "");
	}

	public String getString(String tag, String def){
		Object ob = this.get(tag);
		if( ob != null ){
			return ob.toString();
		}
		return def;
	}
	
	public Level getLogLevel(){
		try{
			return Level.parse(this.getString("LogLevel"));
		}
		catch(Exception e){}
		return POJOServer.getLogLevel();
	}
	
	
	@SuppressWarnings("unchecked")
	public JSON getSub(String tag){
		Object o = get(tag);
		if( o != null ){
			if(o instanceof Map){
				return new JSON((Map<String, Object>)o);
			}
		}
		return new JSON();
	}
}
