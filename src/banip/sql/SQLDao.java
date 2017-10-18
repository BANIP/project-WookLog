package banip.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class SQLDao {
	Connection conn;
	Statement stat;
	PreparedStatement pstmt;
	ResultSet rs;
	
	protected static String id = "BOARD_USER";
	protected static String pwd  = "qA2ayEzoMIcoGaGEQAD6gidIG8w3Ri";
	protected static String link = "jdbc:mysql://localhost:3306/education";
	
	public SQLDao(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(link, id, pwd);
			stat = conn.createStatement();
			System.out.println( " complete sql Loading" );
		} catch(ClassNotFoundException ee){
			System.out.println( "class was not founded" );
			System.out.println( ee.getMessage() );
		} catch(SQLException ee){
			System.out.println( ee.getMessage() );
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
