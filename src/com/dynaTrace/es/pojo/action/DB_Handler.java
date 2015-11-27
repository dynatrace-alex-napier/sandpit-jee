package com.dynaTrace.es.pojo.action;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.JSON;

public abstract class DB_Handler extends POJOAction{
	public static String DEF_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
	public static String DEF_URL = "jdbc:jtds:sqlserver://localhost:1433";
	public static String DEF_USER = "vsm";
	public static String DEF_PSWD = "vsm";
	public static String DEF_TABLE = "pojo_test_records";

	
	public static String CLOSE_HANDLES = "CloseHandles";
	public static String DRIVER = "Driver";
	public static String URL = "URL";
	public static String USER = "User";
	public static String PSWD = "Password";
	public static String NUM_RECS = "NumberInitialRecords";
	public static String ITERATIONS = "Iterations";
	public static String USE_PREP = "UsePreparedStmt";
	public static String TABLE = "Table";
	public static String APPEND = "AppendToTable";
	
	protected static Vector<Connection> connections = new Vector<Connection>();

	protected static Logger log = POJOLogger.getLogger(DB_Handler.class);

	protected String tableName;
	protected Connection conn;
	private String driver;
	private String url;
	private String user;
	private String pswd;

	protected int iterations;
	private boolean closeHandles;
	private boolean appendToTable;
	protected int numRecs;
	
	public String toString(){
		return user + "@"+url+" on table "+tableName+" "+
			(closeHandles?"":" *** IS_Leaky");
	}
	
	public void setup(JSON p) throws Exception{
		super.setup(p);
		driver = p.getString(DRIVER, DEF_DRIVER);
		DriverManager.registerDriver((Driver)Class.forName(driver).newInstance());
		
		url = p.getString(URL, DEF_URL);
		user = p.getString(USER, DEF_USER);
		pswd = p.getString(PSWD, DEF_PSWD);
		tableName = p.getString(TABLE, DEF_TABLE);
		
		// this is the initial table loading
		numRecs = p.getInt(NUM_RECS, 0);
		iterations = p.getInt(ITERATIONS, 1);
		
		closeHandles = p.getBoolean(CLOSE_HANDLES, true);
		appendToTable = p.getBoolean(APPEND, true);

		// make sure the table exists
		createTable();
		
		// this call creates the database and adds some initial records
		if( numRecs > 0 && !appendToTable )
			setupDatabase();
	}
	
	
	protected String getTableName(){
		return tableName;
	}
	
	private void createTable(){
		String sql = "create table "+tableName+" (number int, string1 varchar(256), string2 varchar(256), string3 varchar(256), date DATETIME)";
		
		try{
			execute(sql, true);
			log.warning("Table "+tableName+" created: "+sql);
		}
		catch(Exception e){
			log.warning("Create Table failed : "+e.getMessage());
		}  
		
	}
	
	private void setupDatabase() throws Exception
	{

		if( !appendToTable ){
			try{
				execute("drop table "+getTableName(), true);
				log.warning("Table "+tableName+" dropped");
			}catch(Exception e){
				log.warning("Drop Table failed : "+e.getMessage());			
			}
			
			createTable();  
		}	
		// set the default data load up
		
		connect();
		try{
			Calendar cal = Calendar.getInstance();
			PreparedStatement s = conn.prepareStatement("insert into "+tableName+" (number, string1, string2, string3, date) values(?, ?, ?, ?, ?)");
			for(int i=0; i<numRecs; i++)
			{
				int col =1;
				cal.add(Calendar.MINUTE, -1);
				s.setInt(col++, i);
				s.setString(col++, newStr(256));
				s.setString(col++, newStr(256));
				s.setString(col++, newStr(256));
				s.setDate(col++, new java.sql.Date(cal.getTimeInMillis()));
				
				s.execute();
				log.finest("Loaded record "+i);
			}
			log.info("Inserted "+numRecs+" for testing, number is sequential, date is unique, strings 1-3 are filler");
		}
		catch(Exception e){
			e.printStackTrace();;
		}
		conn.close();
	}
	
	protected String newStr(int len)
	{
		StringBuffer b = new StringBuffer();
		Random r = new Random();
		for(int i=0; i<len; i++){
			b.append(new Character((char)(r.nextInt(78)+48)));
		}
		return b.toString();
	}
	
	protected void connect() throws Exception
	{		
		conn = DriverManager.getConnection(url, user, pswd);
		if( !closeHandles ){
			connections.add(conn);
			log.info("connection is retained "+user+"@"+url);		
		}
		log.info("created database connection"+user+"@"+url);
	}
	
	public int query(String sql) throws Exception
	{
		return query(sql, closeHandles);
	}
	private int query(String sql, boolean closeHndl) throws Exception
	{
	
		connect();
		log.finer("Database Query looping "+iterations+" times");
		Statement s=null;
		int count = 0;
		for(int loopNum=0; loopNum<iterations; loopNum++){
			long start, end;
	
			log.finer("running query "+loopNum);
			s = conn.createStatement();
			start = System.nanoTime();
			s.executeQuery(sql);
			end = System.nanoTime();

			log.finer("Inside query exec time "+(((double)end-start)/1000000000.0)+" secs.");
			ResultSet rs = s.getResultSet();
			while (rs.next()) 
				count++;
		}
		
		// beware - this causes resource leaks
		if( closeHndl ){
			s.close();
			conn.close();
		}
		return count;
	}

	
	public int execute(String sql) throws Exception
	{
		return execute(sql, closeHandles);
	}
	
	private int execute(String sql, boolean closeHndl) throws Exception
	{
		log.info("Executing SQL: "+sql);
		int res;
		connect();

		Statement s = conn.createStatement();
		res = s.executeUpdate(sql);
		if( closeHndl )
			s.close();
			
		// beware - this causes resource leaks
		if( closeHndl ){
			conn.close();
		}
		return res;
	}

	

	
	@Override
	public abstract void runAction();
}
