package banip.bean.support;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;

import banip.bean.CategoryBean;
import banip.bean.SQLBean;
import banip.data.StatusCode;
import banip.util.BoardJSON;

public class BeanList<T extends SQLBean>{
	ArrayList<T> _list;

	
	public BeanList(ArrayList<T> list){
		_list =list;
	}
	
	public BeanList(ResultSet rs, Class<T> classType) throws InstantiationException, IllegalAccessException, SQLException{
		_list = new ArrayList<T>();
		while( rs.next() ) {
			T bean = classType.newInstance();
			bean.setFieldAll(rs);
			_list.add(bean);
		}
		
	}
	
	public JSONArray getJSONArray() {
		JSONArray jsonArray = new JSONArray();
		Iterator<T> iter = _list.iterator();
		while (iter.hasNext()) {
			T sqlBean = iter.next();
			jsonArray.add(sqlBean.getJSON());
		}
		return jsonArray;
	}
	
	public BoardJSON getBoardJSON() {
		BoardJSON boardJSON = new BoardJSON(StatusCode.STATUS_SUCCESS);
		boardJSON.putData("list", getJSONArray());
		return boardJSON;
	}

	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return _list.iterator();
	}
	
}
