package com.dynaTrace.es.pojo.action;

import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.JSON;


public class Analyser extends POJOAction {

	private static Logger log = POJOLogger.getLogger(Analyser.class);
	private static double MB = 1024*1024;
	
	@Override
	public void runAction() {
		// dump some runtime info.
		Runtime rt = Runtime.getRuntime();
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = rootGroup.getParent()) != null) {
            rootGroup = parent;
        }

		StringBuilder buf = new StringBuilder();
		double max = ((double)rt.maxMemory())/MB;
		double free = ((double)rt.freeMemory())/MB;
		double total = ((double)rt.totalMemory())/MB;
		buf.append("\n----------------  Memory  ----------------------------\n");
		buf.append("Free  Memory: "+free);
		buf.append("\nTotal Memory: "+total);
		buf.append("\nMax   Memory: "+max);
		buf.append("\nPct   Free  : "+(free + max - total)/max * 100);
		if(json.getBoolean("PrintThreads", true)){
			buf.append("\nThreads: -----------------------------------\n");
			buf.append(listThreads(rootGroup, ""));
			buf.append("--------------------------------------------\n");
		}
		buf.append("\n----------------  End ----------------------------\n");			
		log.warning(buf.toString());
	}	
	
	private String listThreads(ThreadGroup group, String indent){
		StringBuilder buf = new StringBuilder();
		int nt = group.activeCount();
		buf.append(indent + "Group[Size: "+nt+"  Name: " + group.getName() + ":" + group.getClass()+"]\n");
        Thread[] threads = new Thread[nt*2 + 10]; //nt is not accurate
        nt = group.enumerate(threads, false);

        // List every thread in the group
        for (int i=0; i<nt; i++) {
            Thread t = threads[i];
            buf.append(indent + "  Thread["+ t.getState()+ " =>> "+ t.getName() + ":" + t.getClass() + "]\n");
        }

        // Recursively list all subgroups
        int ng = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[ng*2 + 10];
        ng = group.enumerate(groups, false);

        for (int i=0; i<ng; i++) {
            buf.append(listThreads(groups[i], indent + "  "));
        }
		 
        return buf.toString();
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
