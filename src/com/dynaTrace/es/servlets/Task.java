package com.dynaTrace.es.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
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


import com.dynaTrace.es.pojo.action.POJOAction;
import com.dynaTrace.es.pojo.util.JSON;
import com.dynaTrace.es.servlets.Test.Tasks;
import com.dynaTrace.es.servlets.task.TaskManager;

/*
 * Servlet designed to talk to the web page via JSON
 * HTML responses should be kept to a minimum
 * Let angular do all the html in the front end
 */
@WebServlet(value="/task", loadOnStartup=1)
public class Task extends HttpServlet {

	public enum Action{
		browse, view, run, add, delete, modify, help;
	
		private static final Vector<String> values = new Vector<String>();
		static{
			for(Action t: Action.values()){
				values.add(t.toString());	
			}			
		}
		
		public static boolean contains(String s){
			return values.contains(s);
		}
		
		public static Action get(String s){
			for(Action t: Action.values()){
				if( t.toString().equals(s) )
					return t;	
			}
			return help;
		}
	}

	private static Logger log = Logger.getLogger(Task.class.getName());

	public static final String DEF_PAGE = "ServletHomePage";
	public static final String JOB_NAME = "job";
	public static final String CLS_NAME = "className";
	public static final String THROW_ERR = "throwError";
	
	// page action variables
	public static final String ACTION = "action";
	

	@Override
	public void init(ServletConfig config){
		log.info("Init called");
		
//		ClassLoader l = ClassLoader.getSystemClassLoader();

	}

	/**
	 * Servlet actions:
	 * 1. Browse known tasks
	 * 2. View a task
	 * 3. Run a task
	 * 4. Add a task
	 * 5. Delete a task
	 * 6. Modify a task
	 */
	public void doGet(HttpServletRequest  req, HttpServletResponse res)
			throws ServletException, IOException{
		
		if( req.getParameter(Task.THROW_ERR) != null){
			int userError = 500;
			try{
				userError = Integer.parseInt(req.getParameter(Task.THROW_ERR));
			}
			catch(Exception e){
				log.severe("Can't parse user error code: "+req.getParameter(Task.THROW_ERR)+" for "+Task.THROW_ERR);
			}
			res.sendError(userError, "User Generated");
			return;
		}

		JSON output;
		Action action = Action.get(req.getParameter(Task.ACTION)); 
		String title = "Action is "+action;
		switch(action){
			case run:
				output = run(req);
				break;
			case browse:
				output = browse(req);
				break;
			case view:
				output = view(req);
				break; 
			case add:
				output = add(req);
				break; 
			case delete:
				output = delete(req);
				break; 
			case modify:
				output = modify(req);
				break;
			case help:
				output = help(req);
				break;
			default:
				// dead code
				output = error(String.format("%s is not a recognised action, choose one of %s", action, Tasks.values()));

		}
		
		finishHTML(res, output, title);
	}
	// add a new action
	private JSON add(HttpServletRequest req){
		return error("add not implemented yet");
	}
	
	// list all defined actions
	private JSON browse(HttpServletRequest req){
		JSON json = new JSON();
		json.put("timestamp" , new Date());
		return TaskManager.getInstance().getJobs();
	}

	// delete an action from the list of specified actions
	private JSON delete(HttpServletRequest req){
		return error("delete not implemented yet");
		
	}

	// gets the help message
	private JSON help(HttpServletRequest req){
		JSON help = new JSON();
		ArrayList<String> arr = new ArrayList<String>();
		help.put("help", arr);
		arr.add("Usage: call this page with one of the following parameters");
		arr.add("  action="+Action.add+" - adds a new task to the configuration");
		arr.add("    must have JSON configuration specific to the class being instantiated");
		arr.add("  action="+Action.browse+" - browse a list of all tasks");
		arr.add("  action="+Action.delete+" - delete a task (no undo)");
		arr.add("  action="+Action.help+" - display this message");
		arr.add("  action="+Action.modify+" - modify an existsing task ");
		arr.add("    must have JSON configuration specific to the class being instantiated");
		arr.add("  action="+Action.run+" - execute an existing task");
		arr.add("  action="+Action.view+" - view an existing task");
		return help;
		
	}

	// modify an action
	private JSON modify(HttpServletRequest req){
		return error("modify not implemented yet");
		
	}

	// run the action
	private JSON run(HttpServletRequest req){
//		html.error("run not implemented yet", null);
		TaskManager mgr = TaskManager.getInstance();
		String jobName = req.getParameter(Task.JOB_NAME);
		log.info("Trying to run job :"+jobName);
		return mgr.run(jobName, req);
	}
	
	// view a specific action
	private JSON view(HttpServletRequest req){
		TaskManager mgr = TaskManager.getInstance();
		return mgr.getJob(req.getParameter(Task.JOB_NAME));	
	}
	
	private JSON error(String s){
		JSON j = new JSON();
		j.put("error", s);
		return j;
	}

	private void finishJSON(HttpServletResponse res, JSON json) throws IOException{
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();		
        out.println(json.toString());
	} 

	private void finishHTML(HttpServletResponse res, JSON json, String title) throws IOException{
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        StringBuilder b = new StringBuilder();
        String HTML = "<!DOCTYPE html><head>%s</head><html><body>%s</body></html>";
        b.append(String.format(HTML, getHTMLHead(title), getHTMLBody(json)));
        out.println(b.toString());
	} 
	
	private String getHTMLBody(JSON j){
		String html = "<pre>%s</pre>";
		
		return String.format(html, j.toString().replaceAll(",", "<br/>"));
	}
	private String getHTMLHead(String title){
        StringBuilder b = new StringBuilder();
        String JS = "<script type=\"text/javascript\" async=\"\" src=\"%s\"></script>";
        String CSS = "<link crossorigin=\"anonymous\" href=\"%s\" media=\"all\" rel=\"stylesheet\">";

        b.append(String.format("<title>%s</title>", title));
        b.append(String.format(CSS, "/styles/my.css"));
        b.append(String.format(JS, "/scripts/my.js"));
        
        return b.toString();
	}	
}

