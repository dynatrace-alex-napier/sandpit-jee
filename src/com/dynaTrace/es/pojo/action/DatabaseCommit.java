package com.dynaTrace.es.pojo.action;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.dynaTrace.es.pojo.logging.POJOLogger;
import com.dynaTrace.es.pojo.util.BadDeveloper;
import com.dynaTrace.es.pojo.util.JSON;

// what happens with different modes of commits with JDBC?
// where is the time seen?

public class DatabaseCommit extends DB_Handler
{
	public static String COMMIT_TYPE="type";
	public static String N_ROWS="numrows";
	 
	private static Logger log = POJOLogger.getLogger(DatabaseCommit.class);
	private int sz = 1024;
	private int commitType = 0; 
	private int numRows = 0; 
	
	public void setup(JSON p) throws Exception{
		super.setup(p);
		
		// 0 = auto, 1 on connection, 2 in SQL
		commitType = p.getInt(DatabaseCommit.COMMIT_TYPE, 0);
		numRows = p.getInt(DatabaseCommit.N_ROWS, 0);
	}
	
	public void runAction(){
		if( numRows < 1){
			numRows = 1000;
		}
		String tbl = "temp_"+System.nanoTime();
		try{
			connect();
			createTable(tbl);

			switch(commitType){
			case 0:
				setAutoCommitOn(this.conn);
				doLotsOfDatabaseIO(tbl);
				break;
			case 1:
				setAutoCommitOff(this.conn);
				doLotsOfDatabaseIO(tbl);
				commit(this.conn);
				break;
			case 2:
				setAutoCommitOff(this.conn);
				doLotsOfDatabaseIO(tbl);
				sqlCommit(this.conn);
				break;
			default:
				throw new RuntimeException("Not a known type of commit");
			}
			
		}
		catch(Exception e){
			log.severe("Error in DatabaseCommit.runAction() - "+e.getMessage());
			e.printStackTrace();
		}
		
		finally{
			close(this.conn);
		}
		// clean up in another connection
		try{
			connect();
			switch(commitType){
			case 0:
				setAutoCommitOn(this.conn);
				deleteTableContents(tbl);			
				dropTable(tbl);
				break;
			case 1:
				setAutoCommitOff(this.conn);
				deleteTableContents(tbl);			
				commit(this.conn);
				dropTable(tbl);
				commit(this.conn);
				break;
			case 2:
				setAutoCommitOff(this.conn);
				deleteTableContents(tbl);			
				sqlCommit(this.conn);
				dropTable(tbl);
				sqlCommit(this.conn);
				break;
			default:
				throw new RuntimeException("Not a known type of commit");
			}			
		}
		catch(Exception e){
			
		}
		finally{
			close(this.conn);
		}
	}
	
	private void commit(Connection c) throws Exception{
		c.commit();
	//	BadDeveloper.sleep(250);
	}

	private void sqlCommit(Connection c) throws Exception{
		Statement s = conn.createStatement();
		s.execute("commit;");
		s.close();
	//	BadDeveloper.sleep(250);
	}

	
	private void close(Connection c) {
		try{
			c.close();
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	
	private void doLotsOfDatabaseIO(String tbl) throws Exception{
		for(int i=0; i<numRows; i++){
			insertARecord(tbl);
		}
	}

	private void createTable(String tbl) throws Exception{
		Statement s = conn.createStatement();
		s.execute("CREATE TABLE "+tbl+
				"(A nvarchar(1024) NULL,"+
				"B nvarchar(1024) NULL,"+
				"C nvarchar(1024) NULL,"+
				"D nvarchar(1024) NULL,"+
				"E nvarchar(1024) NULL,"+
				"F nvarchar(1024) NULL)");
		s.close();
	}
	
	
	
	private void insertARecord(String tbl) throws Exception{
		Statement s = conn.createStatement();
		s.execute(String.format("insert into %s (A,B,C,D,E,F) VALUES ('%s','%s','%s','%s','%s','%s')",
				tbl, 
				getString("Aaaa", sz), 
				getString("Bbbb", sz), 
				getString("Cccz", sz), 
				getString("Dded", sz), 
				getString("Eqwerty", sz), 
				getString("Funky", sz)));
		s.close();
		
	}
	
	private String getString(String s, int len){
		StringBuilder b = new StringBuilder();
		b.append(System.nanoTime());
		b.append("_");
		b.append(System.currentTimeMillis());
		while(b.length() < len-s.length()-1){
			b.insert(0, s);
		}
		return b.toString();
	}
	
	private void deleteTableContents(String tbl) throws Exception{
		Statement s = conn.createStatement();
		s.execute("delete from "+tbl);
		s.close();		
	}
	
	private void dropTable(String tbl) throws Exception{
		Statement s = conn.createStatement();
		s.execute("drop table "+tbl);
		s.close();				
	}
	
	private void setAutoCommitOff(Connection c) throws SQLException{
		c.setAutoCommit(false);
		sleep(200); // makes sure it gets seen by auto sensor
		
	}
	
	private void setAutoCommitOn(Connection c) throws SQLException{
		c.setAutoCommit(true);
		sleep(200); // makes sure it gets seen by auto sensor
		
	}
	
	private void sleep(long t){
		BadDeveloper.sleep(t);
	}

	@Override
	protected JSON getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
