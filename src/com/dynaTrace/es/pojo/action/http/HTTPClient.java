package com.dynaTrace.es.pojo.action.http;

import java.util.Scanner;
import java.util.Vector;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.dynaTrace.es.pojo.action.POJOAction;
import com.dynaTrace.es.pojo.util.JSON;



//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.;


/**
 * Action that executes a Http call against a given URL.
 * 
 * Supports:
 * 	method : get/post
 * 	parameters : from the JSON or the URL
 *  requestBody  : for post ?
 * 
 * @author alex.napier
 *
 */
public class HTTPClient extends POJOAction {
    
    public static final String URI 			= "uri";
    public static final String DEF_PAGE 	= "ServletHomePage";
    public static final String METHOD 		= "method";
    public static final String PARAMS 		= "params";    
	public static final String HEADERS 		= "headers";
	public static final String CONTENT_LEN 	= "content-length";
	public static final String RESPONSE 	= "response";
    

    private String uri = "no-host";
    
    // the params to be added to the http request
    private JSON params;
    private HttpMethod method;
    
    private JSON result = new JSON();
    
	public void setup(JSON p)throws Exception{
		super.setup(p);
		
		this.uri = p.getString(URI, System.getProperty(DEF_PAGE, 
					"http://unknown-host:8176/addSystemProperty?called="+DEF_PAGE));

		// get any parameters to add
		this.params = p.getSub(PARAMS);
		
		// get the method
		method = HttpMethod.get(p.getString(METHOD), HttpMethod.get);
				
	}
	
	public Object getParam(String p){
		return params.get(p);
	}
	public JSON getParams(){
		return new JSON(params);
	}
	public HttpMethod getMethod(){
		return this.method;
	}
	public String getURI(){
		return uri;
	}
	
	@Override
	public void runAction() throws Exception {
		// TODO Auto-generated method stub#

		switch(method){
			case get:
				result = doGet();
				break;
			case post:
			case put:
			case delete:
			case head:
			default:
				throw new Exception("Unimplemented Method: "+method);
		
		}
		
	}
	
	
	// add things to this that you want to return to the caller when the job is executed.
	public JSON getResult(){
		return result;
	}
	
	
	private JSON doGet() throws Exception{
		JSON json = new JSON();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(this.uri);
		CloseableHttpResponse response = httpclient.execute(httpget);

		JSON headers = new JSON();
		json.put(HTTPClient.HEADERS, headers);
		for( Header h : response.getAllHeaders()){
			headers.put(h.getName(), h.getValue());
		}
		HttpEntity ent = response.getEntity();

		json.put(HTTPClient.CONTENT_LEN, ent.getContentLength());
		Header h = ent.getContentType();
		if( h != null ){
			headers.put(h.getName(), h.getValue());
		}
		h = ent.getContentEncoding();
		
		if( h != null ){
			headers.put(h.getName(), h.getValue());
		}
		
		// is this a leak and can we detect it?
		Scanner s = new Scanner(ent.getContent()).useDelimiter("\\A");
		json.put(HTTPClient.RESPONSE, s.hasNext() ? s.next() : "");
		
		return json;
	}
	
}