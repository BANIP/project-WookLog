package banip.sql.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryProcess {
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	
	public QueryProcess(Connection conn) {
		this.conn = conn;
	}
	
	protected int getAutoIncreaseKey() throws SQLException {
		ResultSet rs = pstmt.getGeneratedKeys();
		if(rs.next()) return rs.getInt(1);
			else return -1;
	}

	public void close() {
		try {
			if(pstmt != null && !pstmt.isClosed()) pstmt.close();
			} catch(SQLException ee){}
		try {
			if(rs  != null && !rs.isClosed()) rs.close(); 
			} catch(SQLException ee){}
	}
}
