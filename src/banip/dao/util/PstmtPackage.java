package banip.dao.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import banip.bean.SQLBean;

public class PstmtPackage {

	PreparedStatement _pstmt;
	String _templet;
	ArrayList<Column> _columnList = new ArrayList<Column>();
	
	public PstmtPackage(String templet){
		_templet = templet;
	}

	public PstmtPackage addColumn(Column column) {
		_columnList.add(column);
		return this;
	}
	
	public PstmtPackage addColumn(Class<?> type, String value) {
		Column column = new Column(type,value);
		_columnList.add(column);
		return this;
	}
	public PstmtPackage setConnection(Connection conn) throws SQLException {
		_pstmt = conn.prepareStatement(_templet);
		return this;
	}
	
	public boolean close() {
		try {
			_pstmt.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	
	public void throwNullConnection() {
		throw new NullPointerException("connection 객체를 지정하지 않았습니다. setConnection메서드를 실행 해 주세요.");
	}
	
	public boolean update(SQLBean bean) {
		ArrayList<SQLBean> list = new ArrayList<SQLBean>();
		list.add(bean);
		return update(list);
	}
	
	public boolean update(ArrayList<? extends SQLBean> beans) {
		Iterator<? extends SQLBean> beanIter = beans.iterator();
		while (beanIter.hasNext()) {
			SQLBean bean = (SQLBean) beanIter.next();
			addBatch(bean);
		}
		
		try {
			_pstmt.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;

	}

	private boolean addBatch(SQLBean bean) {
		try {
			setPstmtParmeter(_columnList,_pstmt,bean);
			_pstmt.addBatch();
			_pstmt.clearParameters();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("preparedStatement 업데이트 쿼리 처리 중 예외 발생");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void setPstmtParmeter(ArrayList<Column> columnList, PreparedStatement pstmt, SQLBean bean) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		// TODO Auto-generated method stub
		int size = columnList.size();
		for(int i = 0;i < size;i++) {
			Column column = columnList.get(i);
			Object value = column.getBeanValue(bean);
			Method pstmtSetMethod = column.getPstmtSetMethod(pstmt);
			pstmtSetMethod.invoke(pstmt,i+1,value);
		}
	}

}
