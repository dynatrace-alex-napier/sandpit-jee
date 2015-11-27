package com.dynaTrace.es.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

class HTML {
	private StringBuilder errors = new StringBuilder();
	private StringBuilder body = new StringBuilder();
	private StringBuilder head = new StringBuilder();

	public static String CSS_TAG = "<link rel='stylesheet' type='text/css' href='%s'/>";
	public static String JS_TAG = "<script type='text/javascript' src='%s'></script>";
	public HTML(){
		
	}
	
	public void append(String content){
		body.append(content);
	}

	public void append(String content, String tag){
		body.append(String.format("<%s>%s</%s>", tag, content, tag));
	}
	
	public void appendHead(String content){
		head.append(content);
	}
	
	public void addCSS(String file){
		head.append(String.format(CSS_TAG, file));
	}
	public void addJS(String file){
		head.append(String.format(JS_TAG, file));
	}
	
	public void error(String msg){
		error(msg, null);
	}
	
	public void error(String msg, Exception e){
        errors.append(String.format("<error><msg>Error:%s</msg>", msg));
        if( e != null ){
        	StringWriter stack = new StringWriter();
        	e.printStackTrace(new PrintWriter(stack));
        	errors.append(String.format("<errTxt>Exception:%s<errTxt/><br/><stackTrace>%s</stackTrace>", e.getMessage(), stack.toString()));
        }
        errors.append("</error>");
	}
	
	public void addTable(List<List<String>> rows){
		append("<table>");
		for(List<String> row : rows){
			append("<tr>");
			for(String cell : row){
				append(cell, "td");
			}
			body.append("</tr>");
		}
		append("</table>");
	}
	
	public void finish(HttpServletResponse res) throws IOException{
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();		
        out.println(String.format("<html><head>%s</head><body>%s</body></html>", head, body));
	} 
}