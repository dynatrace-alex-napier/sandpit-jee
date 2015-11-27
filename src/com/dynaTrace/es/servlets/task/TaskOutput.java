package com.dynaTrace.es.servlets.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dynaTrace.es.servlets.Format;

public class TaskOutput extends ArrayList<TaskElement> {

	//private List<TaskElement> elements = new ArrayList<TaskElement>();

	public TaskOutput(){
	}
	
	public void add(String s){
		add(s, null);
	}
	
	public void add(List<String> ss){
		add(ss, null);
	}

	public void add(String s, Format fmt){
		this.add(new TaskElement(fmt, s));
	}
	
	public void add(List<String> ss, Format fmt){
		this.add(new TaskElement(fmt, ss));
	}
	
}

class TaskElement{
	public final Format fmt;
	public final String line;
	public final List<String> set;

	public TaskElement(Format fmt, List<String> set){
		this.fmt = fmt;
		this.line = null;
		this.set = set;
	}
	public TaskElement(Format fmt, String line){
		this.fmt = fmt;
		this.line = line;
		this.set = null;
	}
}

