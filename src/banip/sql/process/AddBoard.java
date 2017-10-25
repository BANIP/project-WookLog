package banip.sql.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import banip.bean.BoardBean;
import banip.query.BoardQuery;

public class AddBoard extends QueryProcess {

	ArrayList<String> querys;
	BoardBean bean;
	int userID;
	
	public AddBoard(Connection conn, BoardBean bean, int userID) {
		super(conn);
		BoardQuery boardquery = (BoardQuery) bean.getQuery();
		 querys = boardquery.getAddBoardQuery(userID);
		// TODO Auto-generated constructor stub
	}

	public int executeAddHistory() throws SQLException {
		
		pstmt = conn.prepareStatement( querys.get(0), PreparedStatement.RETURN_GENERATED_KEYS);
		pstmt.executeUpdate();
		int historyID = getAutoIncreaseKey();
		
		close();
		return historyID;
	}


	

	public int executeAddBoard(int historyID) throws SQLException {
		pstmt = conn.prepareStatement( querys.get(1), Statement.RETURN_GENERATED_KEYS);	
		pstmt.setInt(1, historyID);
		pstmt.executeUpdate();
		int boardID = getAutoIncreaseKey();
		
		close();
		
		return boardID;
	}

	public void executeUpdateHistory(int boardID, int historyID	) throws SQLException {
		System.out.println(querys.get(2));
		pstmt = conn.prepareStatement( querys.get(2), Statement.RETURN_GENERATED_KEYS);	
		pstmt.setInt(1, boardID);
		pstmt.setInt(2, historyID);
		pstmt.executeUpdate();
		close();
	}
}
