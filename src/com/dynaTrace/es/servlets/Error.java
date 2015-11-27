package com.dynaTrace.es.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*
 * Servlet calls PojoServer features based on a list of jobs in request.
 * All known job names will be executed
 * Unknown jobs will result in 500 errors
 * 
 * 2. jsonjobs will be parsed and all jobs there will also be executed, these are not stored but created on the fly.
 * Errors  
 * @author cwuk-anapier
 *
 */

/*
 * Servlet that throws errors
 */
@WebServlet(value="/s2.html", loadOnStartup=1)
public class Error extends HttpServlet {

	public static final String TYPE = "type";
	

	public void doGet(HttpServletRequest  req, HttpServletResponse res)
			throws ServletException, IOException{
		
		String type = req.getParameter(Error.TYPE);
		switch(type){
			case "a":
				throw new ServletException("Case A");
			case "b":
				throw new IOException("Case B");
		}
		
		throw new RuntimeException("Unknown type: "+type);
	}
}

