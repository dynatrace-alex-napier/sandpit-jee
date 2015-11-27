{
	/*
		@todo : 
			1. make these defaults class based so we only add the items the class expects.  Whats the point in the logger have DB credentials...
				we will also maintain a based level defaults though for POJOAction level properties
			2. add option to run this n times between min and max execution intervals (0, 0 is no delay) , pojo action supports this already just needs the thread executor added, 
				an option to specify sync vs async
	*/
	"DEFAULTS" : {
		"LogLevel":"WARNING"	// uses java.util.logging for values, FINEST, FINER, FINE, INFO, WARNING, SEVERE, default in INFO
			
		// where the job uses resources handles they will only be freed if this is set to true 
		// i.e. file handles, database connections, TCP sockets etc?
		,"CloseHandles":false
		
		// should the job start and return or wait for all threads to complete
		,"async" : true
		
		// should the thread be set as daemon, when true PP across thread boundaries are not followed
		,"daemon" : false

		// if the job should keep its objects open for a period, in seconds
		// GC will only happen after this period expires so set it big and watch memory grow
		,"TTL":300				
			
		// default database connection details, can be overridden in the action if required
		,"Driver":"net.sourceforge.jtds.jdbc.Driver",
		"URL":"jdbc:jtds:sqlserver://localhost:1433",
		"User":"vsm",
		"Password":"vsm"
		
		// details of the job queue
		// doesn't work correctly, the queue only ever goes to CoreSize not MaxSize
		,"Queues":{
			"CoreSize":10	// number of core threads
			,"MaxSize":25	// max number of pool threads
			,"TTL":0			// time to live for the queue threads (seconds)
		}
	}
	// work in progress ... add the rest here and update the TaskManager to use them
	,"CLASS_DEFS" : {
		"com.dynaTrace.es.pojo.action.Exceptions2" : {
			"ExceptionA":true
			,"ExceptionB":true
			,"ExceptionC":true
			,"ExceptionNPE":true		
		},
		"com.dynaTrace.es.pojo.action.AsyncRunner" : {
			"LogLevel":"INFO",
			"Duration" : 20000,
			"Depth" : 15,
			"ResponseTime" : 1250
		}
	}
	// each job represents some kind of work item that uses various resources
	// read the source code bundled with it to see exactly what is going on
	,"DBCommit":{
		"description" : [
			"Use different commit methods to test SSP theory about commits appearing as readSocket time",
			"SSP maintains InsureJ/IQH insurance agrregation platform from L&G"
		],
		"className":"com.dynaTrace.es.pojo.action.DatabaseCommit",		
		"Driver":"net.sourceforge.jtds.jdbc.Driver",
		"URL":"jdbc:jtds:sqlserver://localhost:1433",  
		"User":"bsm",
		"Password":"bsm" 
	},
	"jms":{
		"description" : [
			"do JMSmy stuff"
		],
		"className":"com.dynaTrace.es.pojo.action.JMSExample"
		
	},
	"throw":{
		"description" : [
			"Proves exceptions are shown in the PP even when not thrown",
			"Can we work out when they are really thrown?"
		],
		"className":"com.dynaTrace.es.pojo.action.ThrowException",
		"exceptionClass":"java.lang.Runtime",
		"message":"1. this is my exception"		
		
	},
	"http-1" : {
		"description" : [
			"does http calls, lots of them ..."
		],

		"className":"com.dynaTrace.es.pojo.action.http.HTTPClient",
		"LogLevel":"INFO",
		"uri" : "http://localhost/Hello-Dynatrace/s1.html?action=run&job=Sleeper",
		"jobs" : {
			"a":{
				"className":"com.dynaTrace.es.pojo.action.http.HTTPClient",
				"uri" : "http://localhost/Hello-Dynatrace/s1.html?action=run&job=Sleeper&Cycle=6000"
			},
			"b":{
				"className":"com.dynaTrace.es.pojo.action.http.HTTPClient",
				"uri" : "http://localhost/Hello-Dynatrace/s1.html?action=run&job=Sleeper&Cycle=3000"
			},
			"c":{
				"className":"com.dynaTrace.es.pojo.action.http.HTTPClient",
				"uri" : "http://localhost/Hello-Dynatrace/s1.html?action=run&job=Sleeper&Cycle=1500"
			}
		}
	},		
	"http-2" : {
		"description" : "tbc",
		"className":"com.dynaTrace.es.pojo.action.http.HTTPClient",
		"uri" : "http://localhost/Hello-Dynatrace/s1.html?action=run&job=Sleeper",
		"jobs" : {
			"a":{
				"className":"com.dynaTrace.es.pojo.action.Sleeper",
				"cycle" : 500,
				"uri" : "http://localhost/Hello-Dynatrace/s1.html?action=run&job=Sleeper&Cycle=6000"
			},
			"b":{
				"className":"com.dynaTrace.es.pojo.action.http.HTTPClient",
				"uri" : "http://localhost/Hello-Dynatrace/s1.html?action=run&job=Sleeper&Cycle=3000"
			},
			"c":{
				"className":"com.dynaTrace.es.pojo.action.http.HTTPClient",
				"uri" : "http://localhost/Hello-Dynatrace/s1.html?action=run&job=Sleeper&Cycle=1500"
			}
		}
	},	
	"BTFilterTest" : {
		"className" : "com.dynaTrace.es.pojo.action.transactions.FilterTest",
		"methodA" : "aaa",
		"methodB" : "bbbBBB",
		"methodC" : "CCCCC",
		"methodD" : "D"
	},
	"Async" : {
		"description" : [
			"Start a PP that spins a thread off and returns, causes long running PP duration"
		],

		"className":"com.dynaTrace.es.pojo.action.AsyncRunner",
		"LogLevel":"INFO",
		"Duration" : 5000,
		"Depth" : 2,
		"ResponseTime" : 250
	},		
	"WriteFile" : {
		"description" : [
			"builds a string one char at a time, writes it to a temp file,",
			"then deletes after ttl expires                                                                         ",
			"If the string is large                                                                                 ",
			"- it will exceed autosensors                                                                           ",
			"- it will take time to write                                                                           ",
			"- the file handle will be held until ttl expires                                                       ",
			"- the file will be cleaned off the system at the same time or on shutdown, if the app crashes the      "
		],
		"className":"com.dynaTrace.es.pojo.action.WriteFile",
		"StringLength":10300,
		"TTL":300
	},
	"Sleeper":{
		"description" : [
		  "Sleeps the thread in groups of cycle (msecs) length",
		  "If IsBroken (ie broken sleep) then it sleeps total of cycle in micro-cycle chunks"
		 ],
		"className":"com.dynaTrace.es.pojo.action.Sleeper",
		"Cycle":2000,		// total duration
		"MicroCycle":5,	// length of little suspensions if IsBroken is true
		"IsBroken":true
	},	
	"BTExample" : {
		"description" : [
		  "Creates some values we can look at with BTs, see if you can work it out from there "
		 ],
		"className":"com.dynaTrace.es.pojo.action.BTExample"
	},			
	"Leak" : {
		"description" : [
			"Allocates a chunk of data using a StringBuilder object",
			"Maintains a reference to it for ttl "
		],
		"async" : false,
		"className":"com.dynaTrace.es.pojo.action.LeakMemory",
		"NumObjects":5000,
		"ObjectSize":10,
		"TTL":600,			
		"LogLevel":"WARNING"
	},
	"Logger" : {
		"description" : [
			"Writes log messages using the java util logger",
			"Writes one SEVERE message every 60 seconds starting immediately"
		],

		"className":"com.dynaTrace.es.pojo.action.JavaUtilLogger",
		"LogLevel":"INFO"
	},
	"InfoLogger" : {
		"description" : [
			"Writes log messages using the java util logger",
			"Writes INFO. WARNING and SEVERE message every 10 seconds starting after 60 seconds"
		],
		"className":"com.dynaTrace.es.pojo.action.JavaUtilLogger",
		"LogLevel":"INFO"
	},	
	// @todo : add message option
	"WarnLogger" : {
		"description" : [
			"Run a logger warning message"
		],
		"className":"com.dynaTrace.es.pojo.action.JavaUtilLogger",
		"LogLevel":"DEBUG"
	},	
	"AutoSensorTest" : {
		"description" : [	
			"Requires sensor configuration to show this off",
			"add sensors to methods called method1, method2, method3 and method4 and they are not captured correctly",
			"add sensors for methodOne, methodTwo, methodThree and methodFour and it shows the correct stack",
			"correct stack is shown below, spinCPUs is called using the values in the JSON below, 4 is num threads",
			"methodOne",
			"     method1",
			"            spinCPUs(One, 4)",
			"        methodTwo",
			"            methodTwoSub",
			"                spinCPUs(Two, 4)",
			"            methodThree",
			"                methodThreeSub",
			"                    spinCPUs(Three, 4)",
			"                methodFour",
			"                    methodFourSub",
			"                        spinCPUs(Four, 4)",
			"",
			"See System Profile, Agent Mapping, Advanced, Auto Sensors for config rules on auto sensor",
			"resolution                                                                                             "
		 ],
		 
		"className":"com.dynaTrace.es.pojo.action.AutoSensorTest",
		"Active":false,
		"One":5,
		"Two":20,
		"Three":50,
		"Four":100
	},	
	"Mem2" : {
		"description" : [
			"Allocates a chain of objects that reference each other",
			"Seems to fool the GC into never releasing the memory "
		],
		"className":"com.dynaTrace.es.pojo.action.CircularMemory",
		"ObjectSize":0.001,  //  size in MB
		"NumberOfObjects":100000,
		// TTL is time the reference is kept for, after this its released and should be GC'd
		// set to 0 to keep forever
		"TTL":1200			
	},
	"Spin" : {
		"description" : [
			"spins the CPU for the time specified, allocate threads (NumThreads) to max at 100%"
		],
		"className":"com.dynaTrace.es.pojo.action.SpinCPU",
		"Duration":10000,
		"NumThreads":2
	},
	"ReadFile" : {
		"description" : [
			"Reads a file as many times as you want",
			"Generates the file with random text to the size of FileSize KB",
			"Reads the file Iterations times, may also leave handles open for upto TTL seconds in memory",
			"When the memory is released the handle is closed                                                          "
		],
		"className":"com.dynaTrace.es.pojo.action.ReadFile",
		"TTL" : 300,
		"Iterations":10,		
		"FileSize":512
	},
	"DeadLock" : {
		"description" : [
			"every time a deadlock is used it blocks one of the pool threads so reloading this file",
			"multiple times with this active will take all the pool threads and no other jos will run",
			"effectively hanging the server, Does this work in a tomcat context?"
		],
		"className":"com.dynaTrace.es.pojo.action.Deadlock",
		"Iterations":3 // only do this so many times before stopping or it will hang the server			
	},
	"Analyser" : {
		"description" : [
		  "Print details of the system, memory threads etc so we can validate against DT monitoring",
		  "Check the application log file for this output"
		],
		"className":"com.dynaTrace.es.pojo.action.Analyser",
		"PrintThreads":true
	},
	/*
	@todo: to include JPA/hibernate.
	*/
	"DBInsert" : {
		"description" : [
		  "Inserts records into a database"
		],
		"className":"com.dynaTrace.es.pojo.action.DB_Insert",			
		"AppendToTable":true, // if set to false the table will be dropped and recreated
		"Iterations":10,
		"NumberInitialRecords":100
	},
	"DBInsert2" : {
		"description" : [
		],
		"className":"com.dynaTrace.es.pojo.action.DB_Insert",
		"AppendToTable":false,
		"Iterations":10,
		"NumberInitialRecords":123,	// insert these on start up
		"Table":"pojoTestingXYZ",

		// database connectoin details, some defaults will be used since they are not here
		"URL":"jdbc:jtds:sqlserver://localhost:1433/fourthree"

	},
	"DBQuery" : {
		"description" : [
			"Guess?"
		],
		"className":"com.dynaTrace.es.pojo.action.DB_Query",
		"Iterations":10,
		"Table":"pojoTesting",
		// query arguments
		"Properties":"*",
		"Where":"where string1 = 'does not match a lot...'",
		
		// database connection details
		"URL":"jdbc:jtds:sqlserver://localhost:1433/fourthree"
		
	}

}