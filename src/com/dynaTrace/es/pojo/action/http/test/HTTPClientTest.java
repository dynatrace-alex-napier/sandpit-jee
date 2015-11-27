package com.dynaTrace.es.pojo.action.http.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dynaTrace.es.pojo.action.http.HTTPClient;
import com.dynaTrace.es.pojo.action.http.HttpMethod;
import com.dynaTrace.es.pojo.util.JSON;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class HTTPClientTest {

	JSON params = new JSON();
	JSON queryStr = null;
	String queryJSON = "{\"a\":\"an a\", \"b\":123}";
	String TEST_URI = "http://some-test-uri";
	HTTPClient hc = new HTTPClient();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	@Before
	public void setUp() throws Exception {
		try {
			queryStr = new JSON(queryJSON);
		} catch (Exception e) {
			fail("Parsing JSON "+queryJSON+" for queryStr: "+e);
			e.printStackTrace();
			return;
		}
		
		params.put(HTTPClient.URI, TEST_URI);
		params.put(HTTPClient.PARAMS, queryStr);
		params.put(HTTPClient.METHOD, HttpMethod.get);
		
		try{
			hc.setup(params);
		}
		catch(Exception e){
			fail("Failed with Exception calling setup: "+e.getMessage());
			e.printStackTrace();
			return;
		}		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetup() {
		// test the correct properties are read and set during setup
		assertEquals(hc.getURI(), TEST_URI);
		assertEquals(hc.getParams().get("a"), "an a");
		assertEquals(hc.getParams().get("b"), 123);
		assertEquals(hc.getParam("a"), "an a");
		assertEquals(hc.getParam("b"), 123);
		assertEquals(hc.getMethod(), HttpMethod.get);
		//fail("Not yet implemented");
	}
	
	@Test
	public void testGet(){
	//	fail("not implemented");
		params.put(HTTPClient.URI, "http://www.google.com");
		JSON res = null;
		try{
			hc.setup(params);
			hc.runAction();
		}
		catch(Exception e){
			fail("Exception during test: "+e);
			return;
		}
		res = hc.getResult();
		assertNotNull(res);
		JSON headers = res.getSub(HTTPClient.HEADERS);
		assertNotNull(headers);
		assertTrue(headers.size() > 0);
		for(String h : headers.keySet()){
			assertNotNull(h);
			assertNotNull(headers.get(h));
			System.out.println("Header :"+h+ " is "+ headers.get(h));
		}
		String response = res.getString(HTTPClient.RESPONSE);
		assertNotNull(response);
		assertTrue(response.length() > 100);
	}

	@Test
	public void testPost(){
		fail("not implemented");
	}
	
	@Test
	public void testPut(){
		fail("not implemented");
	}
	
	@Test
	public void testDelete(){
		fail("not implemented");
	}
	
	@Test
	public void testHead(){
		fail("not implemented");
	}
}
