package com.dynaTrace.es.servlets;

import java.util.ArrayList;
import java.util.List;

class Element{
	String type;
	String content;
	List<String> attrList = new ArrayList<String>();
	public Element(String t, String v){
		type = t;
		content = v;
	}
	
	public Element(String v){
		content = v;
	}

	// expects a single attribute ie call ele.attr("style='color:red;size:12px'");
	// note the quotes
	public void attr(String attr){
		attrList.add(attr);
	}

	// expects property, value pairs, ie call ele.attr("style", "color:red;size:12px");
	// note no quotes
	public void attr(String p, String v){
		attrList.add(p+"="+v);
	}

	/* expects a list of formatted property value pairs
	 * list.add("style='color:red'size:12pt');
	 * list.add("id='foo'");
	 * list.add("name='bah'");
	 * ele.attrs(list);
	 */
	public void attrs(List<String> list){
		attrList.addAll(list);
	}
	
	public String toString(){
		StringBuilder out = new StringBuilder();
		if( type!=null ){
			StringBuilder attrs = new StringBuilder();
			for(String s : attrList){
				attrs.append(s+" ");
			}
			out.append(String.format("<%s %s>%s</%s>", type, attrs.toString(), content, type));
		}
		else{
			out.append(content);
		}
		return out.toString();
	}
}