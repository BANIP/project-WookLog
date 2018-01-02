package banip.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import banip.support.SettingBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class SQLDao {
	Connection conn;
	Statement stat;
	PreparedStatement pstmt;
	ResultSet rs;
	
	
	public SQLDao(){
		String link="", id="", pwd="";
		
		try{
			SettingBean settingBean = SettingBean.getInstance();
			link = settingBean.getDB_PATH() + "/" + settingBean.getDB_DATABASE();
			id = settingBean.getDB_USERNAME();
			pwd = settingBean.getDB_PASSWORD();
		} catch(Exception ee){
			ee.printStackTrace();
			System.out.println(" setting xml을 처리하려던 도중 오류 발생. ");
			System.out.println("link =>" + link );
			System.out.println("id =>" + id );
			System.out.println("pwd =>" + pwd );
		}

		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(link, id , pwd);
			stat = conn.createStatement();
			System.out.println( " complete sql Loading" );
		} catch(ClassNotFoundException ee){
			System.out.println( "class was not founded" );
			System.out.println( ee.getMessage() );
		} catch(SQLException ee){
			System.out.println( ee.getMessage() );
			System.out.println("link =>" + link );
			System.out.println("id =>" + id );
			System.out.println("pwd =>" + pwd );
			ee.printStackTrace();
		}
	} 
	
	public void close(boolean isconnClose){
		try {
			if(pstmt != null && !pstmt.isClosed()) pstmt.close();
			} catch(SQLException ee){}
		try {
			if(rs  != null && !rs.isClosed()) rs.close(); 
			} catch(SQLException ee){}
		if(isconnClose){
			try {
				if(conn != null && !conn.isClosed()) conn.close();
				} catch(SQLException ee){}
			try {
				if(stat  != null && !stat.isClosed()) stat.close();
				} catch(SQLException ee){}
		}
	}
	
	/**
	 * 예외발생시 sysout으로 알려주는 메서드
	 * @param message 메세지
	 * @param ee 예외 클래스
	 */
	protected void printException( Exception ee, String query ){
		final String className = getMethodName(3);
		System.out.println(className + " 예외 발생 => " + ee.getMessage());
		System.out.println(className + " 쿼리 => " + query);
	}
	
	/**
	 * 메소드 스택의 깊이에 따른 메소드의 이름을 획득
	 */
	protected String getMethodName(int depth) {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		return stackTrace[depth].getMethodName();
	}
}
