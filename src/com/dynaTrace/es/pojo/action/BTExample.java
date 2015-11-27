package com.dynaTrace.es.pojo.action;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

import javax.script.ScriptException;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.JSON;


public class BTExample extends POJOAction {

	private List<String> myNames = new Vector<String>();
	private List<FooBean> myActions = new Vector<FooBean>();
	private List<String> myObjects = new Vector<String>();

	private static String PERSON = "Bob,Jane,Peter,Paul,Mary,Lucas,Fred,Ralph";
	private static String ACTION = "playing,beating,reading,eating,patting";
	private static String OBJECT = "a guitar,a drum,a kindle,a large apple,the donkey,";
	
	private static int MAX_CORES = Runtime.getRuntime().availableProcessors()+1;
	
	private Random r = new Random();
		
	public void setup(JSON p)throws Exception{
		super.setup(p);
		
		String strNames[] = p.getString("Names", PERSON).split(",");
		String strActions[] = p.getString("Actions", OBJECT).split(",");
		String strObjects[] = p.getString("Adjectives", ACTION).split(",");
		
		myNames.addAll(Arrays.asList(strNames));
		//myActions.addAll(Arrays.asList(strActions));
		myObjects.addAll(Arrays.asList(strObjects));
		for(String s:strActions){
			myActions.add(new FooBean(s));
		}
	}
	@Override
	public void runAction() {
		int num=0;
//		for(String n : myNames){
//			FooBean action = getMyAction(n);
//			String adj = getMyObject(n, action); 
//			ActionBean bean = new ActionBean(getMyName(n), action, adj);
//			IActionBean b2 = new ActionBeanTwo(n+"--"+adj);
//			doBeanStuff(b2, n, adj, num);
//			log.info(getSentence(bean, bean.getName(), bean.getAction()) +" xxx in "+bean.getExecTime()+"msecs.");
//			num++;
//		}
		log.info("processed "+num+" transactions");
		
		// step two, do some method calls that pass params.
		// use this to build BTs that split on method names
		splitOnStallMethodToSeeParams();
	}
	
	private void splitOnStallMethodToSeeParams(){
		
		HttpUtils hu = new HttpUtils();
		hu.doGet(123, 	"http://www.abcd.com/page-123.html");
		hu.doGet(53, 	"http://www.abcd.com/page-53.html");
		hu.doGet(35, 	"http://www.abcd.com/page-35.html");
		hu.doGet(35, 	"http://www.abcd.com/page-35.html");
		hu.doGet(549, 	"http://www.abcd.com/page-549.html");
		hu.doGet(35, 	"http://www.abcd.com/page-35.html");
		hu.doGet(35, 	"http://www.abcd.com/page-35.html");
		hu.doGet(35, 	"http://www.abcd.com/page-35.html");
		
		//throw new RuntimeException("hello RT exception");
	}

	// next 3 methods are so we can access the values from a BT
	private String getMyName(String name){
		return name;
	}
	
	private void doBeanStuff(IActionBean iab, String name, String adj, int n){
		log.info("Doing Bad Bean Like Stuff: "+iab.getName()+"//"+name+"//"+adj);
		BadDeveloper.spinCPUs(25*n, 2);
		
		
	}
	
	private FooBean getMyAction(String name){
		FooBean fb = myActions.get(r.nextInt(myActions.size())); 	
		return fb;
	}
	
	private String getMyObject(String name, FooBean action){
		return myObjects.get(r.nextInt(myObjects.size()));
	}
	
	protected String getSentence(ActionBean bean, String name, FooBean action){
		sleeping(1234);
		return getPrivSentence(name, action, bean.getObject());
	}

	private String getPrivSentence(String name, FooBean action, String object){
		// delay will be based on order of the name, action and adj
		int i = myNames.indexOf(name);
		int j = myActions.indexOf(action);
		int k = myObjects.indexOf(object);
		if(i*j*k < 0){
			i=50;
			j=5;
			k=1;
			log.severe("Bad Params in getPrivSentence: "+name+"  :"+action+" :"+object);
		}
		
		BadDeveloper.spinCPUs(r.nextInt(i * j * k+100)+50, Math.min(MAX_CORES, r.nextInt(MAX_CORES) * r.nextInt(MAX_CORES)));
		return name +" is " +action.str+" "+object;
	}
	

	private int sleeping(int i){
		BadDeveloper.sleep(r.nextInt(50)+100);
		return i;
	}
	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}

class HttpUtils{
	private static final Logger log = POJOLogger.getLogger(HttpUtils.class); 
	public HttpUtils(){

	}
	
	public String doGet(int t, String name){
		log.warning("HttpUtils.doGet is stalling for "+t+" msecs.");		
		BadDeveloper.sleep(t);
		return "<html>hello, world</html>";
	}
	
}

// these are used for testing accessor methods.
// note: we can call an accessor on a class that is passed when the interface is all that is declared
// not sure what happens if that class is not correct, ie some other implementor is passed

interface IActionBean{
	public String getName();
}
class ActionBeanTwo implements IActionBean{
	
	String name;
	ActionBeanTwo(String nm){
		name = nm;
	}
	
	public String getName(){
		return name;
	}
	
	public String getActionBeanName(){
		return  "@@"+name;
	}
}

class ActionBean implements IActionBean{
	String name;
	FooBean action;
	String object;
	long startTime;
	private static final Logger log = POJOLogger.getLogger(ActionBean.class); 
	ActionBean(String name, FooBean action, String adj){
		this.name = name;
		this.action = action;
		this.object = adj;
		startTime = System.currentTimeMillis();
	}
	
	public String getName(){
		return name;
	}
	
	FooBean getAction(){
		return action;
	}
	
	String getObject(){
		return object;
	}
	

	long getExecTime(){
		return System.currentTimeMillis()-startTime;
	}
	
	void setName(String s){
		name = s;
	}
	
	void setAction(FooBean s){
		action = s;
	}
	
	void setObject(String s){
		object = s;
	}
	
	public String toString(){
		log.warning("toString is being called on ActionBean");
		return name + "/" + action.str +"/" + object;
	}
}

class FooBean implements IActionBean{
	String str = "nuLL";	
	private static final Logger log = POJOLogger.getLogger(FooBean.class); 
	FooBean(String s){
		str = s;
	}
	
	String getS(){
		return str;
	}
	
	public String getName(){
		return getS();
	}
	
	public String toString(){
		log.warning("##################################  FooBean toString is being called");
		return "FooBean::"+str;
	}
}